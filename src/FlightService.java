import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.function.Predicate;
import java.util.stream.Stream;

public class FlightService {
    private List<Predicate<Flight>> filterParams;
    public FlightService(){}
    public FlightService(List<Predicate<Flight>> params) {
        this.filterParams = filterParams;
    }
    @SafeVarargs
    public FlightService(Predicate<Flight> ...params) {
        this.filterParams = Arrays.asList(params);
    }
    public void setFilterParams (List <Predicate <Flight>> params) {
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
       // переменное кол-во аргументов
       Predicate <Flight> assembledFilter = filterParams.stream().reduce(Predicate::and).orElse(fl -> true);
        return flights
                .stream()
                .filter(assembledFilter)
                .toList();
    }
}
