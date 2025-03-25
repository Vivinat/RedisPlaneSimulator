package PlaneTests;

import itineraries.Itinerary;
import org.junit.Test;
import planes.Plane;
import routes.Route;

import java.util.Optional;

import static org.junit.Assert.*;

public class PlaneTests extends PlaneTestsBase {


    @Test
    public void Plane_SetOffRemovesPlaneFromCity_NullCity(){

        Itinerary itinerary = new Itinerary(0,2, routeMapController.getRouteList());
        Plane plane = new Plane("123", 0, itinerary, routeMapController.getRouteList());
        Optional <Route> expectedRoute = routeMapController.getRouteById(itinerary.itineraryMap.get(0));
        if (expectedRoute.isEmpty()){
            fail("Route not found.");
        }

        plane.SetOff();

        assertTrue(expectedRoute.get().city.planesOnCity.isEmpty());
    }

    @Test
    public void Plane_ExpectedCreation_CityWithOnePlane(){
        Itinerary itinerary = new Itinerary(0,2, routeMapController.getRouteList());
        Plane plane = new Plane("123", 0, itinerary, routeMapController.getRouteList());

        Optional <Route> expectedRoute = routeMapController.getRouteById(itinerary.itineraryMap.get(0));
        if (expectedRoute.isEmpty()){
            fail("Route not found.");
        }

        assertEquals(1, expectedRoute.get().city.planesOnCity.size());
    }

    @Test
    public void Plane_FlyToItineraryNextPosition_PlaneOnNewRoute(){
        Itinerary itinerary = new Itinerary(0,2, routeMapController.getRouteList());
        Plane plane = new Plane("123", 0, itinerary, routeMapController.getRouteList());

        Optional <Route> expectedRoute = routeMapController.getRouteById(itinerary.itineraryMap.get(1));
        if (expectedRoute.isEmpty()){
            fail("Route not found.");
        }

        plane.SetOff();
        plane.Fly();
        assertEquals(expectedRoute.get().routeId, plane.currentRoute.routeId);
    }

    @Test
    public void City_PlaneLeavesAndRemovesItselfFromPreviousCity_PreviousCityIsEmpty(){
        Itinerary itinerary = new Itinerary(0,3, routeMapController.getRouteList());
        Plane plane = new Plane("123", 0, itinerary, routeMapController.getRouteList());

        Optional <Route> expectedRoute = routeMapController.getRouteById(itinerary.itineraryMap.get(1));
        if (expectedRoute.isEmpty()){
            fail("Destination not found.");
        }

        plane.SetOff();
        plane.Fly();
        plane.Fly();
        assertFalse(expectedRoute.get().city.planesOnCity.contains(plane));
    }

    @Test
    public void Plane_ArriveAtDestination_PlaneOnDestinyCityList() throws Exception {

        Itinerary itinerary = new Itinerary(0,2, routeMapController.getRouteList());
        Plane plane = new Plane("123", 0, itinerary, routeMapController.getRouteList());

        int retries = 5;
        int delay = 500;

        plane.SetOff();
        plane.Fly();
        plane.Fly();

        WaitForLanding(plane,retries,delay);

        Optional <Route> destination = routeMapController.getRouteById(itinerary.destinationId);
        if (destination.isPresent()){
            assertTrue(routeMapController.getRouteById(itinerary.destinationId).get().city.planesOnCity.contains(plane));
        }else{
            fail("Destination not found.");
        }
    }

    @Test
    public void Plane_ArriveAtDestinationHasLandedWaitingForItinerary_PlaneHasLandedTrue() throws Exception {

        Itinerary itinerary = new Itinerary(0,2, routeMapController.getRouteList());
        Plane plane = new Plane("123", 0, itinerary, routeMapController.getRouteList());

        int retries = 5;
        int delay = 500;

        plane.SetOff();
        plane.Fly();
        plane.Fly();

        WaitForLanding(plane,retries,delay);

        Optional <Route> destination = routeMapController.getRouteById(itinerary.destinationId);
        if (destination.isPresent()){
            assertTrue(plane.hasLanded);
        }else{
            fail("Destination not found.");
        }
    }

    @Test
    public void Plane_ArriveAtDestinationLandedAwaitingForItinerary_PlaneWaitingForItineraryTrue() throws Exception {

        Itinerary itinerary = new Itinerary(0,2, routeMapController.getRouteList());
        Plane plane = new Plane("123", 0, itinerary, routeMapController.getRouteList());

        int retries = 5;
        int delay = 500;

        plane.SetOff();
        plane.Fly();
        plane.Fly();

        WaitForLanding(plane,retries,delay);

        Optional <Route> destination = routeMapController.getRouteById(itinerary.destinationId);
        if (destination.isPresent()){
            assertTrue(plane.waitingForItinerary);
        }else{
            throw new Exception(new Exception("destination not found."));
        }
    }

    @Test
    public void Plane_CannotArriveAtDestination_DestinyCityAirportFull() throws Exception {

        Itinerary itinerary = new Itinerary(0,2, routeMapController.getRouteList());
        Plane plane = new Plane("123", 0, itinerary, routeMapController.getRouteList());
        Plane plane2 = new Plane("456", 0, itinerary, routeMapController.getRouteList());

        int retries = 5;
        int delay = 500;

        plane.SetOff();
        plane.Fly();
        plane.Fly();

        plane2.SetOff();
        plane2.Fly();
        plane2.Fly();

        WaitForLanding(plane,retries,delay);

        assertTrue(plane2.waitingForLanding);
    }

    @Test
    public void Plane_ArrivesAndLeavesForAnotherPlaneToLand_SecondPlaneLands() throws Exception {

        Itinerary itinerary = new Itinerary(0,2, routeMapController.getRouteList());
        Itinerary itinerary2 = new Itinerary(0,2, routeMapController.getRouteList());
        Plane plane = new Plane("123", 0, itinerary, routeMapController.getRouteList());
        Plane plane2 = new Plane("456", 0, itinerary, routeMapController.getRouteList());

        int retries = 5;
        int delay = 500;

        plane.SetOff();
        plane.Fly();
        plane.Fly();

        plane2.SetOff();
        plane2.Fly();
        plane2.Fly();

        WaitForLanding(plane,retries,delay);

        plane.SetItinerary(itinerary2);
        plane.SetOff();
        plane.Fly();

        assertFalse(plane2.waitingForLanding);
    }

    @Test
    public void Plane_CompletesTwoItineraries_PlaneLandsInTwoCities() throws Exception {

        Itinerary itinerary = new Itinerary(0,2, routeMapController.getRouteList());
        Optional <Route> destiny = routeMapController.getRouteById(itinerary.destinationId);
        Itinerary itinerary1 = new Itinerary(itinerary.destinationId,2, routeMapController.getRouteList());
        Optional <Route> destiny1= routeMapController.getRouteById(itinerary1.destinationId);
        Plane plane = new Plane("123", 0, itinerary, routeMapController.getRouteList());

        boolean firstCityLanding = false;
        boolean secondCityLanding = false;
        boolean succesfullCitiesLanding = false;

        if (destiny.isEmpty() || destiny1.isEmpty()){
            fail("Destination not found.");
        }

        int retries = 5;
        int delay = 500;

        plane.SetOff();
        plane.Fly();
        plane.Fly();

        WaitForLanding(plane,retries,delay);

        if (destiny.get().city.planesOnCity.contains(plane)){
            firstCityLanding = true;
        }

        plane.SetItinerary(itinerary1);
        plane.SetOff();
        plane.Fly();
        plane.Fly();

        WaitForLanding(plane,retries,delay);

        if (destiny1.get().city.planesOnCity.contains(plane)) {
            secondCityLanding = true;
        }
        if (firstCityLanding && secondCityLanding){
            succesfullCitiesLanding = true;
        }

        assertTrue(succesfullCitiesLanding);
    }

    @Test
    public void City_CityNotEmptyBeforePlaneArrives_CityFullCapacity() throws Exception {

        Itinerary itinerary = new Itinerary(0,2, routeMapController.getRouteList());
        Plane plane = new Plane("123", 0, itinerary, routeMapController.getRouteList());

        Optional <Route> destination = routeMapController.getRouteById(itinerary.destinationId);
        boolean emptyBeforePlane = false;
        boolean fullAfterPlane = false;
        boolean succesfullCapacity = false;

        if (destination.isEmpty()){
            throw new Exception(new Exception("destination not found."));
        }

        if (destination.get().city.planesOnCity.isEmpty()){
            emptyBeforePlane = true;
        }

        int retries = 5;
        int delay = 500;

        plane.SetOff();
        plane.Fly();
        plane.Fly();

        WaitForLanding(plane,retries,delay);

        if (destination.get().city.planesOnCity.size() == 1){
            fullAfterPlane = true;
        }
        if (fullAfterPlane && emptyBeforePlane){
            succesfullCapacity = true;
        }
        assertTrue(succesfullCapacity);
    }

    @Test
    public void Itinerary_ItineraryCorrectConnections_ConnectionsBetweenRoutesCorrect(){
        Itinerary itinerary = new Itinerary(0,2, routeMapController.getRouteList());
        boolean firstConnection = false;
        boolean secondConnection = false;
        boolean thirdConnection = false;
        boolean successfullConnections = false;

        Optional <Route> firstRoute = routeMapController.getRouteById(itinerary.itineraryMap.get(0));
        Optional <Route> secondRoute = routeMapController.getRouteById(itinerary.itineraryMap.get(1));
        Optional <Route> thirdRoute = routeMapController.getRouteById(itinerary.itineraryMap.get(2));

        if (firstRoute.isEmpty() || secondRoute.isEmpty() || thirdRoute.isEmpty()){
            fail("Routes not found");
        }

        if (firstRoute.get().connections.contains(secondRoute.get().routeId)){
            firstConnection = true;
        }
        if (secondRoute.get().connections.contains(firstRoute.get().routeId)
        && secondRoute.get().connections.contains(thirdRoute.get().routeId)){
            secondConnection = true;
        }
        if (thirdRoute.get().connections.contains(secondRoute.get().routeId)){
            thirdConnection = true;
        }

        if (firstConnection && secondConnection && thirdConnection){
            successfullConnections = true;
        }
        assertTrue(successfullConnections);
    }

    @Test
    public void City_RedisDBReturnPlanePreviousAuthorization_CityAuthorizedPlane() throws Exception {

        Itinerary itinerary = new Itinerary(0,2, routeMapController.getRouteList());
        Plane plane = new Plane("123", 0, itinerary, routeMapController.getRouteList());

        int retries = 5;
        int delay = 500;

        plane.SetOff();
        plane.Fly();
        plane.Fly();

        WaitForLanding(plane,retries,delay);

        Optional <Route> destination = routeMapController.getRouteById(itinerary.destinationId);
        if (destination.isPresent()){
            assertTrue(destination.get().city.retrievePlaneAuthorization(plane.planeID));
        }else{
            throw new Exception(new Exception("destination not found."));
        }
    }

    private void WaitForLanding(Plane plane, int retries, int delay) throws InterruptedException {
        while (retries > 0) {
            if (plane.hasLanded) {
                break;
            }
            Thread.sleep(delay);
            retries--;
        }
    }


}
