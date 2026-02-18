package model.factories;

import exceptions.ValidationException;
import model.entities.Address;
import model.entities.Area;

public final class AddressFactory {
    private AddressFactory() {}

    public static Address create(Integer id, Area area, String cep, String street, Integer number,
                                 String complement, String reference) throws ValidationException {
        if (area == null)
            throw new ValidationException("Bairro/Área é obrigatório.");

        if (street == null || street.trim().isEmpty())
            throw new ValidationException("Rua é obrigatória.");

        String c = (cep != null) ? cep.trim() : null;
        if (c != null && !c.isEmpty() && c.length() != 8)
            throw new ValidationException("CEP deve ter 8 caracteres (apenas números).");

        String comp = (complement != null && !complement.trim().isEmpty()) ? complement.trim() : null;
        String ref = (reference != null && !reference.trim().isEmpty()) ? reference.trim() : null;
        String cepValue = (c != null && !c.isEmpty()) ? c : null;

        Address address = new Address(area, cepValue, street.trim(), number, comp, ref);
        if (id != null) address.setInteger(id);
        return address;
    }
}