package model.factories;

import exceptions.ValidationException;
import model.entities.Size;

/**
 * Factory responsável por criar instâncias válidas de {@link Size} (tamanhos).
 * Centraliza validações dos campos do tamanho (nome, rendimento, peso e preço).
 * Evita criação de {@link Size} com preço negativo ou campos vazios.
 */
public final class SizeFactory {
	
	/**
     * Construtor privado para impedir instanciação.
     */
    private SizeFactory() {}

    /**
     * Cria um {@link Size} validando campos obrigatórios.
     * {@code name}, {@code yield} e {@code weight} são obrigatórios.
     * {@code price} é obrigatório e não pode ser negativo.
     * Valida todos os campos.
     * Normaliza strings com {@code trim()}.
     * Cria o {@link Size} retornando a instância pronta.
     * @param id id do tamanho (pode ser null)
     * @param name nome do tamanho (obrigatório)
     * @param yield rendimento (obrigatório)
     * @param weight peso (obrigatório)
     * @param price preço adicional do tamanho (obrigatório; deve ser >= 0)
     * @return instância válida de {@link Size}
     * @throws ValidationException se algum campo obrigatório for inválido
     */
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