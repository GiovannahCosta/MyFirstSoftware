package controller;

import exceptions.AuthenticationException;
import exceptions.DataAccessException;
import exceptions.ValidationException;
import model.entities.User;
import model.repositories.RepositoryUser;
import services.EncryptionService;

import java.sql.SQLException;
import java.util.Arrays;

/**
 * Controller responsável pelo caso de uso de autenticação (Login).
 * Responsável por validar entrada (email e senha), 
 * consultar usuário no banco por email via {@link RepositoryUser}, 
 * verificar se a senha informada corresponde ao hash armazenado,
 * lançar exceções de domínio ({@link ValidationException}, {@link AuthenticationException})
 * e encapsular erros técnicos em {@link DataAccessException}.
 * este controller limpa o array de senha ({@code char[]}) no bloco {@code finally} para reduzir o tempo de permanência da senha em memória.
 */
public class ControllerLogin {
	
	/**
	 * Repositório para leitura de usuários
	 * Usado para buscar o usuário pelo email (ex.: {@code findByEmailUser})
	 */
    private final RepositoryUser repoUser;
    
    
    /**
     * Construtor padrão.
     * Cria uma instância concreta de {@link RepositoryUser}.
     */
    public ControllerLogin() {
        this.repoUser = new RepositoryUser();
    }
    
    
    /**
     * Construtor com injeção de dependência.
     * @param repoUser repositório a ser usado pelo controller (não deve ser null)
     */
    public ControllerLogin(RepositoryUser repoUser) {
        this.repoUser = repoUser;
    }
    
    /**
     * Realiza login (autenticação) validando credenciais
     * Valida se o email foi informado e normaliza com {@code trim()}.
     * Valida se o email foi informado e normaliza com {@code trim()}.
     * Busca o usuário no banco por email ({@link RepositoryUser#findByEmailUser(String)})
     * Se não existir, lança {@link AuthenticationException}.
     * Gera um hash da senha digitada e compara com o hash salvo
     * Se não bater, lança {@link AuthenticationException}
     * Se bater, retorna o {@link User} autenticado.
     * {@link SQLException} e outras exceções inesperadas viram {@link DataAccessException}.
     * ao final, o conteúdo do array {@code password} é limpo.
     * @param email email digitado
     * @param password senha digitada (em char[] para permitir limpeza)
     * @return usuário autenticado
     * @throws ValidationException se email/senha estiverem vazios
     * @throws AuthenticationException se credenciais forem inválidas
     * @throws DataAccessException se ocorrer falha de banco ou erro inesperado
     */
    public User login(String email, char[] password)
            throws ValidationException, AuthenticationException, DataAccessException {

        if (email == null || email.trim().isEmpty())
            throw new ValidationException("E-mail é obrigatório.");

        String em = email.trim();

        if (password == null || password.length == 0)
            throw new ValidationException("Senha é obrigatória.");

        try {
            User user = repoUser.findByEmailUser(em);
            if (user == null) throw new AuthenticationException("E-mail ou senha inválidos.");

            String typedHash = EncryptionService.hashPassword(password);
            boolean ok = typedHash != null && typedHash.equals(user.getPasswordHash());

            if (!ok) throw new AuthenticationException("E-mail ou senha inválidos.");

            return user;

        } catch (SQLException e) {
            throw new DataAccessException("Erro ao acessar o banco durante o login.", e);
        } catch (Exception e) {
            throw new DataAccessException("Erro inesperado ao realizar login: " + e.getMessage(), e);
        } finally {
            Arrays.fill(password, '\0');
        }
    }
}