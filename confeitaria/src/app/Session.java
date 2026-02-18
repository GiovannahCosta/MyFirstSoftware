package app;

import model.entities.User;

/**
 * Sessão do usuário logado (estado global em memória).
 * Armazena o usuário autenticado para permitir acesso por qualquer tela.
 * Fornece uma forma simples de verificar se existe login ativo.
 * Centraliza as operações de login/logout em uma estrutura única.
 * Obs.: Esta sessão é mantida apenas em memória (variáveis estáticas).
 * Ao fechar o app, o login é perdido.
 */
public final class Session {
	/**
     * Usuário que logado está logado agora.
     * Quando {@code null}, significa que não existe usuário autenticado na sessão.
     */
    private static User loggedUser;
    
    /**
     * Construtor privado para impedir instanciação.
     * Esta classe representa uma sessão global via estado estático.
     */
    private Session() {}
    
    /**
     * Define o usuário logado na sessão.
     * Normalmente chamado após um login bem-sucedido.
     * @param user usuário autenticado (pode ser null para deslogar indiretamente)
     */
    public static void setLoggedUser(User user) {
        loggedUser = user;
    }
    
    /**
     * Retorna o usuário logado atual.
     * @return usuário logado ou null se não houver login
     */
    public static User getLoggedUser() {
        return loggedUser;
    }
    
    /**
     * Verifica se existe um usuário logado válido.
     * o objeto {@code loggedUser} não pode ser null
     * o email não pode ser null
     * o email não pode estar em branco.
     * @return true se a sessão contém um usuário com email válido
     */
    public static boolean isLoggedIn() {
        return loggedUser != null && loggedUser.getEmail() != null && !loggedUser.getEmail().isBlank();
    }
    
    /**
     * Encerra a sessão do usuário (logout).
     * >Define {@code loggedUser} como null
     * Após isso, {@link isLoggedIn()} passa a retornar false.
     */
    public static void logout() {
        loggedUser = null;
    }
}