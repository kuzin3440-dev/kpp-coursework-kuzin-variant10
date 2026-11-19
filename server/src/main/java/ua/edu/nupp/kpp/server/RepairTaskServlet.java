package ua.edu.nupp.kpp.server;

import com.fasterxml.jackson.databind.ObjectMapper;

import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Map;

public class RepairTaskServlet extends HttpServlet {
    private final RepairTaskService service = new RepairTaskService();
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
                RepairTask t = new RepairTask(
                        null,
                        parseLong(req.getParameter("incidentId"), "Некоректний ID інциденту"),
                        req.getParameter("description"),
                        req.getParameter("plannedDate")
                );
                service.add(t);
            } else if ("update".equals(action)) {
                RepairTask t = new RepairTask(
                        parseLong(req.getParameter("id"), "Некоректний ID завдання"),
                        parseLong(req.getParameter("incidentId"), "Некоректний ID інциденту"),
                        req.getParameter("description"),
                        req.getParameter("plannedDate")
                );
                service.update(t);
            } else if ("delete".equals(action)) {
                long id = parseLong(req.getParameter("id"), "Некоректний ID завдання");
                service.delete(id);
            } else {
                throw new IllegalArgumentException("Невідома операція: " + action);
            }
            writeJson(resp, HttpServletResponse.SC_OK, Map.of("status", "ok"));
        } catch (Exception e) {
            writeError(resp, e);
        }
    }

    private long parseLong(String val, String errMsg) {
        try {
            return Long.parseLong(val);
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
