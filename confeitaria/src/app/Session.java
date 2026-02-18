package app;

import model.entities.User;

/**
 * Sessão simples para manter o usuário logado.
 * Mantém baixo acoplamento: qualquer tela pode consultar Session.getLoggedUser().
 */
public final class Session {
    private static User loggedUser;

    private Session() {}

    public static void setLoggedUser(User user) {
        loggedUser = user;
    }

    public static User getLoggedUser() {
        return loggedUser;
    }

    public static boolean isLoggedIn() {
        return loggedUser != null && loggedUser.getEmail() != null && !loggedUser.getEmail().isBlank();
    }

    public static void logout() {
        loggedUser = null;
    }
}