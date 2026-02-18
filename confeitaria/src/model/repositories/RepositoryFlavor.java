package model.repositories;

import model.entities.Flavor;
import model.entities.FlavorLevel;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

/**
 * Repositório responsável pela persistência e consulta de sabores na tabela flavor.
 * Também faz JOIN com flavor_level para montar o objeto Flavor completo (com seu nível).
 * Abstrai o acesso a dados via JDBC para operações de criação, remoção e consulta.
 */
public class RepositoryFlavor {
	
	/**
     * SQL de INSERT de um sabor.
     * Usado nos métodos createFlavor e createFlavorAndReturnId.
     * Campos esperados: name, id_flavor_level, description.
     */
    public static final String SQL_INSERT =
            "INSERT INTO flavor(name, id_flavor_level, description) VALUES (?, ?, ?)";
    
    /**
     * SQL de DELETE de um sabor por id.
     * Usado em deleteFlavor.
     */
    public static final String SQL_DELETE =
            "DELETE FROM flavor WHERE id = ?";
    
    /**
     * SQL de DELETE de um sabor por id.
     * Usado em deleteFlavor.
     */
    public static final String SQL_FIND_BY_ID =
            "SELECT "
                    + "f.id AS flavor_id, f.name AS flavor_name, f.description AS flavor_description, "
                    + "fl.id AS flavor_level_id, fl.name AS flavor_level_name, fl.price AS flavor_level_price "
                    + "FROM flavor f "
                    + "INNER JOIN flavor_level fl ON fl.id = f.id_flavor_level "
                    + "WHERE f.id = ?";
    
    /**
     * SQL de SELECT de todos os sabores.
     * Faz JOIN com flavor_level e ordena por id desc (mais recentes primeiro).
     */
    public static final String SQL_FIND_ALL =
            "SELECT "
                    + "f.id AS flavor_id, f.name AS flavor_name, f.description AS flavor_description, "
                    + "fl.id AS flavor_level_id, fl.name AS flavor_level_name, fl.price AS flavor_level_price "
                    + "FROM flavor f "
                    + "INNER JOIN flavor_level fl ON fl.id = f.id_flavor_level "
                    + "ORDER BY f.id DESC";
    
    /**
     * Insere um novo sabor na tabela flavor.
     * Requer que flavor.getLevel() exista e possua id válido, pois id_flavor_level é FK.
     * Abre conexão com DBConnection, prepara SQL_INSERT, preenche parâmetros (name, id do nível, description), executa e retorna true se inseriu ao menos uma linha.
     * @param flavor sabor a ser inserido (não nulo; deve conter nível com id)
     * @return true se inseriu ao menos uma linha
     * @throws SQLException em erro de acesso ao banco
     */
    public boolean createFlavor(Flavor flavor) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setString(1, flavor.getName());
            stmt.setInt(2, flavor.getLevel().getId());
            stmt.setString(3, flavor.getDescription());
            return stmt.executeUpdate() > 0;
        }
    }
    
    
    /**
     * Insere um novo sabor e retorna o id gerado.
     * Abre conexão, prepara SQL_INSERT com RETURN_GENERATED_KEYS, executa insert.
     * Se não inseriu, retorna null.
     * Lê o primeiro generated key e retorna como Integer.
     *
     * @param flavor sabor a ser inserido (não nulo; deve conter nível com id)
     * @return id gerado ou null se não inserir
     * @throws SQLException em erro de acesso ao banco
     */
    public Integer createFlavorAndReturnId(Flavor flavor) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setString(1, flavor.getName());
            stmt.setInt(2, flavor.getLevel().getId());
            stmt.setString(3, flavor.getDescription());

            if (stmt.executeUpdate() == 0) return null;

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }
    
    /**
     * Remove um sabor da tabela flavor pelo id.
     * Abre conexão, prepara SQL_DELETE, define o id do sabor, executa e retorna true se removeu ao menos uma linha.
     *
     * @param flavor sabor a remover (deve ter id não nulo)
     * @return true se removeu ao menos uma linha
     * @throws SQLException em erro de acesso ao banco
     */
    public boolean deleteFlavor(Flavor flavor) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_DELETE)) {

            stmt.setInt(1, flavor.getId());
            return stmt.executeUpdate() > 0;
        }
    }
    
    /**
     * Busca um sabor pelo id, retornando Flavor com FlavorLevel preenchido.
     * Abre conexão, prepara SQL_FIND_BY_ID, define id, executa.
     * Se existir linha, mapeia via mapResultSetToFlavor; caso contrário retorna null.
     *
     * @param id id do sabor
     * @return Flavor encontrado ou null
     * @throws SQLException em erro de acesso ao banco
     */
    public Flavor findByIdFlavor(Integer id) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_BY_ID)) {

            stmt.setInt(1, id);
            try (ResultSet rs = stmt.executeQuery()) {
                return rs.next() ? mapResultSetToFlavor(rs) : null;
            }
        }
    }
    
    /**
     * Lista todos os sabores cadastrados, retornando Flavor com FlavorLevel preenchido.
     * Abre conexão, prepara SQL_FIND_ALL, executa.
     * Para cada linha, mapeia via mapResultSetToFlavor e adiciona em lista.
     *
     * @return lista de sabores (nunca null, pode ser vazia)
     * @throws SQLException em erro de acesso ao banco
     */
    public List<Flavor> findAllFlavor() throws SQLException {
        List<Flavor> list = new ArrayList<>();
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ALL);
             ResultSet rs = stmt.executeQuery()) {

            while (rs.next()) {
                list.add(mapResultSetToFlavor(rs));
            }
        }
        return list;
    }
    
    /**
     * Converte a linha atual do ResultSet em um objeto Flavor.
     * Espera as colunas com aliases definidos em SQL_FIND_BY_ID/SQL_FIND_ALL.
     * Cria FlavorLevel com id, nome e preço, cria Flavor com nome, nível e descrição, define o id do Flavor.
     *
     * @param rs ResultSet já posicionado em uma linha válida
     * @return Flavor mapeado
     * @throws SQLException em erro ao ler colunas
     */
    private Flavor mapResultSetToFlavor(ResultSet rs) throws SQLException {
        FlavorLevel level = new FlavorLevel(
                rs.getInt("flavor_level_id"),
                rs.getString("flavor_level_name"),
                rs.getDouble("flavor_level_price")
        );

        Flavor flavor = new Flavor(
                rs.getString("flavor_name"),
                level,
                rs.getString("flavor_description")
        );
        flavor.setId(rs.getInt("flavor_id"));
        return flavor;
    }
}