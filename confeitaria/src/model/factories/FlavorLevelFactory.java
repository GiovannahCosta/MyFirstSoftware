package model.factories;

import exceptions.ValidationException;
import model.entities.FlavorLevel;

/**
 * Factory responsável por criar instâncias válidas de {@link FlavorLevel}.
 * Garante que o nome do nível de sabor seja informado.
 * Garante que o preço adicional do nível seja informado e não seja negativo
 */
public final class FlavorLevelFactory {
	/**
     * Construtor privado para impedir instanciação.
     */
    private FlavorLevelFactory() {}
    
    /**
     * Cria um {@link FlavorLevel} validando regras básicas.
     * {@code name} obrigatório.
     * {@code price} obrigatório e não negativo.
     * Valida {@code name} e {@code price}.
     * Normaliza {@code name} com {@code trim()}.
     * Retorna um {@link FlavorLevel} com os valores validados.
     * @param id id do nível (opcional)
     * @param name nome do nível (obrigatório)
     * @param price preço adicional do nível (obrigatório; deve ser >= 0)
     * @return instância válida de {@link FlavorLevel}
     * @throws ValidationException se alguma validação falhar
     */
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