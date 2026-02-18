package model.repositories;

import model.entities.Size;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório responsável pela persistência e consulta de tamanhos na tabela "size".
 * Abstrai o acesso a dados via JDBC para operações de criação, remoção e consulta.
 */
public class RepositorySize {
	
	/**
     * SQL de INSERT na tabela "size".
     * Campos: name, yield, weight, price.
     */
    public static final String SQL_INSERT =
            "INSERT INTO \"size\"(name, yield, weight, price) VALUES (?, ?, ?, ?)";
    
    /**
     * SQL de DELETE de um tamanho por id.
     */
    public static final String SQL_DELETE =
            "DELETE FROM \"size\" WHERE id = ?";
    
    /**
     * SQL de SELECT de um tamanho por id.
     * Usa aliases size_* para padronizar o mapeamento.
     */
    public static final String SQL_FIND_BY_ID =
            "SELECT id AS size_id, name AS size_name, yield AS size_yield, weight AS size_weight, price AS size_price "
                    + "FROM \"size\" WHERE id = ?";
    
    /**
     * SQL de SELECT de um tamanho por nome.
     * Útil para seed e validações (evitar duplicidade por nome).
     */
    private static final String SQL_FIND_BY_NAME =
            "SELECT id AS size_id, name AS size_name, yield AS size_yield, weight AS size_weight, price AS size_price "
                    + "FROM \"size\" WHERE name = ?";
    
    /**
     * SQL de SELECT de todos os tamanhos.
     * Ordena por id para exibição consistente.
     */
    private static final String SQL_FIND_ALL =
            "SELECT id AS size_id, name AS size_name, yield AS size_yield, weight AS size_weight, price AS size_price "
                    + "FROM \"size\" ORDER BY id";

    
    /**
     * Insere um novo tamanho.
     * Abre conexão, prepara SQL_INSERT, preenche name/yield/weight/price, executa e retorna true se inseriu.
     *
     * @param size tamanho a inserir (não nulo)
     * @return true se inseriu ao menos uma linha
     * @throws SQLException em erro de acesso ao banco
     */
    public boolean createSize(Size size) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setString(1, size.getName());
            stmt.setString(2, size.getYield());
            stmt.setString(3, size.getWeight());
            stmt.setDouble(4, size.getPrice());
            return stmt.executeUpdate() > 0;
        }
    }
    
    
    /**
     * Remove um tamanho pelo id.
     * Abre conexão, prepara SQL_DELETE, define o id, executa e retorna true se removeu.
     *
     * @param size tamanho a remover (deve ter id não nulo)
     * @return true se removeu ao menos uma linha
     * @throws SQLException em erro de acesso ao banco
     */
    public boolean deleteSize(Size size) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, size.getId());
            return stmt.executeUpdate() > 0;
        }
    }

    
    /**
     * Busca um tamanho por id.
     * Abre conexão, prepara SQL_FIND_BY_ID, define id, executa.
     * Se existir linha, mapeia via mapResultSetToSize; senão null.
     *
     * @param id id do tamanho
     * @return Size encontrado ou null
     * @throws SQLException em erro de acesso ao banco
     */
    public Size findByIdSize(Integer id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToSize(rs) : null;
            }
        }
    }
    
    /**
     * Busca um tamanho por nome.
     * Abre conexão, prepara SQL_FIND_BY_NAME, define o nome,, executa.
     * Se existir linha, mapeia via mapResultSetToSize; senão null.
     *
     * @param name nome do tamanho
     * @return Size encontrado ou null
     * @throws SQLException em erro de acesso ao banco
     */
    public Size findByNameSize(String name) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_NAME)) {

            stmt.setString(1, name);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToSize(rs) : null;
            }
        }
    }
    
    /**
     * Lista todos os tamanhos.
     * Abre conexão, prepara SQL_FIND_ALL, executa.
     * Mapeia cada linha via mapResultSetToSize.
     *
     * @return lista de tamanhos (nunca null, pode ser vazia)
     * @throws SQLException em erro de acesso ao banco
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
     * Mapeia a linha atual do ResultSet para um objeto Size.
     * Espera colunas com aliases size_id, size_name, size_yield, size_weight, size_price.
     *
     * @param rs ResultSet posicionado na linha
     * @return Size mapeado
     * @throws SQLException em erro ao ler colunas
     */
    private Size mapResultSetToSize(ResultSet rs) throws SQLException {
        return new Size(
                rs.getInt("size_id"),
                rs.getString("size_name"),
                rs.getString("size_yield"),
                rs.getString("size_weight"),
                rs.getDouble("size_price")
        );
    }
}