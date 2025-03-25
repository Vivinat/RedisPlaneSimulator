package routeControllers;

import cities.City;
import routes.Route;

import java.util.*;

public class RouteMapControllerImpl implements RouteMapController{

    public List <Route> routeList = new ArrayList<>();
    private int idCounter = 0;
    private int cityNameLength = 4;

    @Override
    public void createRoutes(int numberOfRoutes, int minimumAirplaneCap, int maxNumberAirplaneCap) {
        Random random = new Random();

        for (int i = 0; i < numberOfRoutes; i++){
            int id = idCounter++;
            routeList.add(new Route(id,new City(UUID.randomUUID().toString().substring(random.nextInt(minimumAirplaneCap, maxNumberAirplaneCap),cityNameLength), 1, id),random.nextInt(idCounter + 1)));
        }
        createRouteMap();
    }

    private void createRouteMap() {
        Random random = new Random();
        HashMap<Integer, List<Integer>> connectionsDict = new HashMap<>();

        for (int i = 0; i < routeList.size(); i++) {
            int connectionChance = 4;

            for (int j = i + 1; j < routeList.size(); j++) {
                if (connectionChance > 1) {
                    connectionsDict.putIfAbsent(routeList.get(i).routeId, new ArrayList<>());
                    connectionsDict.get(routeList.get(i).routeId).add(routeList.get(j).routeId);
                    routeList.get(i).SetRouteConnection(j);
                    routeList.get(j).SetRouteConnection(i);
                }
                connectionChance = random.nextInt(0, 5);
            }

            // At least one connection must happen
            if (!connectionsDict.containsKey(routeList.get(i).routeId) && i + 1 < routeList.size()) {
                connectionsDict.putIfAbsent(routeList.get(i).routeId, new ArrayList<>());
                connectionsDict.get(routeList.get(i).routeId).add(routeList.get(i + 1).routeId);
                routeList.get(i).SetRouteConnection(i+1);
                routeList.get(i+1).SetRouteConnection(i);
            }
        }
    }

    public List<Route> getRouteList() {
        return routeList;
    }

    public Optional<Route> getRouteById(int routeId){
        return routeList.stream()
                .filter(rt -> rt.routeId == routeId)
                .findFirst();
    }
}
