package controller;

import exceptions.DataAccessException;
import exceptions.ValidationException;
import model.entities.Product;
import model.repositories.RepositoryOrder;
import model.repositories.RepositoryOrderItems;
import model.repositories.RepositoryProduct;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

/**
 * Controller responsável pelo caso de uso de Checkout (finalizar compra).
 * Responsável por validar dados básicos do pedido (usuário, total, forma de recebimento, carrinho),
 * criar um pedido na tabela {@code "order"},
 * criar itens na tabela {@code order_items} com {@code price_at_moment},
 * calcular unitário de cada item no momento do checkout,
 * converter {@link SQLException} em {@link DataAccessException}.
 * Este controller recebe o {@code totalPrice} já calculado pela View (subtotal + taxa, quando aplicável), sem calcular a taxa diretamente 
 * A persistência e consistência do pedido é responsabilidade dele.
 */
public class ControllerCheckout {
	/**
     * Repositório do pedido (tabela "order").
     * Usado para inserir o pedido e obter o id gerado.
     */
    private final RepositoryOrder repoOrder;
    
    /**
     * Repositório dos itens do pedido (tabela order_items).
     * Usado para inserir cada item com quantidade e preço no momento.
     */
    private final RepositoryOrderItems repoOrderItems;
    
    /**
     * Repositório de produtos.
     * Usado para recarregar o produto do banco e calcular corretamente o preço unitário
     * a partir de base + size + flavor_level.
     */
    private final RepositoryProduct repoProduct;
    
    /**
     * Construtor padrão.
     * Instancia repositórios concretos.
     */
    public ControllerCheckout() {
        this.repoOrder = new RepositoryOrder();
        this.repoOrderItems = new RepositoryOrderItems();
        this.repoProduct = new RepositoryProduct();
    }
    
    
    /**
     * Construtor com injeção de dependências (testes/controle).
     * @param repoOrder repositório de pedidos
     * @param repoOrderItems repositório de itens
     * @param repoProduct repositório de produtos
     */
    public ControllerCheckout(RepositoryOrder repoOrder, RepositoryOrderItems repoOrderItems, RepositoryProduct repoProduct) {
        this.repoOrder = repoOrder;
        this.repoOrderItems = repoOrderItems;
        this.repoProduct = repoProduct;
    }
    
    /**
     * Confirma o pedido e persiste no banco (pedido + itens).
     * Valida {@code idUser} (usuário logado)
     * Valida {@code totalPrice} (obrigatório e não negativo)
     * Valida {@code delivery} (forma de recebimento)
     * Valida se {@code cartItems} não é vazio
     * Cria o registro do pedido e obtém {@code idOrder}.
     * Para cada item do carrinho, recarrega o produto do banco, calcula o unitário (base + size + level) e insere item em {@code order_items} com {@code price_at_moment}.
     * Cada item ignora entradas inválidas (id nulo, qty &lt;= 0) e também ignora produtos inexistentes. 
     * @param idUser id do usuário logado
     * @param totalPrice total do pedido (já incluindo taxa de entrega, se houver)
     * @param delivery string indicando entrega/retirada (ex.: "Entrega", "Retirada")
     * @param observations observações do pedido (opcional)
     * @param cartItems mapa do carrinho (productId → quantidade)
     * @return id do pedido criado
     * @throws ValidationException se algum dado obrigatório estiver inválido
     * @throws DataAccessException se ocorrer falha ao criar pedido/itens no banco
     */
    public Integer confirmOrder(Integer idUser,
                                Double totalPrice,
                                String delivery,
                                String observations,
                                Map<Integer, Integer> cartItems)
            throws ValidationException, DataAccessException {

        if (idUser == null || idUser <= 0)
            throw new ValidationException("Usuário inválido. Faça login novamente.");

        if (totalPrice == null)
            throw new ValidationException("Total do pedido é obrigatório.");

        if (totalPrice < 0)
            throw new ValidationException("Total do pedido não pode ser negativo.");

        if (delivery == null || delivery.trim().isEmpty())
            throw new ValidationException("Forma de recebimento é obrigatória.");

        String del = delivery.trim();

        if (cartItems == null || cartItems.isEmpty())
            throw new ValidationException("Carrinho vazio. Adicione itens antes de finalizar.");

        try {
            Integer idOrder = repoOrder.createOrderAndReturnId(
                    idUser,
                    Timestamp.from(Instant.now()),
                    totalPrice,
                    del,
                    (observations != null && observations.trim().isEmpty()) ? null : observations
            );

            if (idOrder == null)
                throw new DataAccessException("Não foi possível criar o pedido.", null);

            for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
                Integer productId = entry.getKey();
                Integer qty = entry.getValue();

                if (productId == null) continue;
                if (qty == null || qty <= 0) continue;

                Product p = repoProduct.findByIdProduct(productId);
                if (p == null) continue;

                double unit = computeUnitPrice(p);

                boolean okItem = repoOrderItems.createOrderItem(idOrder, productId, qty, unit);
                if (!okItem)
                    throw new DataAccessException("Não foi possível salvar um item do pedido.", null);
            }

            return idOrder;

        } catch (SQLException e) {
            throw new DataAccessException("Erro ao confirmar pedido.", e);
        }
    }
    
    /**
     * Calcula o preço unitário final do produto no momento do checkout
     * unit = basePrice + size.price + flavorLevel.price
     * @param p produto carregado do banco (com size e flavor/level)
     * @return preço unitário final
     */
    private static double computeUnitPrice(Product p) {
        double base = p.getBasePrice() != null ? p.getBasePrice() : 0.0;
        double sizePrice = (p.getSize() != null && p.getSize().getPrice() != null) ? p.getSize().getPrice() : 0.0;
        double levelPrice = (p.getFlavor() != null && p.getFlavor().getLevel() != null && p.getFlavor().getLevel().getPrice() != null)
                ? p.getFlavor().getLevel().getPrice() : 0.0;

        return base + sizePrice + levelPrice;
    }
}