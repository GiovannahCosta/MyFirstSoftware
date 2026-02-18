package model.repositories;

import java.sql.*;

/**
 * Repositório responsável por persistência do pedido (cabeçalho) na tabela "order".
 *
 * Motivo de ter apenas um método:
 * O fluxo atual da aplicação precisa apenas criar o pedido e obter o id gerado,
 * pois esse id é necessário para inserir os itens em order_items.
 *
 * Motivo de não ter CRUD completo como outros repositories:
 * O projeto atual trata "order" como registro gerado no checkout e depois consultado por telas de relatório
 * (Meus Pedidos) através do RepositoryMyOrders.
 * 
 * Métodos adicionais (find/update/delete) devem ser criados somente se houver novos requisitos
 * como admin de pedidos, cancelamento, mudança de status, filtros, ou relatórios adicionais.
 */
public class RepositoryOrder {
	
	/**
     * SQL de INSERT na tabela "order".
     * Campos: id_user, datetime, total_price, delivery, observations.
     */
    private static final String SQL_INSERT =
            "INSERT INTO \"order\" (id_user, datetime, total_price, delivery, observations) "
                    + "VALUES (?, ?, ?, ?, ?)";
    
    /**
     * Insere um pedido e retorna o id gerado.
     * Abre conexão, prepara SQL_INSERT com RETURN_GENERATED_KEYS, define os parâmetros do pedido, executa o insert.
     * Se não inserir nenhuma linha, retorna null.
     * Lê o id gerado e retorna.
     *
     * @param idUser id do usuário (FK para "user")
     * @param datetime data/hora do pedido
     * @param totalPrice total do pedido
     * @param delivery forma de recebimento
     * @param observations observações (pode ser null)
     * @return id do pedido criado ou null se não inserir
     * @throws SQLException em erro de acesso ao banco
     */
    public Integer createOrderAndReturnId(Integer idUser, Timestamp datetime, Double totalPrice,
                                          String delivery, String observations) throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT, Statement.RETURN_GENERATED_KEYS)) {

            stmt.setInt(1, idUser);
            stmt.setTimestamp(2, datetime);
            stmt.setDouble(3, totalPrice);
            stmt.setString(4, delivery);
            stmt.setString(5, observations);

            if (stmt.executeUpdate() == 0) return null;

            try (ResultSet rs = stmt.getGeneratedKeys()) {
                return rs.next() ? rs.getInt(1) : null;
            }
        }
    }
}