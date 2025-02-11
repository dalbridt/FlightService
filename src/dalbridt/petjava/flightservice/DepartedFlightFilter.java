package dalbridt.petjava.flightservice;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public class DepartedFlightFilter implements Predicate<Route> { // reference time instead of now
    private LocalDateTime whenDeparted;

    public DepartedFlightFilter(LocalDateTime whenDeparted) {
        this.whenDeparted = whenDeparted;
    }

    @Override
    public boolean test(Route route) {
        for (Flight flight : route.getSegments()) {
            if (flight.getDepartureDate().isBefore(whenDeparted)) {
                return false;
            }
        }

        return true;
    }
}
