package model.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.FlavorLevel;

/**
 * Repositório responsável pela persistência e consulta de níveis de sabor
 * na tabela flavor_level.
 * Abstrai o acesso a dados via JDBC, centralizando SQL e mapeamento de ResultSet
 * para objetos do tipo FlavorLevel.
 */
public class RepositoryFlavorLevel {

    /**
     * SQL de inserção de um nível de sabor na tabela flavor_level.
     * Insere nome e preço.
     */
    public static final String SQL_INSERT =
            "INSERT INTO flavor_level(name, price) VALUES (?, ?)";

    /**
     * SQL de exclusão de um nível de sabor pelo id.
     */
    public static final String SQL_DELETE =
            "DELETE FROM flavor_level WHERE id = ?";

    /**
     * SQL de busca de um nível de sabor pelo id.
     */
    public static final String SQL_FIND_BY_ID =
            "SELECT id, name, price "
            + "FROM flavor_level "
            + "WHERE id = ?";

    /**
	 * SQL de busca de um nível de sabor pelo nome.
	 */
	private static final String SQL_FIND_BY_NAME =
			"SELECT id, name, price "
            + "FROM flavor_level "
            + "WHERE name = ?";

    /**
	 * SQL de listagem de todos os níveis de sabor.
	 */
	private static final String SQL_FIND_ALL =
			"SELECT id, name, price FROM flavor_level";

    /**
	 * Insere um novo nível de sabor.
	 *
     * Funcionamento:
     * 1. Abre conexão com o banco.
     * 2. Prepara o SQL_INSERT.
     * 3. Preenche os parâmetros (name e price).
     * 4. Executa o INSERT.
     * 5. Retorna true se alguma linha foi inserida.
     *
	 * Observação:
	 * Se o banco tiver uma restrição UNIQUE para o nome, uma tentativa de inserir um nome já existente
	 * pode resultar em falha (SQLException) dependendo da configuração do banco/driver.
	 *
	 * @param level nível de sabor a ser persistido (não nulo)
	 * @return true se pelo menos uma linha foi inserida, false caso contrário
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
     * Funcionamento:
     * 1. Abre conexão com o banco.
     * 2. Prepara o SQL_DELETE.
     * 3. Define o parâmetro id com level.getId().
     * 4. Executa o DELETE.
     * 5. Retorna true se alguma linha foi removida.
     *
	 * @param level nível de sabor a ser removido (getId() deve estar preenchido)
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
     * Funcionamento:
     * 1. Abre conexão com o banco.
     * 2. Prepara o SQL_FIND_BY_ID.
     * 3. Define o id como parâmetro.
     * 4. Executa a consulta.
     * 5. Se houver resultado, converte com mapResultSetToFlavorLevel.
     * 6. Se não houver, retorna null.
     *
	 * @param id identificador do nível de sabor
	 * @return o nível de sabor encontrado ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public FlavorLevel findByIdFlavorLevel(Integer id) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFlavorLevel(rs);
                }
                return null;
            }
		}
	}

    /**
	 * Busca nível de sabor pelo nome.
	 *
     * Funcionamento:
     * 1. Abre conexão com o banco.
     * 2. Prepara o SQL_FIND_BY_NAME.
     * 3. Define o nome como parâmetro.
     * 4. Executa a consulta.
     * 5. Se houver resultado, converte com mapResultSetToFlavorLevel.
     * 6. Se não houver, retorna null.
     *
	 * @param name nome do nível de sabor
	 * @return o nível de sabor encontrado ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public FlavorLevel findByNameFlavorLevel(String name) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_NAME)) {
			stmt.setString(1, name);
			try (ResultSet rs = stmt.executeQuery()) {
                if (rs.next()) {
                    return mapResultSetToFlavorLevel(rs);
                }
                return null;
            }
		}
	}

    /**
	 * Lista todos os níveis de sabor.
     *
     * Funcionamento:
     * 1. Abre conexão com o banco.
     * 2. Executa o SQL_FIND_ALL.
     * 3. Para cada linha do ResultSet, mapeia para FlavorLevel.
     * 4. Retorna a lista (pode ser vazia).
	 *
	 * @return lista de níveis de sabor (nunca null, pode ser vazia)
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
	 * Não avança o cursor; espera-se que o chamador já tenha posicionado com next().
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