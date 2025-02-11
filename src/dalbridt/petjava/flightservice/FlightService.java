package dalbridt.petjava.flightservice;

import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.function.Predicate;

public class FlightService {
    private List<Predicate<Route>> filterParams;
    public FlightService() {} // TODO create empty constructor for beforeeach method in tests?
    public FlightService(List<Predicate<Route>> params) {
        this.filterParams = filterParams;
    }

    @SafeVarargs
    public FlightService(Predicate<Route>... params) {
        this.filterParams = Arrays.asList(params);
    }

    public void setFilterParams(List<Predicate<Route>> params) {
        this.filterParams = params;
    }

    public String getFilterParams() {
        StringBuilder builder = new StringBuilder();
        for (Predicate<Route> p : filterParams) {
            builder.append(p.getClass());
            builder.append(" | ");
        }
        return builder.toString();
    }

    public List<Route> filter(List<Route> routes) {//
        Predicate<Route> assembledFilter = filterParams.stream().reduce(Predicate::and).orElse(fl -> true);
        return routes
                .stream()
                .filter(assembledFilter)
                .toList();
    }

    public List<Route> filter_collections(List<Route> routes) {
        for(Predicate<Route> p : filterParams) {
            Iterator<Route> it = routes.iterator();
            while(it.hasNext()) {
                if(!p.test(it.next())) {
                    it.remove();
                }
            }
        }
        return routes;
    }

    public void printFlights(List<Route> routes, String header) {
        System.out.println("--- " + header + " ---");
        int[] count = {1};

        routes.forEach(f -> {
            System.out.println(count[0]++ + " " + f);
        });

    }
}
