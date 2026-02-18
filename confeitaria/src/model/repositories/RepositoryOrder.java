package model.repositories;

import java.sql.*;

public class RepositoryOrder {

    private static final String SQL_INSERT =
            "INSERT INTO \"order\" (id_user, datetime, total_price, delivery, observations) "
                    + "VALUES (?, ?, ?, ?, ?)";

    public Integer createOrderAndReturnId(Integer idUser, Timestamp datetime, Double totalPrice,
                                          String delivery, String observations) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, idUser);
            stmt.setTimestamp(2, datetime);
            stmt.setDouble(3, totalPrice);
            stmt.setString(4, delivery);
            stmt.setString(5, observations);

            if (stmt.executeUpdate() == 0) return null;

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }
}