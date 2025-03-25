package itineraries;

import itinerariesController.ItineraryController;
import routes.Route;

import java.util.List;

public class Itinerary {

    public List<Integer> itineraryMap;
    public Integer destinationId;

    public Itinerary(int startRouteId, int itineraryLength, List<Route> worldMap) {
        this.itineraryMap = ItineraryController.AssignRoute(startRouteId, itineraryLength, worldMap);
        destinationId = this.itineraryMap.get(this.itineraryMap.size() - 1);
    }
}
