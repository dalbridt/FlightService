package dalbridt.petjava.flightservice;

import org.apache.commons.dbcp2.BasicDataSource;

public class DBConnection {
    private static final BasicDataSource ds = new BasicDataSource();
    static {
        ds.setUrl("jdbc:postgresql://localhost:5432/demo");
        ds.setUsername("admin");
        ds.setPassword("pwd1234");
    }
    
}
