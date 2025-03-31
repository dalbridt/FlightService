package dalbridt.petjava.flightservice;

import java.time.LocalTime;
import java.util.function.Predicate;

public class DepartureTimeFilter implements Predicate<Flight> {
    private LocalTime noEarlierThan;

    public DepartureTimeFilter(LocalTime noEarlierThan) {
        this.noEarlierThan = noEarlierThan;

    }

    @Override
    public boolean test(Flight flight) {
        LocalTime departureTime = flight.getSegments().getFirst().getDepartureDate().toLocalTime();
        return departureTime.isAfter(noEarlierThan) || departureTime.equals(noEarlierThan);
    }
}
