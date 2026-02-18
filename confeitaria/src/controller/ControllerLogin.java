package controller;

import exceptions.AuthenticationException;
import exceptions.DataAccessException;
import exceptions.ValidationException;
import model.entities.User;
import model.repositories.RepositoryUser;
import services.EncryptionService;

import java.sql.SQLException;
import java.util.Arrays;

public class ControllerLogin {

    private final RepositoryUser repoUser;

    public ControllerLogin() {
        this.repoUser = new RepositoryUser();
    }

    public ControllerLogin(RepositoryUser repoUser) {
        this.repoUser = repoUser;
    }

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