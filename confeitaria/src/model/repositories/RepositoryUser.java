package model.repositories;

import model.entities.User;
import java.sql.Connection;
import java.sql.SQLException; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.util.ArrayList;
import java.util.List;



/**
 * Repositório responsável pela persistência e consulta de usuários
 * na tabela {@code user}. Abstrai o acesso a dados via JDBC.
 * A tabela user referencia {@code person} via id_person.
 */

public class RepositoryUser {

	/**
	 * INSERT na tabela user. Em caso de conflito em id_person (UNIQUE),
	 * a linha não é inserida (ON CONFLICT DO NOTHING).
	 * Exige que a person já exista (id_person = person.id).
	 */
	private static final String SQL_INSERT =
            "INSERT INTO \"user\" (id_person, password_hash) VALUES (?, ?)";
	
	/**
	 * SELECT por id com JOIN em person (first_name, last_name, email).
	 */
	private static final String SQL_FIND_BY_ID =
			"SELECT u.id, u.id_person, u.password_hash, p.first_name, p.last_name, p.email "
			+ "FROM \"user\" u JOIN person p ON u.id_person = p.id WHERE u.id = ?";

	/**
	 * SELECT por email com JOIN em person.
	 */
	private static final String SQL_FIND_BY_EMAIL =
			"SELECT u.id, u.id_person, u.password_hash, p.first_name, p.last_name, p.email "
			+ "FROM \"user\" u JOIN person p ON u.id_person = p.id WHERE p.email = ?";

	/**
	 * SELECT de todos os usuários com JOIN em person.
	 */
	private static final String SQL_FIND_ALL =
			"SELECT u.id, u.id_person, u.password_hash, p.first_name, p.last_name, p.email "
			+ "FROM \"user\" u JOIN person p ON u.id_person = p.id";

	/**
	 * 
	 * DELETE de um usuário
	 */
	private static final String DELETE_FIND_BY_ID  = "DELETE FROM \"user\" WHERE id = ? ";


	/**
	 * Insere um novo usuário. A person deve já existir (user.getId() != null).
	 * Se o id_person já existir, nenhuma linha é inserida.
	 *
	 * @param user usuário a ser persistido (não nulo; getId() = id da person)
	 * @return true se pelo menos uma linha foi inserida, false caso contrário (ex.: id_person duplicado)
	 * @throws SQLException em erro de acesso ao banco
	 */
	public boolean createUser(User user) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
			stmt.setInt(1, user.getId());
            stmt.setString(2, user.getPasswordHash());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		}
	}

	/**
	 * Remove um usuário pelo id da tabela user.
	 *
	 * @param user usuário a ser removido (getIdUser() deve ser o id da tabela user)
	 * @return true se pelo menos uma linha foi removida
	 * @throws SQLException em erro de acesso ao banco
	 */
	public boolean deleteUser(User user) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(DELETE_FIND_BY_ID)) {
			stmt.setInt(1, user.getIdUser());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		}
	}
	
	/**
	 * Busca usuário pelo identificador.
	 * 
	 * @param id identificador do usuário
	 * @return O usuário encontrado ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados 
	 */
	public User findByIdUser(Integer id) throws SQLException{
		try(Connection conn = DBConnection.getConnection();
		PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)){
			stmt.setInt(1,id);
			try(ResultSet rs = stmt.executeQuery()){
				return rs.next() ? mapResultSetToUser(rs) : null;
			}
		}
	}


	/**
	 * Busca usuário pelo email.
	 * 
	 * @param email do usuário
	 * @return o usuário encontrado ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public User findByEmailUser(String email) throws SQLException{
		try(Connection conn = DBConnection.getConnection(); 
		PreparedStatement  stmt = conn.prepareStatement(SQL_FIND_BY_EMAIL)){
			stmt.setString(1,email);
			try(ResultSet rs= stmt.executeQuery()){
				return rs.next() ? mapResultSetToUser(rs) : null;
			}
		}
	}

	/**
	 * Lista todos os usuários
	 * @return lista de usuários (nunca null, pode ser vazia)
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public List<User> findAllUser() throws SQLException{
		List<User> list = new ArrayList<>();
		try(Connection conn = DBConnection.getConnection();
			PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
			ResultSet rs = stmt.executeQuery()){
				while(rs.next()){
					list.add(mapResultSetToUser(rs));
				}
		}

		return list;

	}

	/**
	 *  Mapeia a linha atual do ResultSet para um objeto User.
	 *  Não avança o cursor; espera-se que o chamador tenha posicionado next().
	 * 
	 * @param rs ResultSet posicionado na linha desejada
	 * @return instância de User preenchida com os dados da linha
	 * @throws SQLException em erro ao ler colunas
	 */
	private User mapResultSetToUser(ResultSet rs) throws SQLException{
		User user = new User();
		user.setIdUser(rs.getInt("id"));
		user.setId(rs.getInt("id_person"));
		user.setFirstName(rs.getString("first_name"));
		user.setLastName(rs.getString("last_name"));
		user.setEmail(rs.getString("email"));
		user.setPasswordHash(rs.getString("password_hash"));
		
		return user;
	}
	

}
