package model.factories;

import exceptions.ValidationException;
import model.entities.Size;

public final class SizeFactory {
    private SizeFactory() {}

    public static Size create(Integer id, String name, String yield, String weight, Double price) throws ValidationException {
        if (name == null || name.trim().isEmpty())
            throw new ValidationException("Nome do tamanho é obrigatório.");

        if (yield == null || yield.trim().isEmpty())
            throw new ValidationException("Rendimento é obrigatório.");

        if (weight == null || weight.trim().isEmpty())
            throw new ValidationException("Peso é obrigatório.");

        if (price == null)
            throw new ValidationException("Preço do tamanho é obrigatório.");

        if (price < 0)
            throw new ValidationException("Preço do tamanho não pode ser negativo.");

        return new Size(id, name.trim(), yield.trim(), weight.trim(), price);
    }
}