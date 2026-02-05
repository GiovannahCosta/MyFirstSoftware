package model.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import model.entities.Address;
import model.entities.Area;
import model.entities.Person;

/**
 * Repositório responsável pela persistência e consulta de pessoas
 * na tabela {@code person}. Abstrai o acesso a dados via JDBC.
 * A tabela person referencia {@code address} via id_address.
 */
public class RepositoryPerson {

	/**
	 * INSERT na tabela person. Em caso de conflito em email (UNIQUE),
	 * a linha não é inserida (ON CONFLICT DO NOTHING).
	 * Exige que o endereço já exista (id_address = address.id).
	 */
	private static final String SQL_INSERT =
			"INSERT INTO person(first_name, last_name, email, id_address) VALUES(?, ?, ?, ?) "
			+ "ON CONFLICT (email) DO NOTHING";

	/**
	 * SELECT por id com JOIN em address e area (para preencher Address e Area na Person).
	 */
	private static final String SQL_FIND_BY_ID =
			"SELECT p.id AS person_id, p.first_name, p.last_name, p.email, p.id_address, "
			+ "a.id AS address_id, a.id_area, a.cep, a.street, a.number, a.complement, a.reference, "
			+ "ar.id AS area_id, ar.name AS area_name, ar.fee AS area_fee "
			+ "FROM person p "
            + "INNER JOIN address a ON a.id = p.id_address "
			+ "INNER JOIN area ar ON ar.id = a.id_area WHERE p.id = ?";

	/**
	 * SELECT por email com JOIN em address e area.
	 */
	private static final String SQL_FIND_BY_EMAIL =
			"SELECT p.id AS person_id, p.first_name, p.last_name, p.email, p.id_address, "
			+ "a.id AS address_id, a.id_area, a.cep, a.street, a.number, a.complement, a.reference, "
			+ "ar.id AS area_id, ar.name AS area_name, ar.fee AS area_fee "
			+ "FROM person p INNER JOIN address a ON a.id = p.id_address "
			+ "INNER JOIN area ar ON ar.id = a.id_area WHERE p.email = ?";

	/**
	 * SELECT de todos os registros com JOIN em address e area.
	 */
	private static final String SQL_FIND_ALL =
			"SELECT p.id AS person_id, p.first_name, p.last_name, p.email, p.id_address, "
			+ "a.id AS address_id, a.id_area, a.cep, a.street, a.number, a.complement, a.reference, "
			+ "ar.id AS area_id, ar.name AS area_name, ar.fee AS area_fee "
			+ "FROM person p INNER JOIN address a ON a.id = p.id_address "
			+ "INNER JOIN area ar ON ar.id = a.id_area";

	/**
	 * DELETE por id.
	 */
	private static final String SQL_DELETE = "DELETE FROM person WHERE id = ?";

	/**
	 * Insere uma nova person. O endereço deve já existir (person.getAddress().getInteger() != null).
	 * Se o email já existir, nenhuma linha é inserida.
	 *
	 * @param person pessoa a ser persistida (não nula; getAddress() e id do endereço não nulos)
	 * @return true se pelo menos uma linha foi inserida, false caso contrário (ex.: email duplicado)
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public boolean createPerson(Person person) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
			stmt.setString(1, person.getFirstName());
			stmt.setString(2, person.getLastName());
			stmt.setString(3, person.getEmail());
			stmt.setInt(4, person.getAddress().getInteger());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		}
	}

	/**
	 * Insere uma nova person e retorna o id gerado. Se o email já existir, nenhuma linha é inserida.
	 *
	 * @param person pessoa a ser persistida (não nula; getAddress().getInteger() não nulo)
	 * @return id da person criada ou null se email duplicado / erro
	 */
	public Integer createPersonAndReturnId(Person person) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setString(1, person.getFirstName());
			stmt.setString(2, person.getLastName());
			stmt.setString(3, person.getEmail());
			stmt.setInt(4, person.getAddress().getInteger());
			if (stmt.executeUpdate() == 0) return null;
			try (ResultSet rs = stmt.getGeneratedKeys()) {
				return rs.next() ? rs.getInt(1) : null;
			}
		}
	}

	/**
	 * Remove uma person pelo id.
	 *
	 * @param person pessoa a ser removida (getId() = id da tabela person)
	 * @return true se pelo menos uma linha foi removida
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public boolean deletePerson(Person person) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
			stmt.setInt(1, person.getId());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		}
	}

	/**
	 * Busca person pelo identificador.
	 *
	 * @param id identificador da pessoa
	 * @return a pessoa encontrada ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public Person findByIdPerson(Integer id) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapResultSetToPerson(rs) : null;
			}
		}
	}

	/**
	 * Busca person pelo email.
	 *
	 * @param email email da pessoa
	 * @return a pessoa encontrada ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public Person findByEmailPerson(String email) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_EMAIL)) {
			stmt.setString(1, email);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapResultSetToPerson(rs) : null;
			}
		}
	}

	/**
	 * Lista todas as pessoas.
	 *
	 * @return lista de pessoas (nunca null, pode ser vazia)
	 * @throws SQLException em erro de acesso ao banco
	 */
	public List<Person> findAllPerson() throws SQLException {
		List<Person> list = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				list.add(mapResultSetToPerson(rs));
			}
		}
		return list;
	}

	/**
	 * Mapeia a linha atual do ResultSet para um objeto Person.
	 * Espera colunas com aliases person_id, first_name, last_name, email, id_address,
	 * address_id, id_area, cep, street, number, complement, reference,
	 * area_id, area_name, area_fee (do JOIN com address e area).
	 *
	 * @param rs ResultSet posicionado na linha desejada
	 * @return instância de Person preenchida com os dados da linha (incluindo Address e Area)
	 * @throws SQLException em erro ao ler colunas
	 */
	private Person mapResultSetToPerson(ResultSet rs) throws SQLException {
		Person person = new Person();
		person.setId(rs.getInt("person_id"));
		person.setFirstName(rs.getString("first_name"));
		person.setLastName(rs.getString("last_name"));
		person.setEmail(rs.getString("email"));

		Address address = new Address();
		address.setInteger(rs.getInt("address_id"));
		address.setCep(rs.getString("cep"));
		address.setStreet(rs.getString("street"));
		address.setNumber(rs.getObject("number") != null ? rs.getInt("number") : null);
		address.setComplement(rs.getString("complement"));
		address.setReference(rs.getString("reference"));

		Area area = new Area();
		area.setId(rs.getInt("area_id"));
		area.setName(rs.getString("area_name"));
		area.setFee(rs.getDouble("area_fee"));
		address.setArea(area);

		person.setAddress(address);
		return person;
	}
}

