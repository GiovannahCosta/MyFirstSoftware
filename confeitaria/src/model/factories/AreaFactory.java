package model.factories;

import exceptions.ValidationException;
import model.entities.Area;

/**
 * Factory responsável por criar instâncias válidas de {@link Area}.
 * Centralizar validações relacionadas ao conceito de "Área/Bairro" (nome e taxa)
 * Evitar duplicação de validações em controllers/views
 * Garantir que qualquer {@link Area} criada por esta factory esteja consistente
 * Esta classe segue o padrão "utility class" (somente métodos estáticos), por isso possui construtor privado.
 */
public final class AreaFactory {
	/**
     * Construtor privado para impedir instanciação.
     * Esta factory deve ser utilizada apenas via métodos estáticos.
     */
    private AreaFactory() {}
    
    /**
     * Cria uma {@link Area} validando regras básicas de consistência.
     * {@code name} é obrigatório e não pode ser vazio após {@code trim()}
     * {@code fee} é obrigatório.
     * {@code fee} não pode ser negativo.
     * Valida parâmetros.
     * Cria uma instância de {@link Area} com {@code name.trim()} e {@code fee}
     * Se {@code id} for fornecido, seta o id na entidade (útil ao reconstruir objetos do banco).
     * @param id id da área (opcional; use quando estiver reconstruindo uma área existente)
     * @param name nome da área/bairro (obrigatório)
     * @param fee taxa de entrega associada à área (obrigatória; deve ser >= 0)
     * @return instância válida de {@link Area}
     * @throws ValidationException se alguma regra de validação não for atendida
     */
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