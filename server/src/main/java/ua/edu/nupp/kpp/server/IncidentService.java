package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class IncidentService {
    private static final Logger LOG = Logger.getLogger(IncidentService.class.getName());
    private final IncidentDAO dao = new IncidentDAO();
    private final ChargingModuleDAO moduleDAO = new ChargingModuleDAO();

    public List<Incident> getAll() throws SQLException {
        return dao.getAll();
    }

    public Incident getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(long moduleId, String description, double severityScore) throws SQLException {
        dao.add(fromModule(null, moduleId, description, severityScore, IncidentState.RECORDED));
        LOG.info("Створено інцидент");
    }

    public void update(long id, long moduleId, String description, double severityScore) throws SQLException {
        Incident old = dao.getById(id);
        if (old == null) {
            throw new IllegalArgumentException("Інцидент не знайдено");
        }
        dao.update(fromModule(id, moduleId, description, severityScore, old.getState()));
        LOG.info("Оновлено інцидент");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено інцидент");
    }

    public static boolean isAllowedTransition(IncidentState from, IncidentState to) {
        return (from == IncidentState.RECORDED && to == IncidentState.DIAGNOSED)
                || (from == IncidentState.DIAGNOSED && to == IncidentState.REPAIR)
                || (from == IncidentState.REPAIR && to == IncidentState.RESTORED);
    }

    public void changeState(long id, IncidentState next) throws SQLException {
        Incident current = dao.getById(id);
        if (current == null) {
            throw new IllegalArgumentException("Інцидент не знайдено");
        }
        if (!isAllowedTransition(current.getState(), next)) {
            LOG.warning("Недопустимий перехід стану: " + current.getState() + " -> " + next);
            throw new IllegalArgumentException("Недопустимий перехід стану");
        }
        dao.updateState(id, next);
        LOG.info("Змінено стан: " + current.getState() + " -> " + next);
    }

    private Incident fromModule(Long id,
                                long moduleId,
                                String description,
                                double severityScore,
                                IncidentState state) throws SQLException {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Опис інциденту не може бути порожнім");
        }
        if (severityScore < 0 || severityScore > 100) {
            throw new IllegalArgumentException("Тяжкість несправності (F2) має бути в діапазоні 0..100");
        }
        ChargingModule m = moduleDAO.getById(moduleId);
        if (m == null) {
            throw new IllegalArgumentException("Зарядний модуль не знайдено");
        }
        double f1 = m.getLoadScore();
        double f2 = severityScore;
        double f3 = m.getDeficitScore();
        double priorityIndex = RiskCalculator.calculate(f1, f2, f3);

        return new Incident(id, moduleId, description, state, f1, f2, f3, priorityIndex);
    }
}
