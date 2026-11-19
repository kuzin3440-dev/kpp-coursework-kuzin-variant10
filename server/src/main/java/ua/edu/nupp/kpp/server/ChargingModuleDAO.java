package ua.edu.nupp.kpp.server;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class ChargingModuleDAO {

    public void add(ChargingModule m) throws SQLException {
        String sql = "INSERT INTO charging_module "
                + "(station_id, module_code, connector_type, power_kw, load_score, deficit_score) "
                + "VALUES (?, ?, ?, ?, ?, ?)";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, m);
            ps.executeUpdate();
        }
    }

    public List<ChargingModule> getAll() throws SQLException {
        String sql = "SELECT id, station_id, module_code, connector_type, power_kw, load_score, deficit_score "
                + "FROM charging_module ORDER BY id";
        List<ChargingModule> list = new ArrayList<>();
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql);
             ResultSet rs = ps.executeQuery()) {
            while (rs.next()) {
                list.add(map(rs));
            }
        }
        return list;
    }

    public ChargingModule getById(long id) throws SQLException {
        String sql = "SELECT id, station_id, module_code, connector_type, power_kw, load_score, deficit_score "
                + "FROM charging_module WHERE id = ?";
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

    public void update(ChargingModule m) throws SQLException {
        String sql = "UPDATE charging_module SET station_id = ?, module_code = ?, connector_type = ?, "
                + "power_kw = ?, load_score = ?, deficit_score = ? WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            bind(ps, m);
            ps.setLong(7, m.getId());
            ps.executeUpdate();
        }
    }

    public void delete(long id) throws SQLException {
        String sql = "DELETE FROM charging_module WHERE id = ?";
        try (Connection c = DBUtil.getConnection();
             PreparedStatement ps = c.prepareStatement(sql)) {
            ps.setLong(1, id);
            ps.executeUpdate();
        }
    }

    private void bind(PreparedStatement ps, ChargingModule m) throws SQLException {
        ps.setLong(1, m.getStationId());
        ps.setString(2, m.getModuleCode());
        ps.setString(3, m.getConnectorType());
        ps.setDouble(4, m.getPowerKw());
        ps.setDouble(5, m.getLoadScore());
        ps.setDouble(6, m.getDeficitScore());
    }

    private ChargingModule map(ResultSet rs) throws SQLException {
        return new ChargingModule(
                rs.getLong("id"),
                rs.getLong("station_id"),
                rs.getString("module_code"),
                rs.getString("connector_type"),
                rs.getDouble("power_kw"),
                rs.getDouble("load_score"),
                rs.getDouble("deficit_score")
        );
    }
}
