package dalbridt.petjava.flightservice;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class FlightDaoService {
    private final BasicDataSource ds;

    private final String validateAirportsQuery = "select count (*) from airports_data\n" + "where airport_code = ? or airport_code = ?";
    private final String setIsActiveQuery = "update flights\n" + "set is_active=?\n" + "where flights.flight_id = ?\n";
    private final String getSeatsAmountQuery = "select count(s.seat_no) as seat_count\n" + "from seats s \n" + "where s.aircraft_code = (select f.aircraft_code from flights f where f.departure_airport = ? \n" + "  and f.arrival_airport = ? limit 1)\n" + "group by s.aircraft_code";
    private final String getFlWTransitQuery = "with f1 as (select distinct flights.departure_airport , flights.arrival_airport  from flights\n" + "where flights.departure_airport = ?), \n" + "f2 as  (select distinct flights.departure_airport , flights.arrival_airport  from flights\n" + "where flights.arrival_airport  = ?)\n" + "select f1.departure_airport, f1.arrival_airport as transit_airport, f2.arrival_airport   from f1\n" + "join f2 on f1.arrival_airport = f2.departure_airport";
    private final String getAllFlightsBetweenPointsQuery = "with f1 as (select distinct on (scheduled_departure::time , arrival_airport) \n" +
                                                           "flight_id, flight_no, scheduled_departure, scheduled_arrival, departure_airport, arrival_airport, aircraft_code \n" +
                                                           "from flights \n" +
                                                           "where flights.departure_airport = ?\n" +
                                                           "order by scheduled_departure::time  asc),\n" +
                                                           "f2 as (select distinct on (scheduled_departure::time, departure_airport) \n" +
                                                           "flight_id, flight_no, scheduled_departure, scheduled_arrival, departure_airport, arrival_airport, aircraft_code \n" +
                                                           "from flights \n" +
                                                           "where flights.arrival_airport  = ?\n" +
                                                           "order by scheduled_departure::time  asc)\n" +
                                                           "select *\n" +
                                                           "from f1\n" +
                                                           "join f2 on f1.arrival_airport = f2.departure_airport";


    private final String addNewFlightQuery = "insert into flights (flight_no, scheduled_departure, scheduled_arrival, departure_airport,arrival_airport, status, aircraft_code)\n" + "values (?, ?, ?, \n" + "?, ?, ?, ?)";
    private final String getFlightByIdQuery = "select * from flights where flight_id = ?";

    public FlightDaoService(BasicDataSource ds) {
        this.ds = ds;
    }

    public boolean validateABpoints(String departure_airport, String arrival_airport) {
        try (PreparedStatement pstmt = ds.getConnection().prepareStatement(validateAirportsQuery)) {
            pstmt.setString(1, departure_airport);
            pstmt.setString(2, arrival_airport);
            try (ResultSet rs = pstmt.executeQuery()) {
                if (rs.next()) {
                    return rs.getInt(1) == 2;
                }
                return false;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void setISactive(boolean isactive, int flightID) {
        try (PreparedStatement pstmt = ds.getConnection().prepareStatement(setIsActiveQuery)) {
            pstmt.setBoolean(1, isactive);
            pstmt.setInt(2, flightID);
            pstmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public int getSeatsAmount(String departure_airport, String arrival_airport) {
        try (PreparedStatement pstmt = ds.getConnection().prepareStatement(getSeatsAmountQuery)) {
            pstmt.setString(1, departure_airport);
            pstmt.setString(2, arrival_airport);
            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                return rs.getInt(1);
            }
            return 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return 0;
        }
    }

    public List<Flight> getFlightsWithTransit(String departure_airport, String arrival_airport) {
        try (PreparedStatement pstmt = ds.getConnection().prepareStatement(getFlWTransitQuery)) {
            pstmt.setString(1, departure_airport);
            pstmt.setString(2, arrival_airport);
            try (ResultSet rs = pstmt.executeQuery()) {
                List<Flight> flightsWithTransit = new ArrayList<>();
                while (rs.next()) {
                    String seg1Dep = rs.getString(1);
                    String seg1Arriv = rs.getString(2);
                    String seg2Dep = rs.getString(2);
                    String seg2Arriv = rs.getString(3);

                    List<Segment> segments = List.of(new Segment(seg1Dep, seg1Arriv), new Segment(seg2Dep, seg2Arriv));
                    Flight flight = new Flight(segments);
                    flightsWithTransit.add(flight);
                }
                return flightsWithTransit;
            }
        } catch (SQLException e) {
            e.printStackTrace();
            return null;
        }
    }

    public List<Flight> getallFlightsBetweenPoints(String departure_airport, String arrival_airport) {
        try (PreparedStatement pstmt = ds.getConnection().prepareStatement(getAllFlightsBetweenPointsQuery)) {
            pstmt.setString(1, departure_airport);
            pstmt.setString(2, arrival_airport);
            try (ResultSet rs = pstmt.executeQuery()) {
                return mapToFlightsWithTransitList(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public Segment getFlightsByFlightId(int flightId) {
        try (PreparedStatement pstmt = ds.getConnection().prepareStatement(getFlightByIdQuery)) {
            pstmt.setInt(1, flightId);
            try (ResultSet rs = pstmt.executeQuery()) {
                return maptoSegment(rs);
            }
        } catch (SQLException e) {
            e.printStackTrace();
            throw new RuntimeException(e.getMessage(), e);
        }
    }

    public boolean addFlight(Segment segment) {
        try (PreparedStatement pstmt = ds.getConnection().prepareStatement(addNewFlightQuery)) {
            pstmt.setString(1, segment.getFlightNo());
            pstmt.setTimestamp(2, Timestamp.valueOf(segment.getDepartureDate()));
            pstmt.setTimestamp(3, Timestamp.valueOf(segment.getArrivalDate()));
            pstmt.setString(4, segment.getDepartureAirport());
            pstmt.setString(5, segment.getArrivalAirport());
            pstmt.setString(6, "Scheduled");
            pstmt.setString(7, "UFO");
            int rows = pstmt.executeUpdate();
            return rows > 0;
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private List<Flight> mapToFlightsWithTransitList(ResultSet rs) throws SQLException {
        // todo в новом запросе не совпадает очередность колонок
        List<Flight> flightsWithTransit = new ArrayList<>();
        while (rs.next()) {
            String seg1Dep = rs.getString(1);
            LocalDateTime seg1DepTime = rs.getTimestamp(2) != null ? rs.getTimestamp(2).toLocalDateTime() : null;
            String seg1Arriv = rs.getString(3);
            LocalDateTime seg1ArrivTime = rs.getTimestamp(4) != null ? rs.getTimestamp(4).toLocalDateTime() : null;
            String seg2Dep = rs.getString(5);
            LocalDateTime seg2DepTime = rs.getTimestamp(6) != null ? rs.getTimestamp(6).toLocalDateTime() : null;
            String seg2Arriv = rs.getString(7);
            LocalDateTime seg2ArrivTime = rs.getTimestamp(8) != null ? rs.getTimestamp(8).toLocalDateTime() : null;

            List<Segment> segments = List.of(new Segment(seg1DepTime, seg1ArrivTime, seg1Dep, seg1Arriv), new Segment(seg2DepTime, seg2ArrivTime, seg2Dep, seg2Arriv));
            Flight flight = new Flight(segments);
            flightsWithTransit.add(flight);
        }
        return flightsWithTransit;
    }

    private Segment maptoSegment(ResultSet rs) throws SQLException {
        System.out.println("🎪🎪maptoSegment -- i'm alive by that point ");
        if (rs.next()) {
            int flightId = rs.getInt(1);
            String flightNo = rs.getString(2);
            LocalDateTime departureDate = rs.getTimestamp(3) != null ? rs.getTimestamp(3).toLocalDateTime() : null; // todo DB has constraint not null
            LocalDateTime arrivalDate = rs.getTimestamp(4) != null ? rs.getTimestamp(4).toLocalDateTime() : null;
            String departureAirport = rs.getString(5);
            String arrivalAirport = rs.getString(6);

            Segment segment = new Segment(departureDate, arrivalDate, departureAirport, arrivalAirport, flightNo);
            segment.setFlightId(flightId);
            return segment;
        } else {
            throw new SQLException("Resultset is empty");
        }
    }


}
