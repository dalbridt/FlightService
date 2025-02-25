package dalbridt.petjava.flightservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebServlet("/")
public class FlightServiceServlet extends HttpServlet {
    private static final BasicDataSource ds = new BasicDataSource();

    //todo перенести в конструктор получение connection
    static {
        ds.setDriverClassName("org.postgresql.Driver");
        ds.setUrl("jdbc:postgresql://localhost:5432/demo");
        ds.setUsername("admin");
        ds.setPassword("pwd1234");
        try (Connection connection = ds.getConnection()) {
            System.out.println("☘️connection established" + connection);
        } catch (Exception e) {
            System.out.println("‼️" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
    }


    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        response.setContentType("text/html");
        PrintWriter out = response.getWriter();
        LocalDateTime now = LocalDateTime.now();
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        out.write("<h1>  Deployed </h1>" + now.format(formatter));
        out.write("<h1> enter departure and arrival airport codes: </h1>");
        out.write("<form action='hello' method='post'>");
        out.write("<input type='text' name='airportCodeA' />");
        out.write("<br/>");
        out.write("<input type='text' name='airportCodeB' />");
        out.write("<br/>");
        out.write("<input type='submit' value='Submit' />");
        out.write("</form>");
        out.close();
    }

    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json");
        PrintWriter out = response.getWriter();
        String codeA = request.getParameter("airportCodeA");
        String codeB = request.getParameter("airportCodeB");

        if (!validateInput(codeA, codeB)) {
            response.sendRedirect("hello");
            return;
        }

        FlightDaoService dbc = new FlightDaoService(ds);
        try {
            if (dbc.validateABpoints(codeA, codeB)) {
                List <Flight> res= dbc.getallFlightsBetweenPoints(codeA, codeB);
                out.write(convertTojson(res));
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        out.close();
    }

    protected boolean validateInput(String codeA, String codeB) {
        return (codeA.length() == 3 && codeA.matches("\\w+"))
               && (codeB.length() == 3 && codeB.matches("\\w+"));
    }

    String convertTojson(List<Flight> list) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(list);
    }
}
