package model.factories;

import exceptions.ValidationException;
import model.entities.Flavor;
import model.entities.Product;
import model.entities.Size;

/**
 * Factory responsável por criar instâncias válidas de {@link Product}.
 * Centraliza validação de campos obrigatórios (nome, sabor, tamanho e preço base).
 * Evitar persistência de produtos com valores inválidos (ex.: preço negativo)
 * Trata descrição como campo opcional (vazio → null).
 */
public final class ProductFactory {
	
	/**
     * Construtor privado para impedir instanciação.
     */
    private ProductFactory() {}
    
    /**
     * Cria um {@link Product} validando consistência mínima para o catálogo.
     * {@code name} obrigatório.
     * {@code flavor} obrigatório.
     * {@code size} obrigatório.
     * {@code basePrice} obrigatório e não pode ser negativo.
     * Valida campos obrigatórios.
     * Normaliza {@code name} com {@code trim()}.
     * Normaliza {@code description}: vazio vira {@code null}.
     * Cria instância de {@link Product}.
     * Se {@code id} for fornecido, seta o id na entidade.
     * @param id id do produto (opcional)
     * @param name nome do produto (obrigatório)
     * @param flavor sabor (obrigatório)
     * @param size tamanho (obrigatório)
     * @param basePrice preço base (obrigatório; deve ser >= 0)
     * @param description descrição (opcional)
     * @return instância válida de {@link Product}
     * @throws ValidationException se algum campo obrigatório for inválido
     */
    public static Product create(Integer id, String name, Flavor flavor, Size size, Double basePrice, String description)
            throws ValidationException {

        if (name == null || name.trim().isEmpty())
            throw new ValidationException("Nome do produto é obrigatório.");

        if (flavor == null)
            throw new ValidationException("Sabor é obrigatório.");

        if (size == null)
            throw new ValidationException("Tamanho é obrigatório.");

        if (basePrice == null)
            throw new ValidationException("Preço base é obrigatório.");

        if (basePrice < 0)
            throw new ValidationException("Preço base não pode ser negativo.");

        String desc = (description != null && !description.trim().isEmpty()) ? description.trim() : null;

        Product p = new Product(name.trim(), flavor, size, basePrice, desc);
        if (id != null) p.setId(id);
        return p;
    }
}