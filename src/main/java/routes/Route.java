package routes;

import cities.City;

import java.util.ArrayList;
import java.util.List;

public class Route {

    public int routeId;
    public List <Integer> connections;
    public City city;
    public int length;

    public Route(int routeId, City city, int length) {
        this.routeId = routeId;
        this.city = city;
        this.length = length;
        this.connections = new ArrayList<>();
    }

    public void SetRouteConnection(int routeToConnect) {
        this.connections.add(routeToConnect);
    }

}
