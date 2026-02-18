package model.factories;

import exceptions.ValidationException;
import model.entities.FlavorLevel;

public final class FlavorLevelFactory {
    private FlavorLevelFactory() {}

    public static FlavorLevel create(Integer id, String name, Double price) throws ValidationException {
        if (name == null || name.trim().isEmpty())
            throw new ValidationException("Nome do nível de sabor é obrigatório.");

        if (price == null)
            throw new ValidationException("Preço do nível de sabor é obrigatório.");

        if (price < 0)
            throw new ValidationException("Preço do nível de sabor não pode ser negativo.");

        return new FlavorLevel(id, name.trim(), price);
    }
}