package dalbridt.petjava.flightservice;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Objects;

/**
 * Bean that represents a flight segment.
 */
public class Segment {
    private LocalDateTime departureDate;

    private LocalDateTime arrivalDate;

    private String departureAirport;
    private String arrivalAirport;
    private String flightNo;
    private int flightId;
    private String aircraftCode; // todo доделать использование

   public Segment(String departureAirport, String arrivalAirport) {
       this.departureAirport = departureAirport;
       this.arrivalAirport = arrivalAirport;
   }

    public Segment(final LocalDateTime dep, final LocalDateTime arr) {
        this.departureDate = Objects.requireNonNull(dep);
        this.arrivalDate = Objects.requireNonNull(arr);
    }

    public Segment(final LocalDateTime dep, final LocalDateTime arr, String depar, String arri) {
        this.departureDate = Objects.requireNonNull(dep);
        this.arrivalDate = Objects.requireNonNull(arr);
        this.departureAirport = depar;
        this.arrivalAirport = arri;
    }

    public Segment(final LocalDateTime dep, final LocalDateTime arr, String depar, String arri, String flNo, int flId, String aircraftCode) {
        this.departureDate = Objects.requireNonNull(dep);
        this.arrivalDate = Objects.requireNonNull(arr);
        this.flightNo = flNo;
        this.departureAirport = depar;
        this.arrivalAirport = arri;
        this.flightId = flId;
        this.aircraftCode = aircraftCode;
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
                DateTimeFormatter.ofPattern("yy dd MMMM , HH:mm ");
        return flightNo + ' ' +'[' + departureAirport + " " + departureDate.format(fmt) + "| " + arrivalAirport + " " + arrivalDate.format(fmt)
               + ']';
    }

    public void setDepartureAirport(final String airport) {
        departureAirport = airport;
    }

    public void setArrivalAirport(final String airport) {
        arrivalAirport = airport;
    }

    public void setDepartureDate(LocalDateTime date) {
       this.departureDate = date;
    }
    public void setArrivalDate(String date) {
       this.arrivalDate = LocalDateTime.parse(date);
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

    public int getFlightId() {
        return flightId;
    }

    public void setFlightId(int flightId) { // db does automaticly
        this.flightId = flightId;
    }
}
