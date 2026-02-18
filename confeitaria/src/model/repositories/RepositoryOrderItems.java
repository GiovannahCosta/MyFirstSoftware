package model.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

public class RepositoryOrderItems {

    private static final String SQL_INSERT =
            "INSERT INTO order_items (id_order, id_product, quantity, price_at_moment) "
                    + "VALUES (?, ?, ?, ?)";

    public boolean createOrderItem(Integer idOrder, Integer idProduct, Integer quantity, Double priceAtMoment)
            throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setInt(1, idOrder);
            stmt.setInt(2, idProduct);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, priceAtMoment);

            return stmt.executeUpdate() > 0;
        }
    }
}