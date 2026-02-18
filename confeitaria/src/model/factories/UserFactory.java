package model.factories;

import exceptions.ValidationException;
import model.entities.User;

/**
 * Factory responsável por criar instâncias de {@link User} com senha (hash).
 * Centralizar validações de dados de cadastro/autenticação relacionados ao usuário.
 * Garantir que as regras mínimas (ex.: nome, email e senha) sejam aplicadas antes de criar o objeto.
 * Encapsular erros inesperados durante o processamento de senha (ex.: hashing).
 * o construtor de {@link User} pode realizar processamento de senha (hash), podendo lançar exceções.
 * Esta factory captura tais erros e converte para {@link ValidationException} com mensagem amigável.
 */
public final class UserFactory {
	
	/**
     * Construtor privado para impedir instanciação.
     * Esta factory deve ser usada apenas via métodos estáticos.
     */
    private UserFactory() {}
    
    /**
     * Cria um {@link User} validando nome, email e senha.
     * {@code firstName} obrigatório e não vazio.
     * {@code email} obrigatório, não vazio e com validação simplificada.
     * {@code password} obrigatória com tamanho mínimo de 4 caracteres
     * Valida parâmetros obrigatórios.
     * Normaliza campos com {@code trim()} (nome, sobrenome e email).
     * Chama o construtor de {@link User} para criar a entidade (incluindo processamento da senha).
     * Se ocorrer qualquer erro inesperado na criação (ex.: hashing), lança {@link ValidationException}.
     * @param idUser id do usuário (opcional; útil ao reconstruir do banco)
     * @param idPerson id da pessoa associada (obrigatório para persistência)
     * @param firstName primeiro nome (obrigatório)
     * @param lastName sobrenome (opcional)
     * @param email email (obrigatório)
     * @param password senha em {@code char[]} (obrigatória; mínimo de 4 chars)
     * @return instância válida de {@link User}
     * @throws ValidationException se os dados forem inválidos ou se a senha não puder ser processada
     */
    public static User createWithPassword(Integer idUser, Integer idPerson, String firstName, String lastName,
                                          String email, char[] password) throws ValidationException {

        if (firstName == null || firstName.trim().isEmpty())
            throw new ValidationException("Nome é obrigatório.");

        if (email == null || email.trim().isEmpty())
            throw new ValidationException("E-mail é obrigatório.");

        String em = email.trim();
        if (!em.contains("@") || !em.contains(".com"))
            throw new ValidationException("E-mail inválido.");

        if (password == null || password.length < 4)
            throw new ValidationException("Senha deve ter pelo menos 4 caracteres.");

        String ln = (lastName != null && !lastName.trim().isEmpty()) ? lastName.trim() : null;

        try {
            return new User(idUser, idPerson, firstName.trim(), ln, em, password);
        } catch (Exception e) {
            throw new ValidationException("Não foi possível processar a senha. Tente novamente.");
        }
    }
}