package dalbridt.petjava.flightservice;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Main {
    public static void main(String[] args) {
        List<Flight> flights = FlightBuilder.createFlights();
        List <Flight> flights2 = new ArrayList<>(flights);


        FlightService service = new FlightService(new TransferTimeFilter(2),
                new InconsistentDateFlightFilter(),
                new DepartedFlightFilter(LocalDateTime.of(2025, 2, 9, 19, 0, 0)));

        service.printFlights(flights, "before");

        List<Flight> filteredFlights = service.filter(flights);
        service.printFlights(filteredFlights, "after");

        List<Flight> filteredFlights2 = service.filter_collections(flights2);
        service.printFlights(filteredFlights2, "after - 2 ");

    }
}