package dalbridt.petjava.flightservice;

import java.time.LocalDateTime;
import java.util.function.Predicate;

public class DepartedFlightFilter implements Predicate<Flight> { // reference time instead of now
    private LocalDateTime whenDeparted;

    public DepartedFlightFilter(LocalDateTime whenDeparted) {
        this.whenDeparted = whenDeparted;
    }

    @Override
    public boolean test(Flight flight) {
        for (Segment segment : flight.getSegments()) {
            if (segment.getDepartureDate().isBefore(whenDeparted)) {
                return false;
            }
        }

        return true;
    }
}
