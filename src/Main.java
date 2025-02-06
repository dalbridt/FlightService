import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;
import java.util.function.Predicate;

public class Main {
    public static void main(String[] args) {
       List<Flight> flights = FlightBuilder.createFlights();

        FlightService service = new FlightService (new TransferTimeFilter(2),
                new InconsistentDateFlightFilter(),
                new DepartedFlightFilter(LocalDateTime.of(2025, 1, 9, 19, 0, 0)));
        List <Flight> filteredFlights = service.filter(flights);

       int []count  = {1};

        flights.forEach(f -> {
            System.out.println(count[0]++ + " " + f);
        });

        System.out.println(" --- filtered: --- ");

        filteredFlights.forEach(f -> {
            System.out.println(count[0]++ + " " + f);
        });
        System.out.println(" --- param pam pams: --- ");
        System.out.println(service.getFilterParams());
    }
}