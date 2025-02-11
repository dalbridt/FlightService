package dalbridt.petjava.flightservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

public class FlightServiceTest {
    private FlightService service;
    private List<Route> itineraries;

    @BeforeEach
    void setUp() {
        service = new FlightService();
        itineraries = FlightBuilder.createFlights();
    }

    @Test
    void myFirstTest(){
        List <Predicate<Route>> filters = Arrays.asList(
                new TransferTimeFilter(2),
                new InconsistentDateFlightFilter(),
                new DepartedFlightFilter(LocalDateTime.of(2025, 2, 9, 19, 0, 0))
        );
        service.setFilterParams(filters);
        List <Route> filteredItineraries = service.filter(itineraries);
        assertEquals(3, filteredItineraries.size());
    }

    @Test
    void mySecTest(){
        List <Predicate<Route>> filters = Arrays.asList(
                new TransferTimeFilter(2),
                new InconsistentDateFlightFilter(),
                new DepartedFlightFilter(LocalDateTime.of(2025, 2, 9, 19, 0, 0))
        );
        service.setFilterParams(filters);
        List <Route> filteredItineraries = service.filter(itineraries);
        assertEquals(3, filteredItineraries.size());
    }

}
