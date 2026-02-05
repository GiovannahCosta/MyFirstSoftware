package model.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.Flavor;
import model.entities.FlavorLevel;

/**
 * Repositório responsável pela persistência e consulta de sabores
 * na tabela {@code flavor}. Abstrai o acesso a dados via JDBC.
 * A tabela flavor referencia {@code flavor_level} via id_flavor_level
 */

public class RepositoryFlavor {

    /**
     * INSERT na tabela flavor. Exige que o flavor_level já exista (id_flavor = flavor_level.id)
     */
    public static final String SQL_INSERT = 
            "INSERT INTO flavor(name, id_flavor_level, description) VALUES (?, ?, ?)";


    /**
     * DELETE por id.
     */
    public static final String SQL_DELETE =
            "DELETE FROM flavor WHERE id = ?";


    /**
     * SELECT por id com JOIN em flavor_level (para preencher o objeto FlavorLevel do sabor)
     */
    public static final String SQL_FIND_BY_ID = 
            "SELECT f.id, f.name, f.id_flavor_level, f.description, "
            + "fl.id AS flavor_level_id, fl.name AS flavor_level_name, fl.price AS flavor_level_price "
            + "FROM flavor f "
            + "INNER JOIN flavor_level fl ON f.id_flavor_level = fl.id "
            + "WHERE f.id = ?";
    

    /** 
     * SELECT de todos os registros com JOIN em flavor_level
     */
    public static final String SQL_FIND_ALL =
            "SELECT f.id, f.name, f.id_flavor_level, f.description, "
            + "fl.id AS flavor_level_id, fl.name AS flavor_level_name, fl.price AS flavor_level_price "
            + "FROM flavor f "
            + "INNER JOIN flavor_level fl ON f.id_flavor_level = fl.id ";
    
    
    /**
     * Insere um novo sabor. 
     * @param flavor sabor a ser persistido (não nulo; getFlavorLevel() e getId() do nível sabor não nulos)
     * @return true se pelo menos uma linha foi inserida
     * @throws SQLException em erro de acesso ao banco
     */
    public boolean createFlavor(Flavor flavor) throws SQLException{
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)){
            stmt.setString(1, flavor.getName());
            stmt.setInt(2, flavor.getLevel().getId());
            stmt.setString(3, flavor.getDescription());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
                    
        }
    }

    /**
     * Remove um sabor pelo id
     * @param flavor sabor a ser removido (getId() deve ser o id da tabela flavor)
     * @return true se pelo menos uma linha foi removida
     * @throws SQLException em erro de acesso ao banco
     */
    public boolean deleteFlavor(Flavor flavor) throws SQLException{
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)){
            stmt.setInt(1, flavor.getId());
            int rowsAffected = stmt.executeUpdate();
            return rowsAffected > 0;
        }
    }

    /**
    * Busca sabor pelo identificador
    * @param id identificador do sabor
    * @return o sabor encontrado ou null se não existir
    * @throws SQLException em erro de acesso ao banco
    */
    public Flavor findByIdFlavor(Integer id) throws SQLException{
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)){
            stmt.setInt(1, id);
            try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return mapResultSetToFlavor(rs);
                }

                return null;
            }
        }
    } 

    /**
     * Lista todos os sabores
     * 
     * @return lista de sabores (nunca null, mas pode ser vazia)
     * @throws SQLException em erro de acesso ao banco
     */
    public List<Flavor> findAllFlavor() throws SQLException{
        List<Flavor> list = new ArrayList<>();
        try(Connection conn = DBConnection.getConnection();
                PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
                        ResultSet rs = stmt.executeQuery()){
            while(rs.next()){
                list.add(mapResultSetToFlavor(rs));
            }
                    
        }

        return list;
    }
    
    /**
     * Mapeia a linha atual do ResultSet para um objeto Flavor.
	 * Espera colunas f.id, f.name, f.description,
	 * e flavor_level_id, flavor_level_name, flavor_level_price (do JOIN com flavor_level).
     * @param rs ResultSet posicionado na linha desejada
     * @return instância de Flavor preenchida com os dados da linha
     * @throws SQLException em erro ao ler colunas
     */
    public Flavor mapResultSetToFlavor(ResultSet rs) throws SQLException{
        Flavor flavor = new Flavor();
        flavor.setId(rs.getInt("id"));
        flavor.setDescription(rs.getString("description"));

        FlavorLevel level = new FlavorLevel();
        level.setId(rs.getInt("flavor_level_id"));
        level.setName(rs.getString("flavor_level_name"));
        level.setPrice(rs.getDouble("flavor_level_price"));

        return flavor;
        
    }
}
