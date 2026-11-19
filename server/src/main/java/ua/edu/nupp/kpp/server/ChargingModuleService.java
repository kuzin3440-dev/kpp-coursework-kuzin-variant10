package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class ChargingModuleService {
    private static final Logger LOG = Logger.getLogger(ChargingModuleService.class.getName());
    private final ChargingModuleDAO dao = new ChargingModuleDAO();
    private final StationDAO stationDAO = new StationDAO();

    public List<ChargingModule> getAll() throws SQLException {
        return dao.getAll();
    }

    public ChargingModule getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(ChargingModule m) throws SQLException {
        validate(m);
        requireStation(m.getStationId());
        dao.add(m);
        LOG.info("Додано зарядний модуль");
    }

    public void update(ChargingModule m) throws SQLException {
        validate(m);
        if (m.getId() == null || m.getId() <= 0) {
            throw new IllegalArgumentException("Некоректний ідентифікатор модуля");
        }
        requireStation(m.getStationId());
        dao.update(m);
        LOG.info("Оновлено зарядний модуль");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено зарядний модуль");
    }

    static void validate(ChargingModule m) {
        if (m == null || m.getStationId() <= 0 || blank(m.getModuleCode()) || blank(m.getConnectorType())) {
            throw new IllegalArgumentException("Некоректні основні дані зарядного модуля");
        }
        if (m.getPowerKw() <= 0) {
            throw new IllegalArgumentException("Потужність модуля має бути більшою за 0");
        }
        if (m.getLoadScore() < 0 || m.getLoadScore() > 100
                || m.getDeficitScore() < 0 || m.getDeficitScore() > 100) {
            throw new IllegalArgumentException("Фактори F1 (loadScore) і F3 (deficitScore) мають бути в діапазоні 0..100");
        }
    }

    private void requireStation(long stationId) throws SQLException {
        if (stationDAO.getById(stationId) == null) {
            throw new IllegalArgumentException("Зарядну станцію не знайдено");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
