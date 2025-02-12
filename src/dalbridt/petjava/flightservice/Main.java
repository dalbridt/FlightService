package dalbridt.petjava.flightservice;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbcp2.BasicDataSource;


public class Main {
    private static final BasicDataSource ds = new BasicDataSource();
    static {
        ds.setUrl("jdbc:postgresql://localhost:5432/demo");
        ds.setUsername("admin");
        ds.setPassword("pwd1234");
    }
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        try (Connection connection = ds.getConnection()) {
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

            List<Segment> flightsegments = new ArrayList<>();


            while (rs.next()) {
                LocalDateTime departureTime = rs.getTimestamp("scheduled_departure").toLocalDateTime();
                LocalDateTime arrivalTime = rs.getTimestamp("scheduled_arrival").toLocalDateTime();
                String depairport = rs.getString("departure_airport");
                String arrivalairport = rs.getString("arrival_airport");
                String no = rs.getString("flight_no");
                flightsegments.add(new Segment(departureTime, arrivalTime, depairport, arrivalairport, no));
            }

            for (Segment segment : flightsegments) {
                System.out.println(segment);
            }
        } catch (Exception e) {
            System.out.println("‼️" + e.getMessage());
        }
    }
}
