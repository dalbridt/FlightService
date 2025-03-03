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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebServlet("/")
public class FlightServiceServlet extends HttpServlet {
    private  BasicDataSource ds;
    private  FlightDaoService flightDaoService;

    @Override
    public void init()  {
        this.ds = new BasicDataSource();
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
        this.flightDaoService = new FlightDaoService(ds);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String codeA = req.getParameter("departure");
        String codeB = req.getParameter("arrival");
        String servletPath = req.getServletPath();

        if("/getFlightsWithTransit".equals(servletPath)) {
            if(!validateInput(codeA, codeB)){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST , "not valid airport code");
            }
            handleGetFlightsWithTransit(req, resp);
        }else if("/getSeatsAmount".equals(servletPath)) {
            if(!validateInput(codeA, codeB)){
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST , "not valid airport code");
            }
            handleGetSeatsAmount(req, resp);
        } else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "I see you, but not : " + req.getServletPath());
        }
    }

    private void handleGetFlightsWithTransit(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String codeA = req.getParameter("departure");
        String codeB = req.getParameter("arrival");
        PrintWriter out = resp.getWriter();
        try {
            if (flightDaoService.validateABpoints(codeA, codeB)) {
                List <Flight> res= flightDaoService.getFlightsWithTransit(codeA, codeB);
                out.write(convertTojson(res));
                out.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }
    private void handleGetSeatsAmount(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String codeA = req.getParameter("departure");
        String codeB = req.getParameter("arrival");
        try {
            if (flightDaoService.validateABpoints(codeA, codeB)) {
                int res= flightDaoService.getSeatsAmount(codeA, codeB);
                resp.getWriter().write(String.valueOf(res));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    protected boolean validateInput(String codeA, String codeB) {
        return (codeA.length() == 3 && codeA.matches("\\w+"))
               && (codeB.length() == 3 && codeB.matches("\\w+"));
    }

    private String convertTojson(List<Flight> list) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(list);
    }
}

// http://localhost:8080/flightService/?action=getFlightsWithTransit&departure=DME&arrival=LED