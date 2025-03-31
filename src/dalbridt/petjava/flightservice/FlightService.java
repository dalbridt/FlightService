package dalbridt.petjava.flightservice;

import java.util.*;
import java.util.function.Predicate;

public class FlightService {
    private FlightDaoService flightDaoService;
    private List<Predicate<Flight>> filterParams;
    public FlightService() {} // TODO delete constructor or add validation in methods
    public FlightService(List<Predicate<Flight>> params) {
        this.filterParams = params;
    }
    public FlightService(FlightDaoService flightDaoService) {
        this.flightDaoService = flightDaoService;
    }

    @SafeVarargs
    public FlightService(Predicate<Flight>... params) {
        this.filterParams = Arrays.asList(params);
    }



    public void setFilterParams(List<Predicate<Flight>> params) {
        this.filterParams = params;
    }

    public List<Flight> filterFlights(String codeA, String codeB, List<Predicate<Flight>> params){
        // todo
       if( flightDaoService.validateABpoints(codeA, codeB)){
           List <Flight> flights = flightDaoService.getallFlightsBetweenPoints(codeA, codeB);
           setFilterParams(params);
          return filter(flights);
       }
       return null;
    }


    public String getFilterParams() {
        StringBuilder builder = new StringBuilder();
        for (Predicate<Flight> p : filterParams) {
            builder.append(p.getClass());
            builder.append(" | ");
        }
        return builder.toString();
    }

    public List<Flight> filter(List<Flight> flights) {//
        Predicate<Flight> assembledFilter = filterParams.stream().reduce(Predicate::and).orElse(fl -> true);
        return flights
                .stream()
                .filter(assembledFilter)
                .toList();
    }

    public void printFlights(List<Flight> flights, String header) {
        int[] count = {1};

        flights.forEach(f -> {
            System.out.println(count[0]++ + " " + f);
        });

    }

}
