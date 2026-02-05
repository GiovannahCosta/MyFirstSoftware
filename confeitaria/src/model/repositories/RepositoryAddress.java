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

/**
 * Repositório responsável pela persistência e consulta de endereços
 * na tabela {@code address}. Abstrai o acesso a dados via JDBC.
 * A tabela address referencia {@code area} via id_area.
 */
public class RepositoryAddress {

	/**
	 * INSERT na tabela address. Exige que a área já exista (id_area = area.id).
	 */
	private static final String SQL_INSERT =
			"INSERT INTO address(id_area, cep, street, number, complement, reference) VALUES(?, ?, ?, ?, ?, ?)";

	/**
	 * SELECT por id com JOIN em area (para preencher o objeto Area do endereço).
	 */
	private static final String SQL_FIND_BY_ID =
			"SELECT a.id, a.id_area, a.cep, a.street, a.number, a.complement, a.reference, "
			+ "area.id AS area_id, area.name AS area_name, area.fee AS area_fee "
			+ "FROM address a INNER JOIN area ON area.id = a.id_area WHERE a.id = ?";

	/**
	 * SELECT de todos os registros com JOIN em area.
	 */
	private static final String SQL_FIND_ALL =
			"SELECT a.id, a.id_area, a.cep, a.street, a.number, a.complement, a.reference, "
			+ "area.id AS area_id, area.name AS area_name, area.fee AS area_fee "
			+ "FROM address a "
			+ "INNER JOIN area ON area.id = a.id_area";

	/**
	 * DELETE por id.
	 */
	private static final String SQL_DELETE = "DELETE FROM address WHERE id = ?";

	/**
	 * Insere um novo endereço. A área deve já existir (address.getArea().getId() != null).
	 *
	 * @param address endereço a ser persistido (não nulo; getArea() e getId() da área não nulos)
	 * @return true se pelo menos uma linha foi inserida
	 * @throws SQLException em erro de acesso ao banco
	 */
	public boolean createAddress(Address address) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
			stmt.setInt(1, address.getArea().getId());
			stmt.setString(2, address.getCep());
			stmt.setString(3, address.getStreet());
			stmt.setObject(4, address.getNumber());
			stmt.setString(5, address.getComplement());
			stmt.setString(6, address.getReference());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		}
	}

	/**
	 * Insere um novo endereço e retorna o id gerado. A área deve já existir.
	 *
	 * @param address endereço a ser persistido (getArea().getId() != null)
	 * @return id do endereço criado ou null em caso de erro
	 */
	public Integer createAddressAndReturnId(Address address) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {
			stmt.setInt(1, address.getArea().getId());
			stmt.setString(2, address.getCep());
			stmt.setString(3, address.getStreet());
			stmt.setObject(4, address.getNumber());
			stmt.setString(5, address.getComplement());
			stmt.setString(6, address.getReference());
			if (stmt.executeUpdate() == 0) return null;
			try (ResultSet rs = stmt.getGeneratedKeys()) {
				return rs.next() ? rs.getInt(1) : null;
			}
		}
	}

	/**
	 * Remove um endereço pelo id.
	 *
	 * @param address endereço a ser removido (getInteger() deve ser o id da tabela address)
	 * @return true se pelo menos uma linha foi removida
	 * @throws SQLException em erro de acesso ao banco
	 */
	public boolean deleteAddress(Address address) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
			stmt.setInt(1, address.getInteger());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		}
	}

	/**
	 * Busca endereço pelo identificador.
	 *
	 * @param id identificador do endereço
	 * @return o endereço encontrado ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public Address findByIdAddress(Integer id) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
			stmt.setInt(1, id);
			try (ResultSet rs = stmt.executeQuery()) {
				return rs.next() ? mapResultSetToAddress(rs) : null;
			}
		}
	}

	/**
	 * Lista todos os endereços.
	 *
	 * @return lista de endereços (nunca null, pode ser vazia)
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public List<Address> findAllAddress() throws SQLException {
		List<Address> list = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				list.add(mapResultSetToAddress(rs));
			}
		}
		return list;
	}

	/**
	 * Mapeia a linha atual do ResultSet para um objeto Address.
	 * Espera colunas a.id, a.id_area, a.cep, a.street, a.number, a.complement, a.reference
	 * e area_id, area_name, area_fee (do JOIN com area).
	 *
	 * @param rs ResultSet posicionado na linha desejada
	 * @return instância de Address preenchida com os dados da linha
	 * @throws SQLException em erro ao ler colunas
	 */
	private Address mapResultSetToAddress(ResultSet rs) throws SQLException {
		Address address = new Address();
		address.setInteger(rs.getInt("id"));
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
		return address;
	}
}
