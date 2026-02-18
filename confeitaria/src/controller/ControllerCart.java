package controller;

import app.CartSession;
import exceptions.ValidationException;
import model.entities.Product;

public class ControllerCart {

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