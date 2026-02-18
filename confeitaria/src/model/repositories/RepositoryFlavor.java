package model.repositories;

import model.entities.Flavor;
import model.entities.FlavorLevel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class RepositoryFlavor {

    public static final String SQL_INSERT =
            "INSERT INTO flavor(name, id_flavor_level, description) VALUES (?, ?, ?)";

    public static final String SQL_DELETE =
            "DELETE FROM flavor WHERE id = ?";

    public static final String SQL_FIND_BY_ID =
            "SELECT "
                    + "f.id AS flavor_id, f.name AS flavor_name, f.description AS flavor_description, "
                    + "fl.id AS flavor_level_id, fl.name AS flavor_level_name, fl.price AS flavor_level_price "
                    + "FROM flavor f "
                    + "INNER JOIN flavor_level fl ON fl.id = f.id_flavor_level "
                    + "WHERE f.id = ?";

    public static final String SQL_FIND_ALL =
            "SELECT "
                    + "f.id AS flavor_id, f.name AS flavor_name, f.description AS flavor_description, "
                    + "fl.id AS flavor_level_id, fl.name AS flavor_level_name, fl.price AS flavor_level_price "
                    + "FROM flavor f "
                    + "INNER JOIN flavor_level fl ON fl.id = f.id_flavor_level "
                    + "ORDER BY f.id DESC";

    public boolean createFlavor(Flavor flavor) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setString(1, flavor.getName());
            stmt.setInt(2, flavor.getLevel().getId());
            stmt.setString(3, flavor.getDescription());
            return stmt.executeUpdate() > 0;
        }
    }

    public Integer createFlavorAndReturnId(Flavor flavor) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, flavor.getName());
            stmt.setInt(2, flavor.getLevel().getId());
            stmt.setString(3, flavor.getDescription());

            if (stmt.executeUpdate() == 0) return null;

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }

    public boolean deleteFlavor(Flavor flavor) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, flavor.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public Flavor findByIdFlavor(Integer id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToFlavor(rs) : null;
            }
        }
    }

    public List<Flavor> findAllFlavor() throws SQLException {
        List<Flavor> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToFlavor(rs));
            }
        }
        return list;
    }

    private Flavor mapResultSetToFlavor(ResultSet rs) throws SQLException {
        FlavorLevel level = new FlavorLevel(
                rs.getInt("flavor_level_id"),
                rs.getString("flavor_level_name"),
                rs.getDouble("flavor_level_price")
        );

        Flavor flavor = new Flavor(
                rs.getString("flavor_name"),
                level,
                rs.getString("flavor_description")
        );
        flavor.setId(rs.getInt("flavor_id"));
        return flavor;
    }
}