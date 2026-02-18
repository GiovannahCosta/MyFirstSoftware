package model.repositories;

import model.entities.OrderItemSummary;
import model.entities.OrderSummary;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;


/**
 * Repositório responsável por consultas do caso de uso "Meus Pedidos".
 * Retorna objetos de resumo (OrderSummary e OrderItemSummary) em vez de entidades completas.
 *
 * Motivo de existir como repositório separado:
 * As consultas de "Meus Pedidos" são consultas de leitura (SELECT) que atendem diretamente a tela,
 * com foco em listar pedidos de um usuário e os itens de um pedido.
 *
 * Motivo de não ter CRUD completo como outros repositories:
 * Este repositório é orientado a caso de uso (use-case driven).
 * Ele implementa apenas as consultas necessárias para a funcionalidade atual da aplicação.
 * Novos métodos devem ser adicionados somente se surgirem novas telas/relat��rios/filtros.
 */
public class RepositoryMyOrders {
	
	/**
     * SQL para buscar pedidos (cabeçalho) do usuário.
     * Retorna apenas campos necessários para OrderSummary.
     * Ordena por data/hora desc e id desc para mostrar os mais recentes primeiro.
     */
    private static final String SQL_FIND_ORDERS_BY_USER =
            "SELECT id, datetime, total_price, delivery, observations "
                    + "FROM \"order\" "
                    + "WHERE id_user = ? "
                    + "ORDER BY datetime DESC, id DESC";
    
    /**
     * SQL para buscar itens de um pedido.
     * Faz JOIN com product para trazer o nome do produto.
     * Retorna apenas campos necessários para OrderItemSummary.
     */
    private static final String SQL_FIND_ITEMS_BY_ORDER =
            "SELECT oi.quantity, oi.price_at_moment, p.name AS product_name "
                    + "FROM order_items oi "
                    + "INNER JOIN product p ON p.id = oi.id_product "
                    + "WHERE oi.id_order = ? "
                    + "ORDER BY oi.id";
    
    /**
     * Lista os pedidos de um usuário.
     * Abre conexão, prepara SQL_FIND_ORDERS_BY_USER, define id_user, executa.
     * Para cada linha, cria OrderSummary e adiciona em lista.
     *
     * @param idUser id do usuário
     * @return lista de pedidos (nunca null, pode ser vazia)
     * @throws SQLException em erro de acesso ao banco
     */
    public List<OrderSummary> findOrdersByUser(Integer idUser) throws SQLException {
        List<OrderSummary> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ORDERS_BY_USER)) {

            stmt.setInt(1, idUser);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new OrderSummary(
                            rs.getInt("id"),
                            rs.getTimestamp("datetime"),
                            rs.getDouble("total_price"),
                            rs.getString("delivery"),
                            rs.getString("observations")
                    ));
                }
            }
        }

        return list;
    }
    
    /**
     * Lista os itens de um pedido.
     * Abre conexão, prepara SQL_FIND_ITEMS_BY_ORDER, define id_order, executa.
     * Para cada linha, cria OrderItemSummary e adiciona em lista.
     *
     * @param idOrder id do pedido
     * @return lista de itens (nunca null, pode ser vazia)
     * @throws SQLException em erro de acesso ao banco
     */
    public List<OrderItemSummary> findItemsByOrder(Integer idOrder) throws SQLException {
        List<OrderItemSummary> list = new ArrayList<>();

        try (Connection conn = DBConnection.getConnection();
             PreparedStatement stmt = conn.prepareStatement(SQL_FIND_ITEMS_BY_ORDER)) {

            stmt.setInt(1, idOrder);

            try (ResultSet rs = stmt.executeQuery()) {
                while (rs.next()) {
                    list.add(new OrderItemSummary(
                            rs.getString("product_name"),
                            rs.getInt("quantity"),
                            rs.getDouble("price_at_moment")
                    ));
                }
            }
        }

        return list;
    }
}