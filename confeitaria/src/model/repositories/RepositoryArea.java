package model.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.Area;

/**
 * Repositório responsável pela persistência e consulta de áreas
 * na tabela {@code area}. Abstrai o acesso a dados via JDBC.
 */
public class RepositoryArea {

	/**
	 * INSERT na tabela area. Em caso de conflito em name (UNIQUE),
	 * a linha não é inserida (ON CONFLICT DO NOTHING).
	 */
	private static final String SQL_INSERT =
			"INSERT INTO area(name, fee) VALUES(?, ?) "
			+ "ON CONFLICT (name) DO NOTHING";

	/**
	 * SELECT por id.
	 */
	private static final String SQL_FIND_BY_ID =
			"SELECT id, name, fee FROM area WHERE id = ?";

	/**
	 * SELECT por nome.
	 */
	private static final String SQL_FIND_BY_NAME =
			"SELECT id, name, fee FROM area WHERE name = ?";

	/**
	 * SELECT de todos os registros.
	 */
	private static final String SQL_FIND_ALL =
			"SELECT id, name, fee FROM area";

	/**
	 * DELETE por id.
	 */
	private static final String SQL_DELETE = "DELETE FROM area WHERE id = ?";

	/**
	 * Insere uma nova área. Se o nome já existir (UNIQUE), nenhuma linha é inserida.
	 *
	 * @param area área a ser persistida (não nula)
	 * @return true se pelo menos uma linha foi inserida, false caso contrário (ex.: nome duplicado)
	 * @throws SQLException em erro de acesso ao banco
	 */
	public boolean createArea(Area area) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
			stmt.setString(1, area.getName());
			stmt.setDouble(2, area.getFee());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		}
	}

	/**
	 * Remove uma área pelo id.
	 *
	 * @param area área a ser removida (getId() deve ser o id da tabela area)
	 * @return true se pelo menos uma linha foi removida
	 * @throws SQLException em erro de acesso ao banco
	 */
	public boolean deleteArea(Area area) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
			stmt.setInt(1, area.getId());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		}
	}

	/**
	 * Busca área pelo identificador.
	 *
	 * @param id identificador da área
	 * @return a área encontrada ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public Area findByIdArea(Integer id) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapResultSetToArea(rs) : null;
			}
		}
	}

	/**
	 * Busca área pelo nome.
	 *
	 * @param name nome da área
	 * @return a área encontrada ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public Area findByNameArea(String name) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_NAME)) {
			stmt.setString(1, name);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapResultSetToArea(rs) : null;
			}
		}
	}

	/**
	 * Lista todas as áreas.
	 *
	 * @return lista de áreas (nunca null, pode ser vazia)
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public List<Area> findAllArea() throws SQLException {
		List<Area> list = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				list.add(mapResultSetToArea(rs));
			}
		}
		return list;
	}

	/**
	 * Mapeia a linha atual do ResultSet para um objeto Area.
	 * Não avança o cursor; espera-se que o chamador tenha posicionado com next().
	 *
	 * @param rs ResultSet posicionado na linha desejada
	 * @return instância de Area preenchida com os dados da linha
	 * @throws SQLException em erro ao ler colunas
	 */
	private Area mapResultSetToArea(ResultSet rs) throws SQLException {
		Area area = new Area();
		area.setId(rs.getInt("id"));
		area.setName(rs.getString("name"));
		area.setFee(rs.getDouble("fee"));
		return area;
	}
}
