package controller;

import app.CartSession;
import exceptions.DataAccessException;
import model.entities.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller auxiliar para a View de carrinho.
 * Existe para montar os dados que a interface do carrinho precisa exibir sem duplicar regras e consultas na View.
 * Lê o estado atual do carrinho em memória ({@link CartSession}) e transforma em uma estrutura pronta para exibição:
 * - carrega o Product completo a partir do id do produto
 * - calcula preço unitário e total por item
 * - calcula subtotal do carrinho
 * Retorna essas informações para a View através de CartViewData.
 *
 * As consultas ao banco são delegadas ao {@link ControllerShop}.
 * Qualquer falha de acesso a dados é convertida para {@link DataAccessException} pelo ControllerShop.
 */
public class ControllerCartView {

	/**
	 * Controller de loja usado para carregar produtos pelo id.
	 * Centraliza a regra de consulta de produto e a conversão de erros de acesso em DataAccessException.
	 */
    private final ControllerShop controllerShop;

    /**
     * Construtor padrão.
     * Instancia um ControllerShop padrão para que este controller consiga consultar produtos.
     */
    public ControllerCartView() {
        this.controllerShop = new ControllerShop();
    }

    /**
     * Construtor com injeção de dependência.
     * Permite fornecer um ControllerShop já configurado (por exemplo, para testes).
     *
     * @param controllerShop controller usado para consultas de produtos (não deve ser null)
     */
    public ControllerCartView(ControllerShop controllerShop) {
        this.controllerShop = controllerShop;
    }

    /**
     * Carrega os dados do carrinho para exibição em tabela.
     *
     * Funcionamento:
     * 1. Inicializa lista de linhas (CartRow) e subtotal.
     * 2. Itera por {@link CartSession#getItems()} no formato productId para qty.
     * 3. Ignora itens inválidos (id null, qty null ou qty menor ou igual a 0).
     * 4. Busca o Product pelo id via {@link ControllerShop#findProductById(Integer)}.
     * 5. Se o produto não existir (null), ignora o item.
     * 6. Calcula o preço unitário com {@link #computeUnitPrice(Product)}.
     * 7. Calcula total do item (unit * qty).
     * 8. Soma no subtotal e adiciona uma CartRow na lista.
     * 9. Retorna um {@link CartViewData} com as linhas e o subtotal.
     *
     * @return objeto CartViewData contendo linhas e subtotal
     * @throws DataAccessException se ocorrer falha ao buscar produtos no banco
     */
    public CartViewData loadCartData() throws DataAccessException {
        List<CartRow> rows = new ArrayList<>();
        double subtotal = 0.0;

        for (Map.Entry<Integer, Integer> entry : CartSession.getItems().entrySet()) {
            Integer productId = entry.getKey();
            Integer qty = entry.getValue();

            if (productId == null || qty == null || qty <= 0) continue;

            Product p = controllerShop.findProductById(productId);
            if (p == null) continue;

            double unit = computeUnitPrice(p);
            double total = unit * qty;
            subtotal += total;

            rows.add(new CartRow(productId, p.getName(), qty, unit, total));
        }

        return new CartViewData(rows, subtotal);
    }

    /**
     * Remove um item do carrinho em memória.
     *
     * Funcionamento:
     * 1. Valida se o productId não é null.
     * 2. Chama {@link CartSession#remove(Integer)} para remover do mapa interno da sessão.
     *
     * @param productId id do produto a remover
     */
    public void removeByProductId(Integer productId) {
        if (productId == null) return;
        CartSession.remove(productId);
    }

    /**
     * Calcula o preço unitário final de um produto.
     *
     * Regra usada:
     * unit = basePrice + size.price + flavor.level.price
     *
     * Funcionamento:
     * 1. Usa basePrice ou 0 se estiver null.
     * 2. Soma o preço do tamanho (Size.price) quando existir.
     * 3. Soma o preço do nível do sabor (FlavorLevel.price) quando existir.
     * 4. Retorna a soma como preço unitário final.
     *
     * @param p produto completo (com size e flavor/level preenchidos quando aplicável)
     * @return preço unitário final
     */
    private static double computeUnitPrice(Product p) {
        double base = p.getBasePrice() != null ? p.getBasePrice() : 0.0;
        double sizePrice = (p.getSize() != null && p.getSize().getPrice() != null) ? p.getSize().getPrice() : 0.0;
        double levelPrice = (p.getFlavor() != null && p.getFlavor().getLevel() != null && p.getFlavor().getLevel().getPrice() != null)
                ? p.getFlavor().getLevel().getPrice() : 0.0;
        return base + sizePrice + levelPrice;
    }

   /**
    * Linha do carrinho para exibição em tabela.
    * Mantém os campos já calculados para a View, evitando que a UI recalcule valores.
    */
    public static class CartRow {

    	/**
    	 * Identificador do produto (chave do carrinho).
    	 */
        private final Integer productId;

        /**
         * Nome do produto para exibição.
         */
        private final String productName;

        /**
         * Quantidade escolhida do produto.
         */
        private final Integer qty;

        /**
         * Preço unitário calculado.
         */
        private final Double unit;

        /**
         * Total do item calculado como unit * qty.
         */
        private final Double total;

        /**
         * Constrói uma linha do carrinho.
         *
         * @param productId id do produto
         * @param productName nome do produto
         * @param qty quantidade
         * @param unit preço unitário
         * @param total total do item
         */
        public CartRow(Integer productId, String productName, Integer qty, Double unit, Double total) {
            this.productId = productId;
            this.productName = productName;
            this.qty = qty;
            this.unit = unit;
            this.total = total;
        }

        /**
         * Retorna o id do produto associado à linha.
         *
         * @return id do produto
         */
        public Integer getProductId() { return productId; }

        /**
         * Retorna o nome do produto associado à linha.
         *
         * @return nome do produto
         */
        public String getProductName() { return productName; }

        /**
         * Retorna a quantidade do item na linha.
         *
         * @return quantidade
         */
        public Integer getQty() { return qty; }

        /**
         * Retorna o preço unitário calculado.
         *
         * @return preço unitário
         */
        public Double getUnit() { return unit; }

        /**
         * Retorna o total calculado do item.
         *
         * @return total do item
         */
        public Double getTotal() { return total; }
    }

    /**
     * Estrutura agregada retornada para a View do carrinho.
     * Contém:
     * - linhas prontas para a tabela
     * - subtotal calculado como soma de todos os totais
     */
    public static class CartViewData {

    	/**
    	 * Linhas prontas para exibição.
    	 */
        private final List<CartRow> rows;

        /**
         * Subtotal do carrinho calculado.
         */
        private final Double subtotal;

        /**
         * Cria o resultado de exibição do carrinho.
         *
         * @param rows linhas
         * @param subtotal subtotal
         */
        public CartViewData(List<CartRow> rows, Double subtotal) {
            this.rows = rows;
            this.subtotal = subtotal;
        }

        /**
         * Retorna as linhas do carrinho.
         *
         * @return lista de linhas
         */
        public List<CartRow> getRows() { return rows; }

        /**
         * Retorna o subtotal do carrinho.
         *
         * @return subtotal
         */
        public Double getSubtotal() { return subtotal; }
    }
}