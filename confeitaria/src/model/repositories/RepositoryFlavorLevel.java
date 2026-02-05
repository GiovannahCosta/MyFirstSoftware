package model.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.FlavorLevel;

/**
 * Repositório responsável pela persistência e consulta de níveis de sabores
 * na tabela {@code flavor_level}. Abstrai o acesso a dados via JDBC.
 */

public class RepositoryFlavorLevel {

    /**
     * INSERT na tabela flavor. Exige que o flavor_level já exista (id_flavor = flavor_level.id)
     */
    public static final String SQL_INSERT = 
            "INSERT INTO flavor_level(name, price) VALUES (?, ?)";
    
    
    /**
     * DELETE por id.
     */
    public static final String SQL_DELETE =
            "DELETE FROM flavor_level WHERE id = ?";


    /**
     * SELECT por id
     */
    public static final String SQL_FIND_BY_ID = 
            "SELECT id, name, price "
            + "FROM flavor_level "
            + "WHERE id = ?";
    
    /**
	 * SELECT por nome.
	 */
	private static final String SQL_FIND_BY_NAME =
			"SELECT id, name, price "
            + "FROM flavor_level "
            + "WHERE name = ?";
    
    /**
	 * SELECT de todos os registros.
	 */
	private static final String SQL_FIND_ALL =
			"SELECT id, name, price FROM flavor_level";
    
    
    /**
	 * Insere um novo nível de sabor. Se o nome já existir (UNIQUE), nenhuma linha é inserida.
	 *
	 * @param flavor_level nível de sabor a ser persistido (não nulo)
	 * @return true se pelo menos uma linha foi inserida, false caso contrário (ex.: nome duplicado)
	 * @throws SQLException em erro de acesso ao banco
	 */
	public boolean createFlavorLevel(FlavorLevel level) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
			stmt.setString(1, level.getName());
			stmt.setDouble(2, level.getPrice());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		}
	}

    /**
	 * Remove um nível de sabor pelo id.
	 *
	 * @param flavor_level nível de sabor a ser removido (getId() deve ser o id da tabela flavor_level)
	 * @return true se pelo menos uma linha foi removida
	 * @throws SQLException em erro de acesso ao banco
	 */
	public boolean deleteFlavorLevel(FlavorLevel level) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
			stmt.setInt(1, level.getId());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		}
	}


    /**
	 * Busca nível de sabor pelo identificador.
	 *
	 * @param id identificador do nível de sabor
	 * @return o nível sabor encontrado ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public FlavorLevel findByIdFlavorLevel(Integer id) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
			stmt.setInt(1, id);
			try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return mapResultSetToFlavorLevel(rs);
                }

                return null;
            }
		}
	}

    /**
	 * Busca nível de sabor pelo nome.
	 *
	 * @param name nome do nível sabor
	 * @return o nível sabor encontrado ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public FlavorLevel findByNameFlavorLevel(String name) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_NAME)) {
			stmt.setString(1, name);
			try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return mapResultSetToFlavorLevel(rs);
                }

                return null;
            }
		}
	}

    /**
	 * Lista todas os níveis de sabor.
	 *
	 * @return lista de níveis sabor (nunca null, pode ser vazia)
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public List<FlavorLevel> findAllFlavorLevel() throws SQLException {
		List<FlavorLevel> list = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				list.add(mapResultSetToFlavorLevel(rs));
			}
		}
		return list;
	}

    /**
	 * Mapeia a linha atual do ResultSet para um objeto FlavorLevel.
	 * Não avança o cursor; espera-se que o chamador tenha posicionado com next().
	 *
	 * @param rs ResultSet posicionado na linha desejada
	 * @return instância de FlavorLevel preenchida com os dados da linha
	 * @throws SQLException em erro ao ler colunas
	 */
	private FlavorLevel mapResultSetToFlavorLevel(ResultSet rs) throws SQLException {
		FlavorLevel level = new FlavorLevel();
		level.setId(rs.getInt("id"));
		level.setName(rs.getString("name"));
		level.setPrice(rs.getDouble("price"));
		return level;
	}
    
}
