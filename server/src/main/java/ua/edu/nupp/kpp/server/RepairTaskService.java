package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class RepairTaskService {
    private static final Logger LOG = Logger.getLogger(RepairTaskService.class.getName());
    private final RepairTaskDAO dao = new RepairTaskDAO();
    private final IncidentDAO incidentDAO = new IncidentDAO();

    public List<RepairTask> getAll() throws SQLException {
        return dao.getAll();
    }

    public RepairTask getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(RepairTask t) throws SQLException {
        validate(t);
        requireIncident(t.getIncidentId());
        dao.add(t);
        LOG.info("Додано ремонтне завдання");
    }

    public void update(RepairTask t) throws SQLException {
        validate(t);
        if (t.getId() == null || t.getId() <= 0) {
            throw new IllegalArgumentException("Некоректний ідентифікатор ремонтного завдання");
        }
        requireIncident(t.getIncidentId());
        dao.update(t);
        LOG.info("Оновлено ремонтне завдання");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено ремонтне завдання");
    }

    static void validate(RepairTask t) {
        if (t == null || t.getIncidentId() <= 0
                || blank(t.getDescription()) || blank(t.getPlannedDate())) {
            throw new IllegalArgumentException("Некоректні дані ремонтного завдання");
        }
    }

    private void requireIncident(long incidentId) throws SQLException {
        if (incidentDAO.getById(incidentId) == null) {
            throw new IllegalArgumentException("Інцидент не знайдено");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
