package model.repositories;

import model.entities.Size;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositorySize {

    public static final String SQL_INSERT =
            "INSERT INTO \"size\"(name, yield, weight, price) VALUES (?, ?, ?, ?)";

    public static final String SQL_DELETE =
            "DELETE FROM \"size\" WHERE id = ?";

    public static final String SQL_FIND_BY_ID =
            "SELECT id AS size_id, name AS size_name, yield AS size_yield, weight AS size_weight, price AS size_price "
                    + "FROM \"size\" WHERE id = ?";

    private static final String SQL_FIND_BY_NAME =
            "SELECT id AS size_id, name AS size_name, yield AS size_yield, weight AS size_weight, price AS size_price "
                    + "FROM \"size\" WHERE name = ?";

    private static final String SQL_FIND_ALL =
            "SELECT id AS size_id, name AS size_name, yield AS size_yield, weight AS size_weight, price AS size_price "
                    + "FROM \"size\" ORDER BY id";

    
    public boolean createSize(Size size) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setString(1, size.getName());
            stmt.setString(2, size.getYield());
            stmt.setString(3, size.getWeight());
            stmt.setDouble(4, size.getPrice());
            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteSize(Size size) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, size.getId());
            return stmt.executeUpdate() > 0;
        }
    }


    public Size findByIdSize(Integer id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToSize(rs) : null;
            }
        }
    }

    public Size findByNameSize(String name) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_NAME)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToSize(rs) : null;
            }
        }
    }

    public List<Size> findAllSize() throws SQLException {
        List<Size> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToSize(rs));
            }
        }
        return list;
    }

    private Size mapResultSetToSize(ResultSet rs) throws SQLException {
        return new Size(
                rs.getInt("size_id"),
                rs.getString("size_name"),
                rs.getString("size_yield"),
                rs.getString("size_weight"),
                rs.getDouble("size_price")
        );
    }
}