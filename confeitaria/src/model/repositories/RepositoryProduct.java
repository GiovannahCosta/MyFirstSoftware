package model.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.Flavor;
import model.entities.FlavorLevel;
import model.entities.Product;
import model.entities.Size;


/**
     * Repositório responsável pela persistência e consulta de produtos
     * na tabela {@code product}. Abstrai o acesso a dados via JDBC.
     * A tabela product referencia {@code flavor} via id_flavor
     */

public class RepositoryProduct {

    /**
     * INSERT na tabela product. Exige que o flavor já exista (id_flavor = flavor.id)
     */
    
    private static final String SQL_INSERT = 
            "INSERT INTO product(name, id_flavor, id_size, base_price, description) VALUES (?, ?, ?, ?, ?)";
    


    /**
     * SELECT por id com JOIN em flavor (para preencher o objeto Flavor do produto), 
     * em size (para preencher o objeto Size do produto),
     * e em flavor_level (para preencher o objeto FlavorLevel do Flavor)
     */

    private static final String SQL_FIND_BY_ID = 
            "SELECT p.id, p.name, p.id_flavor, p.id_size, p.base_price, p.size, p.description, "
            + "f.id AS flavor_id, f.name AS flavor_name, f.level AS flavor_level, "
            + "f.description AS flavor_description, s.id AS size_id, s.name AS size_name, s.yield AS size_yield, "
            + "s.weight AS size_weight, s.price AS size_price, fl.id AS flavor_level_id, "
            + "fl.name AS flavor_level_name, fl.price AS flavor_level_price "
            + "FROM product p "
            + "INNER JOIN flavor f ON f.id = p.id_flavor "
            + "INNER JOIN size s ON s.id = p.id_size"
            + "INNER JOIN flavorLevel fl ON fl.id = f.id_flavor_level"
            + "WHERE p.id = ?";
    
    
    /** 
     * SELECT de todos os registros com JOIN em flavor, em size e em flavor_level
     */

    private static final String SQL_FIND_ALL = 
            "SELECT p.id, p.name, p.id_flavor, p.id_size, p.base_price, p.size, p.description, "
            + "f.id AS flavor_id, f.name AS flavor_name, f.level AS flavor_level, "
            + "f.description AS flavor_description, s.id AS size_id, s.name AS size_name, s.yield AS size_yield, "
            + "s.weight AS size_weight, s.price AS size_price, fl.id AS flavor_level_id, "
            + "fl.name AS flavor_level_name, fl.price AS flavor_level_price "
            + "FROM product p "
            + "INNER JOIN flavor f ON f.id = p.id_flavor "
            + "INNER JOIN size s ON s.id = p.id_size"
            + "INNER JOIN flavorLevel fl ON fl.id = f.id_flavor_level";
    

    /**
     * DELETE por id.
     */

    private static final String SQL_DELETE =
            "DELETE FROM product WHERE id = ?";
    
    
    /**
     * Insere um novo produto. 
     * @param product produto a ser persistido (não nulo; getFlavor() e getId() do sabor, 
     * e getSize() e getId() do tamanho não nulos)
     * @return true se pelo menos uma linha foi inserida
     * @throws SQLException em erro de acesso ao banco
     */
    
    public boolean createProduct(Product product) throws SQLException{
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)){
            stmt.setString(1, product.getName());
            stmt.setInt(2, product.getFlavor().getId());
            stmt.setDouble(3, product.getBasePrice());
            stmt.setInt(4, product.getSize().getId());
            stmt.setString(5, product.getDescription());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;   
        }

    }

    
    /**
     * Remove um produto pelo id
     * @param product produto a ser removido (getId() deve ser o id da tabela product)
     * @return true se pelo menos uma linha foi removida
     * @throws SQLException em erro de acesso ao banco
     */
    public boolean deleteProduct(Product product) throws SQLException{
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)){
            stmt.setInt(1, product.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
            }
    }


   /**
    * Busca produto pelo identificador
    * @param id identificador do produto
    * @return o produto encontrado ou null se não existir
    * @throws SQLException em erro de acesso ao banco
    */
    public Product findByIdProduct(Integer id) throws SQLException{
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return mapResultSetToProduct(rs);
                } 
                return null;
            }
        }
    }



    /**
     * Lista todos os produtos
     * 
     * @return lista de produtos (nunca null, mas pode ser vazia)
     * @throws SQLException em erro de acesso ao banco
     */
    public List<Product> findAllProduct() throws SQLException{
        List<Product> list = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
                    ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                list.add(mapResultSetToProduct(rs));
            }
        }

        return list;
    }


    /**
     * Mapeia a linha atual do ResultSet para um objeto Product.
	 * Espera colunas p.id, p.name, p.size, p.base_price, p.description,
	 * e flavor_id, flavor_level, flavor_description (do JOIN com flavor).
     * @param rs ResultSet posicionado na linha desejada
     * @return instância de Product preenchida com os dados da linha
     * @throws SQLException em erro ao ler colunas
     */
    public Product mapResultSetToProduct(ResultSet rs) throws SQLException{
        Product product = new Product();
        product.setId(rs.getInt("id"));
        product.setName(rs.getString("name"));
        product.setBasePrice(rs.getObject("base_price") != null ? rs.getDouble("base_price") : null);
        product.setDescription(rs.getString("description"));

        Flavor flavor = new Flavor();
        flavor.setId(rs.getInt("flavor_id"));
        flavor.setDescription(rs.getString("flavor_description"));

        Size size = new Size();
        size.setId(rs.getInt("size_id"));
        size.setName(rs.getString("size_name"));
        size.setYield(rs.getString("size_yield"));
        size.setWeight(rs.getString("size_weight"));
        size.setPrice(rs.getObject("size_price") != null ? rs.getDouble("size_price") : null);
        

        FlavorLevel level = new FlavorLevel();
        level.setId(rs.getInt("flavor_level_id"));
        level.setName(rs.getString("flavor_level_name"));
        level.setPrice(rs.getObject("flavor_level_price") != null ? rs.getDouble("flavor_level_price") : null);


        return product;

    }


}
