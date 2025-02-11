package dalbridt.petjava.flightservice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Bean that represents a flight.
 */
public class Route {
    private final List<Flight> flights;

    Route(final List<Flight> segs) {
        flights = segs;
    }

    List<Flight> getSegments() {
        return flights;
    }

    @Override
    public String toString() {
        return flights.stream().map(Object::toString)
                .collect(Collectors.joining(" "));
    }
}

/**
 * Bean that represents a flight segment.
 */
class Flight {
    private final LocalDateTime departureDate;

    private final LocalDateTime arrivalDate;

    Flight(final LocalDateTime dep, final LocalDateTime arr) {
        departureDate = Objects.requireNonNull(dep);
        arrivalDate = Objects.requireNonNull(arr);
    }

    LocalDateTime getDepartureDate() {
        return departureDate;
    }

    LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("dd MMMM , HH:mm "); //HH:mm, dd MMMM yyyy yyyy-MM-dd'T'HH:mm
        return '[' + departureDate.format(fmt) + "| " + arrivalDate.format(fmt)
                + ']';
    }
}