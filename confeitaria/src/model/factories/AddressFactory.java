package model.factories;

import exceptions.ValidationException;
import model.entities.Address;
import model.entities.Area;

/**
 * Factory responsável por criar instâncias válidas de {@link Address}.
 * Garante que o endereço esteja associado a uma {@link Area} (bairro/região).
 * Valida campos mínimos (ex.: rua obrigatória).
 * Normaliza campos opcionais (converter vazio para null).
 * >Aplica validação simples de CEP (quando informado).
 */
public final class AddressFactory {
	/**
     * Construtor privado para impedir instanciação.
     */
    private AddressFactory() {}
    
    /**
    * Cria um {@link Address} validando regras básicas.
    * {@code area} obrigatório.
    * {@code street} obrigatório.
    * {@code cep} opcional, mas se informado deve ter 8 caracteres (apenas números).
    * {@code complement} e {@code reference} são opcionais (vazio → null).
    * Valida área e rua.
    * Normaliza CEP: aplica {@code trim()}; se vazio vira null; se preenchido valida tamanho 8.
    * Normaliza complemento e referência: vazio vira null.
    * Cria {@link Address} com os campos normalizados.
    * Se {@code id} for fornecido, aplica no endereço (útil ao reconstruir do banco
    * @param id id do endereço (opcional)
    * @param area área/bairro (obrigatório)
    * @param cep CEP (opcional; se informado deve ter 8 caracteres)
    * @param street rua (obrigatório)
    * @param number número (opcional)
    * @param complement complemento (opcional)
    * @param reference referência (opcional)
    * @return instância válida de {@link Address}
    * @throws ValidationException se alguma validação falhar
    */
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