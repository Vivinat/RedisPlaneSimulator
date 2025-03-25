package planes;

import itineraries.Itinerary;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPubSub;
import routeControllers.RouteMapControllerImpl;
import routes.Route;

import java.util.List;
import java.util.Optional;

public class Plane {

    public String planeID;
    public Route currentRoute;
    public boolean waitingForLanding;
    public boolean waitingForItinerary;
    private Itinerary itinerary;
    private List<Route> worldMap;
    private CommandTowerListener commandTowerListener;
    private Thread commandTowerThread;
    private final Object lock = new Object();
    public boolean hasLanded = false;
    private static final String REDIS_HOST = "127.0.0.1";
    private static final int REDIS_PORT = 6379;

    public Plane(String planeID, int startingRouteId, Itinerary itinerary, List<Route> worldMap) {
        this.planeID = planeID;
        currentRoute = worldMap.get(startingRouteId);
        currentRoute.city.planesOnCity.add(this);
        this.itinerary = itinerary;
        waitingForItinerary = false;
        waitingForLanding = true;
        this.worldMap = worldMap;
    }

    public void SetOff(){
        currentRoute.city.planesOnCity.remove(this);
        currentRoute.city.NotifyLeavingAirport();
        RemovingCommandTower();
        waitingForLanding = true;
        hasLanded = false;
    }

    public void SetItinerary(Itinerary itinerary){
        if (!waitingForItinerary){
            System.out.println("Cannot set. Plane is not waiting for a new itinerary");
            return;
        }
        this.itinerary = itinerary;
        waitingForItinerary = false;
    }

    public void Fly(){

        if (itinerary.itineraryMap.isEmpty()) {
            throw new RuntimeException("Itinerary is empty, no more routes to fly.");
        }

        if (itinerary.itineraryMap.size() != 1){
            itinerary.itineraryMap.remove(0);
        }

        Optional<Route> currentRouteOpt = worldMap.stream()
                .filter(rt -> rt.routeId == itinerary.itineraryMap.get(0))
                .findFirst();

        if (currentRouteOpt.isEmpty()){throw new RuntimeException("no route found");}

        System.out.printf("\nPlane %s flew from %d to %d", this.planeID, this.currentRoute.routeId, currentRouteOpt.get().routeId);
        this.currentRoute = currentRouteOpt.get();
        if (currentRoute.routeId == itinerary.destinationId) {
            Landing();
            if (!waitingForLanding){
                waitingForItinerary = true;
                System.out.printf("Plane %s arrived at destination", this.planeID);
                RemovingCommandTower();
            }
        }else{
            SetOff();
        }
    }


    private void Landing() {
        ListenToCommandTower();

        try {
            Thread.sleep(100);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }

        if (!hasLanded){
            PublishToCommandTower("Requesting landing-" + this.planeID, currentRoute.city.commandTowerChannelId);
        }

        synchronized (lock) {
            long timeoutMillis = 10000;
            long startTime = System.currentTimeMillis();

            while (waitingForLanding && (System.currentTimeMillis() - startTime) < timeoutMillis) {
                try {
                    lock.wait(timeoutMillis);
                } catch (InterruptedException e) {
                    Thread.currentThread().interrupt();
                }
                if (!waitingForLanding){
                    break;
                }
            }

            if (waitingForLanding) {
                System.out.println("Landing timeout for plane " + planeID + ". No authorization from command tower.");
                return;
            }
        }

        if (!waitingForLanding) {
            hasLanded = true;
            System.out.println("Plane " + planeID + " landed successfully.");
            currentRoute.city.planesOnCity.add(this);
            System.out.println("Plane " + planeID + " added to city " + currentRoute.city.name);
        }
    }


    private void ListenToCommandTower() {
        commandTowerListener = new CommandTowerListener();
        commandTowerThread = new Thread(() -> {
            try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
                jedis.subscribe(commandTowerListener, currentRoute.city.commandTowerChannelId); // Inscreve no canal da cidade
            } catch (Exception e) {
                System.err.println("Error subscribing to command tower: " + e.getMessage());
            }
        });
        commandTowerThread.start();
    }

    private void PublishToCommandTower(String message, String cityChannelId) {
        try (Jedis jedis = new Jedis(REDIS_HOST, REDIS_PORT)) {
            System.out.println("\nPlane " + planeID + " published on channel " + cityChannelId + "of the city " + currentRoute.city.name + ": " + message);
            if (!waitingForLanding || !hasLanded){
                jedis.publish(cityChannelId, message);
            }
        }
    }

    private class CommandTowerListener extends JedisPubSub {
        @Override
        public void onMessage(String channel, String message) {

            String[] parts = message.split("-");
            String subject = parts[0];
            String planeId = parts[1];

            if (planeId.equals(planeID) && subject.contains("Authorized")) {
                System.out.println("\nPlane " + planeID + " authorized to land.");
                synchronized (lock) {
                    waitingForLanding = false; // Libera o lock, pois o avião foi autorizado
                    lock.notifyAll(); // Notifica que a autorização foi recebida
                }
                } else if (subject.contains("Unauthorized")) {
                    System.out.println("\nPlane " + planeID + " not authorized to land.");
                    waitingForLanding = true;
                }
            }
    }

    public void RemovingCommandTower() {
        if (commandTowerListener != null && commandTowerThread != null && commandTowerThread.isAlive()) {
            commandTowerListener.unsubscribe();
            try {
                commandTowerThread.join();
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

}
