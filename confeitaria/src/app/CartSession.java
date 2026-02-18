package app;

import model.entities.Product;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Sessão de carrinho mantida em memória.
 * Armazena os itens que o usuário adicionou ao carrinho durante a execução do app.
 * Fornece operações básicas de manipulação (adicionar, setar quantidade, remover, limpar).
 * Permitr acesso por qualquer View/Controller sem necessidade de passar o carrinho por parâmetro.
 * {@code Map<Integer, Integer>} mapeando {@code productId -> quantidade}.
 * Usa {@link LinkedHashMap} para manter ordem de inserção (útil para exibição consistente na UI).
 * Obs.: Os dados não são persistidos em banco: ao fechar o app, o carrinho é perdido.
 * Obs.: Os métodos fazem validações defensivas e ignoram entradas inválidas.
 */
public final class CartSession {

	/**
     * Mapa com os itens do carrinho.
     * Chave: {@code id} do produto.  
     * Valor: quantidade escolhida.
     *É {@code static} porque esse carrinho representa um estado global (uma "sessão") acessível de qualquer ponto da aplicação.
     */
    private static final Map<Integer, Integer> items = new LinkedHashMap<>();
    
    /**
     * Construtor privado para impedir instanciação.
     * Esta classe é uma utility/sessão com estado estático.
     */
    private CartSession() {}
    
    /**
     * Adiciona uma quantidade a um produto no carrinho.
     * Valida se {@code product} e {@code product.getId()} não são null.
     * Valida se {@code quantity} é maior que zero.
     * Se o produto já existe no carrinho, soma a quantidade.
     * Se não existe, cria uma nova entrada no mapa.
     * @param product produto a adicionar (deve possuir id)
     * @param quantity quantidade a ser adicionada (deve ser > 0)
     */
    public static void add(Product product, int quantity) {
        if (product == null || product.getId() == null) return;
        if (quantity <= 0) return;

        items.merge(product.getId(), quantity, Integer::sum);
    }
    
    /**
     * Define (sobrescreve) a quantidade de um produto no carrinho.
     * Valida se {@code product} e {@code product.getId()} não são null.
     * Se {@code quantity <= 0}, remove o produto do carrinho.
     * Se {@code quantity > 0}, grava exatamente este valor no mapa.
     * @param product produto (deve possuir id)
     * @param quantity quantidade final (se <= 0 remove)
     */
    public static void set(Product product, int quantity) {
        if (product == null || product.getId() == null) return;
        if (quantity <= 0) {
            items.remove(product.getId());
            return;
        }
        items.put(product.getId(), quantity);
    }
    
    /**
     * Remove um produto do carrinho a partir do id.
     * @param productId id do produto a remover
     */
    public static void remove(Integer productId) {
        if (productId == null) return;
        items.remove(productId);
    }
    
    /**
     * Remove todos os itens do carrinho.
     * Usado, por exemplo, após confirmar o pedido (checkout).
     */
    public static void clear() {
        items.clear();
    }
    
    /**
     * Retorna uma visão somente leitura do carrinho.
     * Usado pela UI para listar itens sem permitir alteração direta no mapa interno.
     *@return mapa imutável (productId → quantidade)
     */
    public static Map<Integer, Integer> getItems() {
        return Collections.unmodifiableMap(items);
    }
    
    /**
     * Indica se o carrinho está vazio
     * @return true se não existir nenhum item no carrinho
     */
    public static boolean isEmpty() {
        return items.isEmpty();
    }
}