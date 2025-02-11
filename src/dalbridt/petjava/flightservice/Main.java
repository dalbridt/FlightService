package dalbridt.petjava.flightservice;

import java.sql.*;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.util.ArrayList;
import java.util.List;

import static dalbridt.petjava.flightservice.DBConnectProperties.*;

public class Main {
//    public static void main(String[] args) {
//        List<Flight> routes = FlightBuilder.createFlights();
//
//        FlightService service = new FlightService (new TransferTimeFilter(2),
//                new InconsistentDateFlightFilter(),
//                new DepartedFlightFilter(LocalDateTime.of(2025, 1, 9, 19, 0, 0)));
//        List <Flight> filteredFlights = service.filter(routes);
//
//        int []count  = {1};
//
//        routes.forEach(f -> {
//            System.out.println(count[0]++ + " " + f);
//        });
//
//        System.out.println(" --- filtered: --- ");
//
//        filteredFlights.forEach(f -> {
//            System.out.println(count[0]++ + " " + f);
//        });
//
//    }
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)){
            String sql = "    SELECT *\n" +
                         "    FROM flights\n" +
                         "    WHERE departure_airport LIKE ?\n" +
                         "    AND arrival_airport LIKE ?\n" +
                         "    AND status LIKE ?\n" +
                         "    AND scheduled_departure >= ?\n" +
                         "    AND scheduled_departure < ?\n" +
                         "    ORDER BY scheduled_departure\n";
            PreparedStatement pstmt = connection.prepareStatement(sql);
                pstmt.setString(1, "LED");
                pstmt.setString(2, "CEE");
                pstmt.setString(3, "Scheduled");
                pstmt.setTimestamp(4, Timestamp.valueOf("2017-08-17 00:00:00"));
                pstmt.setTimestamp(5, Timestamp.valueOf("2017-09-18 00:00:00"));

            ResultSet rs = pstmt.executeQuery();

             List <Segment> flightsegments = new ArrayList<>();


            int counter = 1;
            while(rs.next()){
                String flightNo = rs.getString("flight_no");
                LocalDateTime departureTime = rs.getTimestamp("scheduled_departure").toLocalDateTime();
                LocalDateTime arrivalTime = rs.getTimestamp("scheduled_arrival").toLocalDateTime();
                String depairport = rs.getString("departure_airport");
                String arrivalairport = rs.getString("arrival_airport");
                String no = rs.getString("flight_no");
                flightsegments.add(new Segment(departureTime, arrivalTime, depairport, arrivalairport, no));
//                System.out.println(counter++ +" " + flightNo + "\t" + departureTime + "\t" + "\t"+ arrivalTime + "\t" + depairport + "\t" + arrivalairport);
            }
            for(Segment segment : flightsegments){
                System.out.println(segment);
            }

        }catch(Exception e){
            System.out.println("‼️" + e.getMessage());
        }
    }
}
