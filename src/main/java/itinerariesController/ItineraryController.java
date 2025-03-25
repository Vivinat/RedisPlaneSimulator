package itinerariesController;

import routeControllers.RouteMapControllerImpl;
import routes.Route;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;
import java.util.stream.Collectors;

public class ItineraryController {


    public static List<Integer> AssignRoute(int startRouteId, int itineraryLength, List<Route> WorldMap){
        List <Integer> itineraryMap = new ArrayList<>();

        Random random = new Random();
        if (WorldMap.isEmpty()){
            throw new RuntimeException("Empty route map");
        }
        Route startRoute = WorldMap.get(startRouteId);
        itineraryMap.add(startRoute.routeId);
        Route currentRoute = WorldMap.get(startRouteId);

        for (Route chave : WorldMap) {
            System.out.printf("Route: %d connected with %s%n", chave.routeId, chave.connections);
        }

        while (itineraryLength != 0) {
            List<Integer> availableConnections = currentRoute.connections.stream()
                    .filter(conn -> !itineraryMap.contains(conn))
                    .collect(Collectors.toList());

            if (availableConnections.isEmpty()) {
                System.out.println("No connection available. Stopping itinerary");
                break;
            }

            int generatedConnectonId = availableConnections.get(random.nextInt(availableConnections.size()));

            itineraryMap.add(generatedConnectonId);

            currentRoute = WorldMap.get(generatedConnectonId);

            itineraryLength--;
        }

        return itineraryMap;
    }

}
