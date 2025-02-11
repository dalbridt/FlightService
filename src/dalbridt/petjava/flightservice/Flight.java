package dalbridt.petjava.flightservice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


/**
 * Bean that represents a flight.
 */
public class Flight {
    private final List<Segment> segments;

    Flight(final List<Segment> segs) {
        segments = segs;
    }

    List<Segment> getSegments() {
        return segments;
    }

    @Override
    public String toString() {
        return segments.stream().map(Object::toString)
                .collect(Collectors.joining(" "));
    }
}

/**
 * Bean that represents a flight segment.
 */
class Segment {
    private final LocalDateTime departureDate;

    private final LocalDateTime arrivalDate;

    private String departureAirport;
    private String arrivalAirport;
    private String flightNo;

    Segment(final LocalDateTime dep, final LocalDateTime arr) {
        departureDate = Objects.requireNonNull(dep);
        arrivalDate = Objects.requireNonNull(arr);
    }
    Segment(final LocalDateTime dep, final LocalDateTime arr, String depar, String arri, String flNo) {
        departureDate = Objects.requireNonNull(dep);
        arrivalDate = Objects.requireNonNull(arr);
        flightNo = flNo;
        departureAirport = depar;
        arrivalAirport = arri;
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
                DateTimeFormatter.ofPattern("dd MMMM , HH:mm ");
        return '[' + departureAirport + " " + departureDate.format(fmt) + "| " + arrivalAirport + " " + arrivalDate.format(fmt)
                + ']';
    }

    void setDepartureAirport(final String airport) {
        departureAirport = airport;
    }
    void setArrivalAirport(final String airport) {
        arrivalAirport = airport;
    }
    String getDepartureAirport() {
        return departureAirport;
    }
    String getArrivalAirport() {
        return arrivalAirport;
    }

    void setFlightNo(final String flightNo) {
        this.flightNo = flightNo;
    }
    String getFlightNo() {
        return flightNo;
    }
}