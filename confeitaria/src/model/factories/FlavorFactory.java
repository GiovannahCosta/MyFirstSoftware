package model.factories;

import exceptions.ValidationException;
import model.entities.Flavor;
import model.entities.FlavorLevel;

public final class FlavorFactory {
    private FlavorFactory() {}

    public static Flavor create(Integer id, String name, FlavorLevel level, String description) throws ValidationException {
        if (name == null || name.trim().isEmpty())
            throw new ValidationException("Nome do sabor é obrigatório.");

        if (level == null)
            throw new ValidationException("Nível do sabor é obrigatório.");

        String desc = (description != null && !description.trim().isEmpty()) ? description.trim() : null;

        Flavor f = new Flavor(name.trim(), level, desc);
        if (id != null) f.setId(id);
        return f;
    }
}