package model.factories;

import exceptions.ValidationException;
import model.entities.User;

public final class UserFactory {
    private UserFactory() {}

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
            // erro inesperado na geração de hash/criptografia
            throw new ValidationException("Não foi possível processar a senha. Tente novamente.");
        }
    }
}