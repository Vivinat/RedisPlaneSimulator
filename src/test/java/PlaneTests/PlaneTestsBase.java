package PlaneTests;

import org.junit.After;
import org.junit.Before;
import routeControllers.RouteMapControllerImpl;

public class PlaneTestsBase {

    protected RouteMapControllerImpl routeMapController;

    @Before
    public void setUp() {
        if (routeMapController == null) {
            routeMapController = new RouteMapControllerImpl();
        }
        routeMapController.routeList.clear();
        routeMapController.createRoutes(12, 1, 2);
    }

}
