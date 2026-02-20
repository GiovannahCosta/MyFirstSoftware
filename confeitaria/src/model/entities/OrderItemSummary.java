package model.entities;

/**
 * DTO (objeto de transferência de dados) que representa um resumo de item de pedido.
 *
 * Esta classe é usada no caso de uso "Meus Pedidos" para listar os itens de um pedido
 * sem precisar montar a entidade completa {@link OrderItems}.
 *
 * Normalmente é montada a partir de consultas no banco (por exemplo, via RepositoryMyOrders),
 * retornando apenas os campos necessários para exibição: nome do produto, quantidade e preço
 * registrado no momento da compra.
 */
public class OrderItemSummary {

    /**
     * Nome do produto no momento da consulta.
     * É usado diretamente para exibição na tela (sem necessidade de carregar {@link Product}).
     */
    private String productName;

    /**
     * Quantidade do produto no item do pedido.
     */
    private Integer quantity;

    /**
     * Preço unitário registrado no momento do pedido.
     * Serve para manter consistência histórica caso o preço do produto mude no futuro.
     */
    private Double priceAtMoment;

    /**
     * Constrói um resumo de item de pedido.
     *
     * Funcionamento:
     * 1. Recebe valores já prontos, geralmente vindos de uma consulta no banco.
     * 2. Atribui os valores diretamente nos campos internos do DTO.
     *
     * @param productName nome do produto
     * @param quantity quantidade do item
     * @param priceAtMoment preço unitário registrado no momento do pedido
     */
    public OrderItemSummary(String productName, Integer quantity, Double priceAtMoment) {
        this.productName = productName;
        this.quantity = quantity;
        this.priceAtMoment = priceAtMoment;
    }

    /**
     * Retorna o nome do produto.
     *
     * @return nome do produto
     */
    public String getProductName() {
        return productName;
    }

    /**
     * Retorna a quantidade do item.
     *
     * @return quantidade
     */
    public Integer getQuantity() {
        return quantity;
    }

    /**
     * Retorna o preço unitário registrado no momento do pedido.
     *
     * @return preço unitário do item (pode ser null)
     */
    public Double getPriceAtMoment() {
        return priceAtMoment;
    }

    /**
     * Calcula o total do item do pedido.
     *
     * Funcionamento:
     * 1. Se {@code priceAtMoment} for null, considera 0.0.
     * 2. Se {@code quantity} for null, considera 0.
     * 3. Retorna a multiplicação do preço unitário pela quantidade.
     *
     * Observação:
     * Este método não altera estado interno; é apenas um cálculo de conveniência para a View.
     *
     * @return total do item (preço unitário multiplicado pela quantidade)
     */
    public Double getTotal() {
        double unit = priceAtMoment != null ? priceAtMoment : 0.0;
        int qty = quantity != null ? quantity : 0;
        return unit * qty;
    }
}