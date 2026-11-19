package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class IncidentDAO {

    public void add(Incident inc) throws SQLException {
        String sql = "INSERT INTO incident "
                + "(module_id, description, state, f1, f2, f3, priority_index) "
                + "VALUES (?, ?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, inc);
            ps.executeUpdate();
        }
    }

    public List<Incident> getAll() throws SQLException {
        String sql = "SELECT id, module_id, description, state, f1, f2, f3, priority_index "
                + "FROM incident ORDER BY id";
        List<Incident> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public Incident getById(long id) throws SQLException {
        String sql = "SELECT id, module_id, description, state, f1, f2, f3, priority_index "
                + "FROM incident WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            try (ResultSet rs = ps.executeQuery()) {
                if (rs.next()) {
                    return map(rs);
                }
            }
        }
        return null;
    }

    public void update(Incident inc) throws SQLException {
        String sql = "UPDATE incident SET module_id = ?, description = ?, state = ?, "
                + "f1 = ?, f2 = ?, f3 = ?, priority_index = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, inc);
            ps.setLong(8, inc.getId());
            ps.executeUpdate();
        }
    }

    public void updateState(long id, IncidentState state) throws SQLException {
        String sql = "UPDATE incident SET state = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setString(1, state.name());
            ps.setLong(2, id);
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM incident WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, Incident inc) throws SQLException {
        ps.setLong(1, inc.getModuleId());
        ps.setString(2, inc.getDescription());
        ps.setString(3, inc.getState().name());
        ps.setDouble(4, inc.getF1());
        ps.setDouble(5, inc.getF2());
        ps.setDouble(6, inc.getF3());
        ps.setDouble(7, inc.getPriorityIndex());
    }

    private Incident map(ResultSet rs) throws SQLException {
        return new Incident(
                rs.getLong("id"),
                rs.getLong("module_id"),
                rs.getString("description"),
                IncidentState.valueOf(rs.getString("state")),
                rs.getDouble("f1"),
                rs.getDouble("f2"),
                rs.getDouble("f3"),
                rs.getDouble("priority_index")
        );
    }
}
