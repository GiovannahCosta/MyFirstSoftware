package model.factories;

import exceptions.ValidationException;
import model.entities.Flavor;
import model.entities.Product;
import model.entities.Size;

public final class ProductFactory {
    private ProductFactory() {}

    public static Product create(Integer id, String name, Flavor flavor, Size size, Double basePrice, String description)
            throws ValidationException {

        if (name == null || name.trim().isEmpty())
            throw new ValidationException("Nome do produto é obrigatório.");

        if (flavor == null)
            throw new ValidationException("Sabor é obrigatório.");

        if (size == null)
            throw new ValidationException("Tamanho é obrigatório.");

        if (basePrice == null)
            throw new ValidationException("Preço base é obrigatório.");

        if (basePrice < 0)
            throw new ValidationException("Preço base não pode ser negativo.");

        String desc = (description != null && !description.trim().isEmpty()) ? description.trim() : null;

        Product p = new Product(name.trim(), flavor, size, basePrice, desc);
        if (id != null) p.setId(id);
        return p;
    }
}