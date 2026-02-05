package model.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.Size;

/**
 * Repositório responsável pela persistência e consulta de tamanhos
 * na tabela {@code size}. Abstrai o acesso a dados via JDBC.
 */
public class RepositorySize {

    /**
     * INSERT na tabela flavor. Exige que o flavor_level já exista (id_flavor = flavor_level.id)
     */
    public static final String SQL_INSERT = 
            "INSERT INTO size(name, yield, weight, price) VALUES (?, ?, ?, ?)";

    
    /**
     * DELETE por id.
     */
    public static final String SQL_DELETE =
            "DELETE FROM size WHERE id = ?";

    

    /**
     * SELECT por id
     */
    public static final String SQL_FIND_BY_ID = 
            "SELECT id, name, yield, weight, price "
            + "FROM size "
            + "WHERE id = ?";
    

    /**
	 * SELECT por nome.
	 */
	private static final String SQL_FIND_BY_NAME =
			"SELECT id, name, yield, weight, price "
            + "FROM size "
            + "WHERE name = ?";
    
    /**
	 * SELECT de todos os registros.
	 */
	private static final String SQL_FIND_ALL =
			"SELECT id, name, yield, weight, price FROM size";
    

    /**
	 * Insere um novo tamanho. Se o nome já existir (UNIQUE), nenhuma linha é inserida.
	 *
	 * @param size tamanho a ser persistido (não nulo)
	 * @return true se pelo menos uma linha foi inserida, false caso contrário (ex.: nome duplicado)
	 * @throws SQLException em erro de acesso ao banco
	 */
	public boolean createFlavorLevel(Size size) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {
            stmt.setString(1, size.getName());
            stmt.setString(2, size.getYield());
            stmt.setString(3, size.getWeight());
			stmt.setDouble(4, size.getPrice());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		}
	}

    /**
	 * Remove um tamanho pelo id.
	 *
	 * @param size tamanho a ser removido (getId() deve ser o id da tabela size)
	 * @return true se pelo menos uma linha foi removida
	 * @throws SQLException em erro de acesso ao banco
	 */
	public boolean deleteFlavorLevel(Size size) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {
			stmt.setInt(1, size.getId());
			int rowsAffected = stmt.executeUpdate();
			return rowsAffected > 0;
		}
	}


    /**
	 * Busca tamanho pelo identificador.
	 *
	 * @param id identificador do tamanho
	 * @return o tamanho encontrado ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public Size findByIdSize(Integer id) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {
			stmt.setInt(1, id);
			try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return mapResultSetToSize(rs);
                }

                return null;
            }
		}
	}

    /**
	 * Busca tamanho pelo nome.
	 *
	 * @param name nome do tamanho
	 * @return o tamanho encontrado ou null se não existir
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public Size findByNameSize(String name) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_NAME)) {
			stmt.setString(1, name);
			try(ResultSet rs = stmt.executeQuery()){
                if(rs.next()){
                    return mapResultSetToSize(rs);
                }

                return null;
            }
		}
	}


    /**
	 * Lista todas os tamanhos.
	 *
	 * @return lista de tamanhos (nunca null, pode ser vazia)
	 * @throws SQLException em erro de acesso ao banco de dados
	 */
	public List<Size> findAllSize() throws SQLException {
		List<Size> list = new ArrayList<>();
		try (Connection conn = DBConnection.getConnection();
				PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
				ResultSet rs = stmt.executeQuery()) {
			while (rs.next()) {
				list.add(mapResultSetToSize(rs));
			}
		}
		return list;
	}


    /**
	 * Mapeia a linha atual do ResultSet para um objeto size.
	 * Não avança o cursor; espera-se que o chamador tenha posicionado com next().
	 *
	 * @param rs ResultSet posicionado na linha desejada
	 * @return instância de Size preenchida com os dados da linha
	 * @throws SQLException em erro ao ler colunas
	 */
	private Size mapResultSetToSize(ResultSet rs) throws SQLException {
		Size size = new Size();
		size.setId(rs.getInt("id"));
		size.setName(rs.getString("name"));
        size.setYield(rs.getString("yield"));
        size.setWeight(rs.getString("weight"));
        size.setPrice(rs.getDouble("price"));
		return size;
	}
    
}
