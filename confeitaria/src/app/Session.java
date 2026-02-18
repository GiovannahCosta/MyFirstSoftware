package app;

import model.entities.User;

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