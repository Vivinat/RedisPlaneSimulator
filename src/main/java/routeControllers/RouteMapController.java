package routeControllers;

import routes.Route;

import java.util.List;
import java.util.Optional;

public interface RouteMapController {

    void createRoutes(int numberOfRoutes, int minimunAirplaneCap, int maxAirplaneCap);

    List<Route> getRouteList();

    Optional<Route> getRouteById(int routeId);

}
