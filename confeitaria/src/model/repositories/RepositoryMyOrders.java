package model.repositories;

import model.entities.OrderItemSummary;
import model.entities.OrderSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositoryMyOrders {

    private static final String SQL_FIND_ORDERS_BY_USER =
            "SELECT id, datetime, total_price, delivery, observations "
                    + "FROM \"order\" "
                    + "WHERE id_user = ? "
                    + "ORDER BY datetime DESC, id DESC";

    private static final String SQL_FIND_ITEMS_BY_ORDER =
            "SELECT oi.quantity, oi.price_at_moment, p.name AS product_name "
                    + "FROM order_items oi "
                    + "INNER JOIN product p ON p.id = oi.id_product "
                    + "WHERE oi.id_order = ? "
                    + "ORDER BY oi.id";

    public List<OrderSummary> findOrdersByUser(Integer idUser) throws SQLException {
        List<OrderSummary> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ORDERS_BY_USER)) {

            stmt.setInt(1, idUser);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new OrderSummary(
                            rs.getInt("id"),
                            rs.getTimestamp("datetime"),
                            rs.getDouble("total_price"),
                            rs.getString("delivery"),
                            rs.getString("observations")
                    ));
                }
            }
        }

        return list;
    }

    public List<OrderItemSummary> findItemsByOrder(Integer idOrder) throws SQLException {
        List<OrderItemSummary> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ITEMS_BY_ORDER)) {

            stmt.setInt(1, idOrder);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new OrderItemSummary(
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price_at_moment")
                    ));
                }
            }
        }

        return list;
    }
}