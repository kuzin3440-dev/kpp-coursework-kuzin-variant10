package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepairTaskDAO {

    public void add(RepairTask t) throws SQLException {
        String sql = "INSERT INTO repair_task (incident_id, description, planned_date) VALUES (?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, t.getIncidentId());
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getPlannedDate());
            ps.executeUpdate();
        }
    }

    public List<RepairTask> getAll() throws SQLException {
        String sql = "SELECT id, incident_id, description, planned_date FROM repair_task ORDER BY id";
        List<RepairTask> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public RepairTask getById(long id) throws SQLException {
        String sql = "SELECT id, incident_id, description, planned_date FROM repair_task WHERE id = ?";
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

    public void update(RepairTask t) throws SQLException {
        String sql = "UPDATE repair_task SET incident_id = ?, description = ?, planned_date = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, t.getIncidentId());
            ps.setString(2, t.getDescription());
            ps.setString(3, t.getPlannedDate());
            ps.setLong(4, t.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM repair_task WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private RepairTask map(ResultSet rs) throws SQLException {
        return new RepairTask(
                rs.getLong("id"),
                rs.getLong("incident_id"),
                rs.getString("description"),
                rs.getString("planned_date")
        );
    }
}
