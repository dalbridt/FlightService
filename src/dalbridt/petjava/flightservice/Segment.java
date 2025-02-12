package dalbridt.petjava.flightservice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Bean that represents a flight segment.
 */
public class Segment {
    private final LocalDateTime departureDate;

    private final LocalDateTime arrivalDate;

    private String departureAirport;
    private String arrivalAirport;
    private String flightNo;

    public Segment(final LocalDateTime dep, final LocalDateTime arr) {
        this.departureDate = Objects.requireNonNull(dep);
        this.arrivalDate = Objects.requireNonNull(arr);
    }

    public Segment(final LocalDateTime dep, final LocalDateTime arr, String depar, String arri, String flNo) {
        this.departureDate = Objects.requireNonNull(dep);
        this.arrivalDate = Objects.requireNonNull(arr);
        this.flightNo = flNo;
        this.departureAirport = depar;
        this.arrivalAirport = arri;
    }

    public LocalDateTime getDepartureDate() {
        return departureDate;
    }

    public LocalDateTime getArrivalDate() {
        return arrivalDate;
    }

    @Override
    public String toString() {
        DateTimeFormatter fmt =
                DateTimeFormatter.ofPattern("dd MMMM , HH:mm ");
        return '[' + departureAirport + " " + departureDate.format(fmt) + "| " + arrivalAirport + " " + arrivalDate.format(fmt)
               + ']';
    }

    public void setDepartureAirport(final String airport) {
        departureAirport = airport;
    }

    public void setArrivalAirport(final String airport) {
        arrivalAirport = airport;
    }

    public String getDepartureAirport() {
        return departureAirport;
    }

    public String getArrivalAirport() {
        return arrivalAirport;
    }

    public void setFlightNo(final String flightNo) {
        this.flightNo = flightNo;
    }

    String getFlightNo() {
        return flightNo;
    }
}
