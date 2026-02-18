package model.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;

/**
 * Repositório responsável por persistência dos itens do pedido na tabela order_items.
 *
 * Motivo de ter apenas um método:
 * O fluxo atual do checkout precisa apenas inserir itens no pedido recém-criado,
 * salvando quantidade e o preço no momento da compra (price_at_moment).
 *
 * Motivo de não ter CRUD completo como outros repositories:
 * As consultas para exibição dos itens do pedido são feitas pelo RepositoryMyOrders,
 * que retorna resumos (OrderItemSummary) via JOIN com product.
 * Métodos adicionais (find/update/delete) devem ser criados apenas se surgirem novas funcionalidades
 * como alteração/cancelamento de itens, auditoria ou relatórios adicionais.
 */
public class RepositoryOrderItems {
	
	/**
     * SQL de INSERT na tabela order_items.
     * Campos: id_order, id_product, quantity, price_at_moment.
     */
    private static final String SQL_INSERT =
            "INSERT INTO order_items (id_order, id_product, quantity, price_at_moment) "
                    + "VALUES (?, ?, ?, ?)";
    
    /**
     * Insere um item do pedido.
     * Abre conexão, prepara SQL_INSERT, define id_order, id_product, quantity e price_at_moment.
     * Executa e retorna true se inseriu ao menos uma linha.
     *
     * @param idOrder id do pedido (FK para "order")
     * @param idProduct id do produto (FK para product)
     * @param quantity quantidade do item
     * @param priceAtMoment preço unitário no momento da compra
     * @return true se inseriu ao menos uma linha
     * @throws SQLException em erro de acesso ao banco
     */
    public boolean createOrderItem(Integer idOrder, Integer idProduct, Integer quantity, Double priceAtMoment)
            throws SQLException {
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_INSERT)) {

            stmt.setInt(1, idOrder);
            stmt.setInt(2, idProduct);
            stmt.setInt(3, quantity);
            stmt.setDouble(4, priceAtMoment);

            return stmt.executeUpdate() > 0;
        }
    }
}