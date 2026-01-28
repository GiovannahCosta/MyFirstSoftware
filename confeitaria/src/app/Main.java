package app;

import model.repositories.CreateTables;
import model.entities.User;
import model.repositories.RepositoryUser;
import services.EncryptionService;

public class Main {

    public static void main(String[] args) {
        try {
            CreateTables.createTableUser();

            char[] password = "julia123".toCharArray();
            User user = new User(1, "Julia", "Pinheiro", "susulaju123@gmail.com", password);
            
            
            RepositoryUser repositoryUser = new RepositoryUser();
            boolean userCreated = repositoryUser.createUser(user);
            
            if (userCreated) {
                System.out.println("Usuário criado com sucesso!");
            } else {
                System.out.println("Usuário já existe ou erro ao criar.");
            }

            boolean successful = EncryptionService.checkPassword("julia123".toCharArray(), user.getPasswordHash());

            if (successful) {
                System.out.println("Login realizado com sucesso! Bem-vindo.");
            } else {
                System.out.println("Senha incorreta! Acesso negado.");
            }
        } catch (Exception e) {
            System.err.println("Erro: " + e.getMessage());
            e.printStackTrace();
        }
    }
}