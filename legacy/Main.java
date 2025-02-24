//package dalbridt.petjava.flightservice;
//
//import org.apache.commons.dbcp2.BasicDataSource;
//import java.sql.*;
//import java.util.List;
//
//
//public class Main {
//    private static final BasicDataSource ds = new BasicDataSource();
//    static {
//        ds.setUrl("jdbc:postgresql://localhost:5432/demo");
//        ds.setUsername("admin");
//        ds.setPassword("pwd1234");
//        try(Connection connection = ds.getConnection()){
//            System.out.println("☘️connection established" +  connection);
//        } catch (Exception e) {
//            System.out.println("‼️" + e.getMessage());
//        }
//    }
//    public static void main(String[] args) throws ClassNotFoundException, SQLException {
//        FlightDaoService dbc = new FlightDaoService(ds);
//        FlightService flightService = new FlightService();
//        ConsoleHandler consoleHandler = new ConsoleHandler();
//        List<String> abpoints = consoleHandler.readPoints();
//
//        while(!dbc.validateABpoints(abpoints.get(0), abpoints.get(1))){
//            System.out.println("Please enter two different valid airport codes");
//            abpoints =  consoleHandler.readPoints();
//        }
//
////        dbc.setISactive(true, 390);
//        System.out.println(dbc.getSeatsAmount(abpoints.get(0), abpoints.get(1)));
//    }
//
//}
