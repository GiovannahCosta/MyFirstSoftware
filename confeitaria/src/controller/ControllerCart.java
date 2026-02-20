package controller;

import app.CartSession;
import exceptions.ValidationException;
import model.entities.Product;

/**
 * Controller responsável pelas regras de negócio do carrinho.
 * Faz a validação mínima antes de alterar o estado do carrinho mantido em memória por {@link CartSession}.
 * Recebe ações da View (por exemplo, adicionar um item ao carrinho).
 * Valida entradas (produto e quantidade) e delega a alteração do carrinho para a CartSession.
 */
public class ControllerCart {

	/**
	 * Adiciona um produto ao carrinho com uma quantidade.
	 *
	 * Funcionamento:
	 * 1. Valida se o produto não é null.
	 * 2. Valida se a quantidade não é null.
	 * 3. Valida se a quantidade é maior que zero.
	 * 4. Chama {@link CartSession#add(Product, int)} para registrar o item no carrinho em memória.
	 *
	 * Observação:
	 * O método {@link CartSession#add(Product, int)} recebe quantidade como tipo primitivo int.
	 * Aqui o parâmetro é Integer para permitir validação explícita de null antes de delegar para a sessão.
	 *
	 * @param product produto que será adicionado
	 * @param qty quantidade desejada
	 * @throws ValidationException se o produto ou quantidade forem inválidos
	 */
    public void addProduct(Product product, Integer qty) throws ValidationException {
        if (product == null)
            throw new ValidationException("Produto inválido.");

        if (qty == null)
            throw new ValidationException("Quantidade é obrigatória.");

        if (qty <= 0)
            throw new ValidationException("Quantidade deve ser maior que zero.");

        CartSession.add(product, qty);
    }
}