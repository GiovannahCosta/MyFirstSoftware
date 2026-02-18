package controller;

import app.CartSession;
import exceptions.ValidationException;
import model.entities.Product;


/**
 * Controller responsável pelas regras de negócio do carrinho.
 * Faz a validação mínima antes de alterar o estado do carrinho mantido em memória por {@link CartSession}.
 * Recebe ações da View (ex.: "Adicionar ao carrinho").
 * Valida entradas (produto e quantidade).
 * Delegação do armazenamento em memória para {@link CartSession}.
 */
public class ControllerCart {
	
	/**
	 * Adiciona um produto ao carrinho com uma quantidade.
	 * Valida se {@code product} não é null.
	 * Valida se {@code qty} não é null e é maior que zero.
	 * Chama {@link CartSession#add(Product, Integer)} para persistir no carrinho em memória.
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