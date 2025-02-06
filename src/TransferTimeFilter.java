import java.time.Duration;
import java.time.LocalDateTime;
import java.util.function.Predicate;

public class TransferTimeFilter implements Predicate <Flight> {
    private Duration maxDuration;
    public TransferTimeFilter(int hoursFilterThreshold ) {
        this.maxDuration = Duration.ofHours(hoursFilterThreshold);
    }
    public boolean test(Flight flight) {
        Duration transferTime = Duration.ZERO;
        LocalDateTime ariv1 = null;
        for (Segment segment : flight.getSegments()){
            if (ariv1 != null) {
                transferTime = transferTime.plus(Duration.between(ariv1, segment.getDepartureDate()));
            }
            ariv1 = segment.getArrivalDate();
        }
//        System.out.println("we counted transfer time and it's " + transferTime.toHours() + ", condition is " + (transferTime.compareTo(Duration.ofHours(2)) < 0));
        return transferTime.compareTo(maxDuration) < 0;
    }
}
