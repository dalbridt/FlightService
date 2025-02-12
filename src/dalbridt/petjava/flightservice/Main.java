package dalbridt.petjava.flightservice;

import java.sql.*;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import org.apache.commons.dbcp2.BasicDataSource;


public class Main {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {
        DBconnection dbc = new DBconnection();
        FlightService flightService = new FlightService();
        String [] abpoints = flightService.getPpoints();

        while(!dbc.validateABpoints(abpoints)){
            System.out.println("Please enter two different valid airport codes");
            abpoints =  flightService.getPpoints();
        }

//        dbc.setISactive(true, 390);
        System.out.println(dbc.getSeatsAmount(abpoints));
    }

}
