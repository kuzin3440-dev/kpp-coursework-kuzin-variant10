package ua.edu.nupp.kpp.server;

import java.sql.SQLException;
import java.util.List;
import java.util.logging.Logger;

public class StationService {
    private static final Logger LOG = Logger.getLogger(StationService.class.getName());
    private final StationDAO dao = new StationDAO();

    public List<Station> getAll() throws SQLException {
        return dao.getAll();
    }

    public Station getById(long id) throws SQLException {
        return dao.getById(id);
    }

    public void add(Station s) throws SQLException {
        validate(s);
        dao.add(s);
        LOG.info("Додано зарядну станцію");
    }

    public void update(Station s) throws SQLException {
        validate(s);
        if (s.getId() == null || s.getId() <= 0) {
            throw new IllegalArgumentException("Некоректний ідентифікатор станції");
        }
        dao.update(s);
        LOG.info("Оновлено зарядну станцію");
    }

    public void delete(long id) throws SQLException {
        dao.delete(id);
        LOG.info("Видалено зарядну станцію");
    }

    static void validate(Station s) {
        if (s == null || blank(s.getCode()) || blank(s.getName()) || blank(s.getCity())) {
            throw new IllegalArgumentException("Усі поля зарядної станції є обов'язковими");
        }
    }

    private static boolean blank(String v) {
        return v == null || v.isBlank();
    }
}
