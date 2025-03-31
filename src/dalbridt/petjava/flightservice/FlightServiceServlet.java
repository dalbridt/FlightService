package dalbridt.petjava.flightservice;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.apache.commons.dbcp2.BasicDataSource;

import java.io.IOException;
import java.io.PrintWriter;
import java.sql.Connection;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;
import java.util.function.Predicate;

import com.fasterxml.jackson.datatype.jsr310.JavaTimeModule;

/**
 * entry point to the app
 * managing http requests
 */

@WebServlet("/")
public class FlightServiceServlet extends HttpServlet {
    private BasicDataSource ds;
    private FlightDaoService flightDaoService; // todo не будет доступа напрямую, только через flight service
    private FlightService flightService;
    private ObjectMapper objectMapper;

    @Override
    public void init() {
        objectMapper = new ObjectMapper();
        objectMapper.registerModule(new JavaTimeModule());
        Properties properties = new Properties();
        try {
            properties.load(this.getClass().getClassLoader().getResourceAsStream("servlet.properties"));
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        this.ds = new BasicDataSource();
        ds.setDriverClassName(properties.getProperty("db.driver"));
        ds.setUrl(properties.getProperty("db.url"));
        ds.setUsername(properties.getProperty("db.username"));
        ds.setPassword(properties.getProperty("db.password"));

        try (Connection connection = ds.getConnection()) {
            System.out.println("☘️connection established" + connection);
        } catch (Exception e) {
            System.out.println("‼️" + e.getMessage());
            throw new RuntimeException(e.getMessage(), e);
        }
        this.flightDaoService = new FlightDaoService(ds); // todo будет пробрасывание зависимостей, но не будет дао напрчямую
        this.flightService = new FlightService(flightDaoService);
    }

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String servletPath = req.getServletPath();

        switch (servletPath) {
            case "/getflightswithtransit" -> {
                String codeA = req.getParameter("departure"); // todo дубдируется три раза, убрать в метод?

                String codeB = req.getParameter("arrival");
                if (!validateInput(codeA, codeB)) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "not valid airport code");
                }
                handleGetFlightsWithTransit(req, resp);
            }
            case "/getseatsamount" -> {
                String codeA = req.getParameter("departure");
                String codeB = req.getParameter("arrival");
                if (!validateInput(codeA, codeB)) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "not valid airport code");
                }
                handleGetSeatsAmount(req, resp);
            }
            case "/getflightinfo" -> {
                if (Integer.parseInt(req.getParameter("flightId")) > 0) {
                    handleGetFlightInfo(req, resp);
                }
            }
            case "/filterflights" -> {
                String codeA = req.getParameter("departure");
                String codeB = req.getParameter("arrival");
                String earliestDepartureTime = req.getParameter("earliestDepartureTime");
                String maxGroundTimeHours = req.getParameter("maxGroundTimeHours");
                if (!validateInput(codeA, codeB) || (earliestDepartureTime == null || maxGroundTimeHours == null)) {
                    resp.sendError(HttpServletResponse.SC_BAD_REQUEST, "not enough request params: earliest departure time "
                                                                       + earliestDepartureTime + " max ground time hours: " + maxGroundTimeHours);
                }
                handleFilterFlights(req, resp);
            }
            case null, default ->
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "I don't have this path : " + req.getServletPath());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String servletPath = req.getServletPath();
        if ("/addnewflight".equals(servletPath)) {
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
                resp.getWriter().write(convertSegmentToJson(segment));
            }
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    private void handleFilterFlights(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        String codeA = req.getParameter("departure");
        String codeB = req.getParameter("arrival");
        String earliestDepartureTime = req.getParameter("earliestDepartureTime");
        String maxGroundTimeHours = req.getParameter("maxGroundTimeHours");
        List<Predicate<Flight>> params = new ArrayList<>();
        if (earliestDepartureTime != null) {
            LocalTime departureTime = LocalTime.parse(earliestDepartureTime);
            DepartureTimeFilter departureTimeFilter = new DepartureTimeFilter(departureTime);
            params.add(departureTimeFilter);
        }
        if (maxGroundTimeHours != null) {
            int groundHours = Integer.parseInt(maxGroundTimeHours);
            TransferTimeFilter transferTimeFilter = new TransferTimeFilter(groundHours);
            params.add(transferTimeFilter);
        }
        List<Flight> filteredFlights = flightService.filterFlights(codeA, codeB, params);
        resp.getWriter().write(convertFlightToJson(filteredFlights));

    }

    private Segment mapSegmentFromRequest(HttpServletRequest req) {
        String codeA = req.getParameter("departure");
        String codeB = req.getParameter("arrival");
        String flightNo = req.getParameter("flightNo");
        LocalDateTime departureTime = LocalDateTime.parse(req.getParameter("departureDate"));
        LocalDateTime arrivalTime = LocalDateTime.parse(req.getParameter("arrivalDate"));
        boolean paramsAreValid = validateInput(codeA, codeB) && flightNo != null && departureTime != null && arrivalTime != null;
        if (paramsAreValid) {
            return new Segment(departureTime, arrivalTime, codeA, codeB, flightNo);
        }
        return null;
    }

    protected boolean validateInput(String codeA, String codeB) {
        return (codeA.length() == 3 && codeA.matches("\\w+"))
               && (codeB.length() == 3 && codeB.matches("\\w+"));
    }

    private String convertFlightToJson(List<Flight> list) throws JsonProcessingException {
        return objectMapper.writeValueAsString(list);
    }

    private String convertSegmentToJson(Segment segment) throws JsonProcessingException {
        String json = objectMapper.writeValueAsString(segment);
        return objectMapper.writeValueAsString(json);
    }
}

// http://localhost:8080/flightService/getSeatsAmount?departure=LED&arrival=DME

// http://localhost:8080/flightService/addNewFlight?departure=SVO&arrival=DME&departureDate=2025-04-05T05:35:00&arrivalDate=2025-04-05T06:35:00&flightNo=PG0407