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

/**
 * Repositório responsável pela persistência e consulta de produtos na tabela product.
 * As consultas fazem JOIN com flavor, flavor_level e size para montar o Product completo.
 * Abstrai o acesso a dados via JDBC.
 */
public class RepositoryProduct {
	
	 /**
     * SQL de INSERT de um produto.
     * Campos: name, id_flavor, id_size, base_price, description.
     */
    private static final String SQL_INSERT =
            "INSERT INTO product(name, id_flavor, id_size, base_price, description) VALUES (?, ?, ?, ?, ?)";
    
    /**
     * SQL de DELETE de um produto por id.
     */
    private static final String SQL_DELETE =
            "DELETE FROM product WHERE id = ?";
    
    /**
     * SQL de SELECT de um produto por id.
     * Faz JOIN com flavor, flavor_level e size.
     * Usa aliases para mapeamento no método mapResultSetToProduct.
     */
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
    
    /**
     * SQL de SELECT de todos os produtos.
     * Faz JOIN com flavor, flavor_level e size.
     * Ordena por id desc (mais recentes primeiro).
     */
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
    
    /**
     * Insere um produto.
     * Requer que product.getFlavor().getId() e product.getSize().getId() existam, pois são FKs.
     * Abre conexão., prepara SQL_INSERT, preenche parâmetros, executa e retorna true se inseriu.
     *
     * @param product produto a inserir (não nulo; deve conter flavor e size com id)
     * @return true se inseriu ao menos uma linha
     * @throws SQLException em erro de acesso ao banco
     */
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
    
    /**
     * Remove um produto pelo id.
     * Abre conexão, prepara SQL_DELETE, define o id, executa e retorna true se removeu.
     *
     * @param product produto a remover (deve ter id não nulo)
     * @return true se removeu ao menos uma linha
     * @throws SQLException em erro de acesso ao banco
     */
    public boolean deleteProduct(Product product) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, product.getId());
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Busca um produto por id, retornando Product com Flavor, FlavorLevel e Size preenchidos.
     * Abre conexão, prepara SQL_FIND_BY_ID, define id, executa.
     * Se existir, mapeia via mapResultSetToProduct; senão null.
     *
     * @param id id do produto
     * @return Product encontrado ou null
     * @throws SQLException em erro de acesso ao banco
     */
    public Product findByIdProduct(Integer id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToProduct(rs) : null;
            }
        }
    }
    
    /**
     * Lista todos os produtos, retornando Product com Flavor, FlavorLevel e Size preenchidos.
     * Abre conexão, prepara SQL_FIND_ALL, executa.
     * Para cada linha, mapeia via mapResultSetToProduct.
     *
     * @return lista de produtos (nunca null, pode ser vazia)
     * @throws SQLException em erro de acesso ao banco
     */
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

    
    /**
     * Mapeia a linha atual do ResultSet para um objeto Product.
     * Espera aliases definidos em SQL_FIND_BY_ID/SQL_FIND_ALL.
     * Cria FlavorLevel, cria Flavor apontando para o nível, cria Size, cria Product apontando para flavor e size, define id do product e do flavor.
     *
     * @param rs ResultSet posicionado na linha
     * @return Product mapeado (com flavor/level e size)
     * @throws SQLException em erro ao ler colunas
     */
    private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
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

        Size size = new Size(
                rs.getInt("size_id"),
                rs.getString("size_name"),
                rs.getString("size_yield"),
                rs.getString("size_weight"),
                rs.getDouble("size_price")
        );

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