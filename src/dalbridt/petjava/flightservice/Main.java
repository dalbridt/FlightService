package dalbridt.petjava.flightservice;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import static dalbridt.petjava.flightservice.DBConnectProperties.*;

public class Main {
    public static void main(String[] args) throws ClassNotFoundException, SQLException {

        try(Connection connection = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS)){
            if(connection != null){
                System.out.println("Connected to database");
            } else {
                System.out.println("Could not connect to database");
            }

        }catch(Exception e){
            System.out.println("‼️" + e.getMessage());
        }
    }
}
