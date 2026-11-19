package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class IncidentServlet extends HttpServlet {
    private final IncidentService service = new IncidentService();
    private final ObjectMapper mapper = new ObjectMapper();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        try {
            writeJson(resp, HttpServletResponse.SC_OK, service.getAll());
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            String action = req.getParameter("action");
            if ("add".equals(action)) {
                long moduleId = parseLong(req.getParameter("moduleId"), "Некоректний ID модуля");
                String description = req.getParameter("description");
                double f2 = parseDouble(req.getParameter("f2"), "Некоректне значення F2");
                service.add(moduleId, description, f2);
            } else if ("update".equals(action)) {
                long id = parseLong(req.getParameter("id"), "Некоректний ID інциденту");
                long moduleId = parseLong(req.getParameter("moduleId"), "Некоректний ID модуля");
                String description = req.getParameter("description");
                double f2 = parseDouble(req.getParameter("f2"), "Некоректне значення F2");
                service.update(id, moduleId, description, f2);
            } else if ("transition".equals(action)) {
                long id = parseLong(req.getParameter("id"), "Некоректний ID інциденту");
                IncidentState state = parseState(req.getParameter("state"));
                service.changeState(id, state);
            } else if ("delete".equals(action)) {
                long id = parseLong(req.getParameter("id"), "Некоректний ID інциденту");
                service.delete(id);
            } else {
                throw new IllegalArgumentException("Невідома операція: " + action);
            }
            writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "ok"));
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private IncidentState parseState(String val) {
        try {
            return IncidentState.valueOf(val);
        } catch (Exception e) {
            throw new IllegalArgumentException("Некоректний стан інциденту");
        }
    }

    private long parseLong(String val, String errMsg) {
        try {
            return Long.parseLong(val);
        } catch (Exception e) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    private double parseDouble(String val, String errMsg) {
        try {
            return Double.parseDouble(val);
        } catch (Exception e) {
            throw new IllegalArgumentException(errMsg);
        }
    }

    private void writeJson(HttpServletResponse resp, int status, Object data) throws IOException {
        resp.setStatus(status);
        resp.setContentType("application/json;charset=UTF-8");
        mapper.writeValue(resp.getWriter(), data);
    }

    private void writeError(HttpServletResponse resp, Exception e) throws IOException {
        int status = (e instanceof IllegalArgumentException)
                ? HttpServletResponse.SC_BAD_REQUEST
                : HttpServletResponse.SC_INTERNAL_SERVER_ERROR;
        String msg = (e.getMessage() != null && !e.getMessage().isBlank())
                ? e.getMessage()
                : "Внутрішня помилка сервера";
        writeJson(resp, status, Map.of("error", msg));
    }
}
