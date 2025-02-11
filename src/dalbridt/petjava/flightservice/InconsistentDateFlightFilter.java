package dalbridt.petjava.flightservice;

import java.util.function.Predicate;

public class InconsistentDateFlightFilter implements Predicate<Route> { // TODO check if 2 equal dates
    @Override
    public boolean test(Route route) {
        for (Flight flight : route.getSegments()) {
            if (flight.getArrivalDate().isBefore(flight.getDepartureDate()) || flight.getDepartureDate().isEqual(flight.getArrivalDate())) {
                return false;
            }
        }
        return true;
    }
}
