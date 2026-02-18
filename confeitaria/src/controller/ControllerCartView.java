package controller;

import app.CartSession;
import exceptions.DataAccessException;
import model.entities.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Controller auxiliar de "View de Carrinho"
 * Este controller existe para montar dados que a UI do carrinho precisa exibir, sem que a View tenha que reimplementar regras e consultas repetidas
 * Responsável por: ler o estado atual do carrinho em memória ({@link CartSession}), para cada item do carrinho (productId → qty), 
 * carregar o {@link Product} completo, 
 * calcular preço unitário e total por item,  
 * retornar uma estrutura pronta para a View (rows + subtotal)
 * usa {@link ControllerShop} internamente para buscar produtos no banco e converte
 * falhas em {@link DataAccessException}
 */

public class ControllerCartView {
	
	/**
	 * Controller de loja usado para carregar produtos pelo id (delegando persistência)
	 * Mantém a regra "buscar produto por id" centralizada em um único local (shop).
	 */
    private final ControllerShop controllerShop;

    /**
     * Construtor padrão.
     * Instancia um {@link ControllerShop} padrão.
     */
    public ControllerCartView() {
        this.controllerShop = new ControllerShop();
    }
    
    /**
     * Construtor com injeção de dependência.
     * @param controllerShop controller usado para consultas de produtos (não deve ser null)
     */
    public ControllerCartView(ControllerShop controllerShop) {
        this.controllerShop = controllerShop;
    }
    
    /**
     * Carrega os dados do carrinho para exibição em tabela.
     * Inicializa lista de linhas ({@link CartRow}) e subtotal.
     * Itera por {@link CartSession#getItems()} (productId → qty).
     * Ignora itens inválidos (id null, qty null ou qty &lt;= 0)
     * Busca o {@link Product} pelo id via {@link ControllerShop#findProductById(Integer)}
     * Se não existir, ignora
     * Calcula unitário (base + size + flavorLevel) e total (unit * qty).
     * Acumula no subtotal e adiciona uma linha.
     * @return objeto {@link CartViewData} contendo linhas e subtotal
     * @throws DataAccessException se ocorrer falha ao buscar produtos no banco
     */
    public CartViewData loadCartData() throws DataAccessException {
        List<CartRow> rows = new ArrayList<>();
        double subtotal = 0.0;

        for (Map.Entry<Integer, Integer> entry : CartSession.getItems().entrySet()) {
            Integer productId = entry.getKey();
            Integer qty = entry.getValue();

            if (productId == null || qty == null || qty <= 0) continue;

            Product p = controllerShop.findProductById(productId); // pode retornar null
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
     * Valida se o {@code productId} não é null
     * Chama {@link CartSession.remove(Integer)}
     * @param productId id do produto a remover
     */
    public void removeByProductId(Integer productId) {
        if (productId == null) return;
        CartSession.remove(productId);
    }
    
    
    /**
     * Calcula o preço unitário "final" de um produto: unit = basePrice + size.price + flavorLevel.price, de acordo com regra de negócio
     * @param p produto completo (com size e flavor/level preenchidos)
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
    * Mantém os campos já calculados para a View (não precisa recalcular na UI)
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
         * Preço unitário calculado (base + size + level)
         */
        private final Double unit;
        
        /**
         * Total do item (unit * qty).
         */
        private final Double total;
        
        /**
         * Constrói uma linha do carrinho.
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

        public Integer getProductId() { return productId; }
        public String getProductName() { return productName; }
        public Integer getQty() { return qty; }
        public Double getUnit() { return unit; }
        public Double getTotal() { return total; }
    }
    
    /**
     * Estrutura agregada para a View do carrinho
     * Retorna lista de linhas (itens) e subtotal (soma de todos os totais)
     */
    public static class CartViewData {
    	/**
    	 * Linhas prontas para a tabela.
    	 */
        private final List<CartRow> rows;
        
        /**
         * Subtotal do carrinho (sem taxa de entrega)
         */
        private final Double subtotal;
        
        /**
         * Cria o resultado de exibição do carrinho.
         * @param rows linhas
         * @param subtotal subtotal
         */
        public CartViewData(List<CartRow> rows, Double subtotal) {
            this.rows = rows;
            this.subtotal = subtotal;
        }

        public List<CartRow> getRows() { return rows; }
        public Double getSubtotal() { return subtotal; }
    }
}