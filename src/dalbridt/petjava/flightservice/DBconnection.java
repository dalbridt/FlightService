package dalbridt.petjava.flightservice;

import org.apache.commons.dbcp2.BasicDataSource;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class DBconnection { // datasource не создает, а получает извне
    private static final BasicDataSource ds = new BasicDataSource();
    static {
        ds.setUrl("jdbc:postgresql://localhost:5432/demo");
        ds.setUsername("admin");
        ds.setPassword("pwd1234");
        try(Connection connection = ds.getConnection()){
            System.out.println("☘️connection established" +  connection);
        } catch (Exception e) {
            System.out.println("‼️" + e.getMessage());
        }
    }
    public boolean validateABpoints(String [] abpoints) throws SQLException {
        String sqlquery = "select count (*) from airports_data\n" +
                          "where airport_code = ? or airport_code = ?";
        PreparedStatement pstmt = ds.getConnection().prepareStatement(sqlquery);
        pstmt.setString(1, abpoints[0]);
        pstmt.setString(2, abpoints[1]);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()){
            return rs.getInt(1) == 2;
        }
        return false;
    }

    public void setISactive(boolean isactive, int flightID) throws SQLException {
        String sqlquery = "update flights\n" +
                          "set is_active=?\n" +
                          "where flights.flight_id = ?\n";
        PreparedStatement pstmt = ds.getConnection().prepareStatement(sqlquery);
        pstmt.setBoolean(1, isactive);
        pstmt.setInt(2, flightID);
        pstmt.executeUpdate();
    }

    public int getSeatsAmount(String[] abpoints) throws SQLException {
        String sqlquery = "select s.aircraft_code , count(s.seat_no) as seat_count\n" +
                          "from seats s \n" +
                          "where s.aircraft_code = (select f.aircraft_code from flights f where f.departure_airport = ? \n" +
                          "  and f.arrival_airport = ? limit 1)\n" +
                          "group by s.aircraft_code";
        PreparedStatement pstmt = ds.getConnection().prepareStatement(sqlquery);
        pstmt.setString(1, abpoints[0]);
        pstmt.setString(2, abpoints[1]);
        ResultSet rs = pstmt.executeQuery();
        if(rs.next()){
            return rs.getInt(1);
        }
        return 0;
    }
}
