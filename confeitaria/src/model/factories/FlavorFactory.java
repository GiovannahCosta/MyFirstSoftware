package model.factories;

import exceptions.ValidationException;
import model.entities.Flavor;
import model.entities.FlavorLevel;

/**
 * Factory responsável por criar instâncias válidas de {@link Flavor}.
 * Garante que o nome do sabor seja informado
 * Garante que exista um {@link FlavorLevel} associado (nível do sabor)
 * Normaliza texto (trim) e trata descrição opcional.
 */
public final class FlavorFactory {
	/**
     * Construtor privado para impedir instanciação.
     */
    private FlavorFactory() {}
    
    /**
     * Cria um {@link Flavor} aplicando validações básicas.
     * {@code name} obrigatório.
     * {@code level} obrigatório.
     * {@code description} opcional (se vazio vira null).
     * Valida parâmetros obrigatórios.
     * Normaliza {@code name} com {@code trim()}.
     * Normaliza {@code description}: se estiver vazia, define como {@code null}.
     * Cria a instância de {@link Flavor} e aplica {@code id} se fornecido.
     * @param id id do sabor (opcional)
     * @param name nome do sabor (obrigatório)
     * @param level nível do sabor (obrigatório)
     * @param description descrição (opcional)
     * @return instância válida de {@link Flavor}
     * @throws ValidationException se dados obrigatórios forem inválidos
     */
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