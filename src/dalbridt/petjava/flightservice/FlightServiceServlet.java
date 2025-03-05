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
import java.util.List;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

@WebServlet("/")
public class FlightServiceServlet extends HttpServlet {
    private BasicDataSource ds;
    private FlightDaoService flightDaoService;

    @Override
    public void init() {
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
        String servletPath = req.getServletPath();

        if ("/getFlightsWithTransit".equals(servletPath)) {
            String codeA = req.getParameter("departure");
            String codeB = req.getParameter("arrival");
            if (!validateInput(codeA, codeB)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "not valid airport code");
            }
            handleGetFlightsWithTransit(req, resp);
        } else if ("/getSeatsAmount".equals(servletPath)) {
            String codeA = req.getParameter("departure");
            String codeB = req.getParameter("arrival");
            if (!validateInput(codeA, codeB)) {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "not valid airport code");
            }
            handleGetSeatsAmount(req, resp);
        } else if ("/getFlightInfo".equals(servletPath)) {
            if (Integer.parseInt(req.getParameter("flightId")) > 0) {
                handleGetFlightInfo(req, resp);
            }
        }else {
            resp.sendError(HttpServletResponse.SC_NOT_FOUND, "I see you, but not : " + req.getServletPath());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String servletPath = req.getServletPath();
        if ("/addNewFlight".equals(servletPath)) {
            handleAddNewFlight(req, resp);
        }
    }

    private void handleGetFlightsWithTransit(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String codeA = req.getParameter("departure");
        String codeB = req.getParameter("arrival");
        PrintWriter out = resp.getWriter();
        try {
            if (flightDaoService.validateABpoints(codeA, codeB)) {
                List<Flight> res = flightDaoService.getFlightsWithTransit(codeA, codeB);
                out.write(convertFlightToJson(res));
                out.close();
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGetSeatsAmount(HttpServletRequest req, HttpServletResponse resp) {
        String codeA = req.getParameter("departure");
        String codeB = req.getParameter("arrival");
        try {
            if (flightDaoService.validateABpoints(codeA, codeB)) {
                int res = flightDaoService.getSeatsAmount(codeA, codeB);
                resp.getWriter().write(String.valueOf(res));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleAddNewFlight(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        Segment newFlight = mapSegmentFromRequest(req);

        if (newFlight == null) {
            resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "Flight data is missing or incorrect");
            return;
        }
        try {
            if (flightDaoService.addFlight(newFlight)) {
                resp.getWriter().write("row added successfully" + newFlight);
            } else {
                resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "not valid input parameters " + newFlight);
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleGetFlightInfo(HttpServletRequest req, HttpServletResponse resp) {
        int id = Integer.parseInt(req.getParameter("flightId"));
        try {
            Segment segment = flightDaoService.getFlightsByFlightId(id);
            if (segment != null) {
                resp.getWriter().write(convetSegmentToJson(segment));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private Segment mapSegmentFromRequest(HttpServletRequest req) {
        String codeA = req.getParameter("departure");
        String codeB = req.getParameter("arrival");
        String flightNo = req.getParameter("flightNo");
        LocalDateTime departureTime = LocalDateTime.parse(req.getParameter("departureDate"));
        LocalDateTime arrivalTime = LocalDateTime.parse(req.getParameter("arrivalDate"));
        boolean paramsAreValid = validateInput(codeA, codeB) && flightNo != null && departureTime !=  null && arrivalTime != null;
        if (paramsAreValid) {
            return new Segment(departureTime,arrivalTime, codeA, codeB, flightNo);
        }
        return null;
    }

    protected boolean validateInput(String codeA, String codeB) {
        return (codeA.length() == 3 && codeA.matches("\\w+"))
               && (codeB.length() == 3 && codeB.matches("\\w+"));
    }

    private String convertFlightToJson(List<Flight> list) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        return objectMapper.writeValueAsString(list);
    }

    private String convetSegmentToJson(Segment segment) throws JsonProcessingException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        String json = objectMapper.writeValueAsString(segment);
        return objectMapper.writeValueAsString(json);
    }
}

// http://localhost:8080/flightService/getSeatsAmount?departure=LED&arrival=DME

// http://localhost:8080/flightService/addNewFlight?departure=SVO&arrival=DME&departureDate=2025-04-05T05:35:00&arrivalDate=2025-04-05T06:35:00&flightNo=PG0407