package model.repositories;

import model.entities.Flavor;
import model.entities.FlavorLevel;
import model.entities.Product;
import model.entities.Size;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class RepositoryProduct {

    private static final String SQL_INSERT =
            "INSERT INTO product(name, id_flavor, id_size, base_price, description) VALUES (?, ?, ?, ?, ?)";

    private static final String SQL_DELETE =
            "DELETE FROM product WHERE id = ?";

    private static final String SQL_FIND_BY_ID =
            "SELECT "
                    + "p.id AS product_id, p.name AS product_name, p.base_price, p.description AS product_description, "
                    + "f.id AS flavor_id, f.name AS flavor_name, f.description AS flavor_description, "
                    + "fl.id AS flavor_level_id, fl.name AS flavor_level_name, fl.price AS flavor_level_price, "
                    + "s.id AS size_id, s.name AS size_name, s.yield AS size_yield, s.weight AS size_weight, s.price AS size_price "
                    + "FROM product p "
                    + "INNER JOIN flavor f ON f.id = p.id_flavor "
                    + "INNER JOIN flavor_level fl ON fl.id = f.id_flavor_level "
                    + "INNER JOIN size s ON s.id = p.id_size "
                    + "WHERE p.id = ?";

    private static final String SQL_FIND_ALL =
            "SELECT "
                    + "p.id AS product_id, p.name AS product_name, p.base_price, p.description AS product_description, "
                    + "f.id AS flavor_id, f.name AS flavor_name, f.description AS flavor_description, "
                    + "fl.id AS flavor_level_id, fl.name AS flavor_level_name, fl.price AS flavor_level_price, "
                    + "s.id AS size_id, s.name AS size_name, s.yield AS size_yield, s.weight AS size_weight, s.price AS size_price "
                    + "FROM product p "
                    + "INNER JOIN flavor f ON f.id = p.id_flavor "
                    + "INNER JOIN flavor_level fl ON fl.id = f.id_flavor_level "
                    + "INNER JOIN size s ON s.id = p.id_size "
                    + "ORDER BY p.id DESC";

    public boolean createProduct(Product product) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getFlavor().getId());
            stmt.setInt(3, product.getSize().getId());
            stmt.setDouble(4, product.getBasePrice());
            stmt.setString(5, product.getDescription());

            return stmt.executeUpdate() > 0;
        }
    }

    public boolean deleteProduct(Product product) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, product.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    public Product findByIdProduct(Integer id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToProduct(rs) : null;
            }
        }
    }

    public List<Product> findAllProduct() throws SQLException {
        List<Product> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToProduct(rs));
            }
        }

        return list;
    }

    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
        // FlavorLevel
        FlavorLevel level = new FlavorLevel(
                rs.getInt("flavor_level_id"),
                rs.getString("flavor_level_name"),
                rs.getDouble("flavor_level_price")
        );

        // Flavor
        Flavor flavor = new Flavor(
                rs.getString("flavor_name"),
                level,
                rs.getString("flavor_description")
        );
        flavor.setId(rs.getInt("flavor_id"));

        // Size
        Size size = new Size(
                rs.getInt("size_id"),
                rs.getString("size_name"),
                rs.getString("size_yield"),
                rs.getString("size_weight"),
                rs.getDouble("size_price")
        );

        // Product
        Product product = new Product(
                rs.getString("product_name"),
                flavor,
                size,
                rs.getDouble("base_price"),
                rs.getString("product_description")
        );
        product.setId(rs.getInt("product_id"));

        return product;
    }
}