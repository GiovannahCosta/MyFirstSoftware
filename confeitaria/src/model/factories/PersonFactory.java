package model.factories;

import exceptions.ValidationException;
import model.entities.Address;
import model.entities.Person;

public final class PersonFactory {
    private PersonFactory() {}

    public static Person create(Integer id, String firstName, String lastName, String email, Address address)
            throws ValidationException {

        if (firstName == null || firstName.trim().isEmpty())
            throw new ValidationException("Nome é obrigatório.");

        if (email == null || email.trim().isEmpty())
            throw new ValidationException("E-mail é obrigatório.");

        String em = email.trim();
        if (!em.contains("@") || !em.contains(".com"))
            throw new ValidationException("E-mail inválido.");

        if (address == null)
            throw new ValidationException("Endereço é obrigatório.");

        String ln = (lastName != null && !lastName.trim().isEmpty()) ? lastName.trim() : null;

        Person p = new Person(firstName.trim(), ln, em, address);
        if (id != null) p.setId(id);
        return p;
    }
}