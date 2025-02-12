package dalbridt.petjava.flightservice;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;
import java.util.function.Predicate;

public class FlightService {
    private List<Predicate<Flight>> filterParams;
    public FlightService() {} // TODO create empty constructor for beforeeach method in tests?
    public FlightService(List<Predicate<Flight>> params) {
        this.filterParams = filterParams;
    }

    @SafeVarargs
    public FlightService(Predicate<Flight>... params) {
        this.filterParams = Arrays.asList(params);
    }

    public void setFilterParams(List<Predicate<Flight>> params) {
        this.filterParams = params;
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

    public List<Flight> filter_collections(List<Flight> flights) {
        for(Predicate<Flight> p : filterParams) {
            Iterator<Flight> it = flights.iterator();
            while(it.hasNext()) {
                if(!p.test(it.next())) {
                    it.remove();
                }
            }
        }
        return flights;
    }

    public void printFlights(List<Flight> flights, String header) {
        System.out.println("--- " + header + " ---");
        int[] count = {1};

        flights.forEach(f -> {
            System.out.println(count[0]++ + " " + f);
        });

    }

    public String [] getPpoints(){
        String [] flightAB = new String[2];
        Scanner sc = new Scanner(System.in);
        System.out.println("PLZ enter departure airport code:");
        String dep = sc.nextLine();
        System.out.println("PLZ enter arrival airport code:");
        String arr = sc.nextLine();

        return flightAB;
    }
}
