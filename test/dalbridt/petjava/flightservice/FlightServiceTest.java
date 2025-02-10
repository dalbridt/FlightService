package dalbridt.petjava.flightservice;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;


import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;

import static org.junit.jupiter.api.Assertions.*;

public class FlightServiceTest {
    private FlightService service;
    private List<Flight> flights;

    @BeforeEach
    void setUp() {
        service = new FlightService();
        flights = FlightBuilder.createFlights();
    }

    @Test
    void myFirstTest(){
        List <Predicate<Flight>> filters = Arrays.asList(
                new TransferTimeFilter(2),
                new InconsistentDateFlightFilter(),
                new DepartedFlightFilter(LocalDateTime.of(2025, 2, 9, 19, 0, 0))
        );
        service.setFilterParams(filters);
        List <Flight> filteredFlights = service.filter(flights);
        assertEquals(3, filteredFlights.size());
    }

}
