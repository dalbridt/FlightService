package dalbridt.petjava.flightservice;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class FlightDaoService {
    private final BasicDataSource ds;

    private final String validateAirportsQuery = "select count (*) from airports_data\n" +
                                                "where airport_code = ? or airport_code = ?";
    private final String setIsActiveQuery = "update flights\n" +
                                            "set is_active=?\n" +
                                            "where flights.flight_id = ?\n";
    private final String getSeatsAmountQuery = "select count(s.seat_no) as seat_count\n" +
                      "from seats s \n" +
                      "where s.aircraft_code = (select f.aircraft_code from flights f where f.departure_airport = ? \n" +
                      "  and f.arrival_airport = ? limit 1)\n" +
                      "group by s.aircraft_code";

    public FlightDaoService(BasicDataSource ds) {
        this.ds = ds;
    }

    public boolean validateABpoints(String departure_airport, String arrival_airport) throws SQLException {
        PreparedStatement pstmt = ds.getConnection().prepareStatement(validateAirportsQuery);
        pstmt.setString(1, departure_airport);
        pstmt.setString(2, arrival_airport);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1) == 2;
        }
        return false;
    }

    public void setISactive(boolean isactive, int flightID) throws SQLException {
        PreparedStatement pstmt = ds.getConnection().prepareStatement(setIsActiveQuery);
        pstmt.setBoolean(1, isactive);
        pstmt.setInt(2, flightID);
        pstmt.executeUpdate();
    }

    public int getSeatsAmount(String departure_airport, String arrival_airport) throws SQLException {
        PreparedStatement pstmt = ds.getConnection().prepareStatement(getSeatsAmountQuery);
        pstmt.setString(1, departure_airport);
        pstmt.setString(2, arrival_airport);
        ResultSet rs = pstmt.executeQuery();
        if (rs.next()) {
            return rs.getInt(1);
        }
        return 0;
    }
}
