import java.time.LocalDateTime;
import java.util.function.Predicate;

public class InconsistentDateFlightFilter implements Predicate<Flight> { // TODO check if 2 equal dates
    @Override
    public boolean test(Flight flight) {
        for (Segment segment : flight.getSegments()) {
            if(segment.getArrivalDate().isBefore(segment.getDepartureDate()) || segment.getDepartureDate().isEqual(segment.getArrivalDate())) {
                return false;
            }
        }
        return true;
    }
}
