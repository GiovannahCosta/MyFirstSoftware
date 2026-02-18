package model.factories;

import exceptions.ValidationException;
import model.entities.Address;
import model.entities.Person;

/**
 * Factory responsável por criar instâncias válidas de {@link Person}.
 * Aplicar validações de dados pessoais (nome e email)
 * Garantir que o endereço exista ao criar a pessoa (ligação Person → Address)
 * Normalizar entrada (trim) e padronizar a criação da entidade.
 */
public final class PersonFactory {
	/**
     * Construtor privado para impedir instanciação.
     */
    private PersonFactory() {}

    /**
     * Cria uma {@link Person} validando regras básicas.
     * {@code firstName} obrigatório
     * {@code email} obrigatório e com validação simplificada.
     * {@code address} obrigatório.
     * Valida os parâmetros.
     * Normaliza campos com {@code trim()}.
     * Cria um {@link Person} associado ao {@link Address} informado
     * Se {@code id} for fornecido, seta o id na entidade.
     * @param id id da pessoa (opcional)
     * @param firstName nome (obrigatório)
     * @param lastName sobrenome (opcional)
     * @param email email (obrigatório)
     * @param address endereço associado (obrigatório)
     * @return instância válida de {@link Person}
     * @throws ValidationException se algum campo obrigatório estiver inválido
     */
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