package model.factories;

import exceptions.ValidationException;
import model.entities.Area;

public final class AreaFactory {
    private AreaFactory() {}

    public static Area create(Integer id, String name, Double fee) throws ValidationException {
        if (name == null || name.trim().isEmpty())
            throw new ValidationException("Nome da área é obrigatório.");

        if (fee == null)
            throw new ValidationException("Taxa da área é obrigatória.");

        if (fee < 0)
            throw new ValidationException("Taxa da área não pode ser negativa.");

        Area a = new Area(name.trim(), fee);
        if (id != null) a.setId(id);
        return a;
    }
}