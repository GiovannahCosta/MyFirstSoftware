package controller;

import exceptions.ConflictException;
import exceptions.DataAccessException;
import exceptions.NotFoundException;
import exceptions.ValidationException;
import model.entities.Address;
import model.entities.Area;
import model.entities.Person;
import model.entities.User;
import model.factories.AddressFactory;
import model.factories.PersonFactory;
import model.factories.UserFactory;
import model.repositories.RepositoryAddress;
import model.repositories.RepositoryArea;
import model.repositories.RepositoryPerson;
import model.repositories.RepositoryUser;

import java.sql.SQLException;
import java.util.Arrays;
import java.util.List;

public class ControllerCadastro {

    private final RepositoryPerson repoPerson;
    private final RepositoryUser repoUser;
    private final RepositoryAddress repoAddress;
    private final RepositoryArea repoArea;

    public ControllerCadastro() {
        this.repoArea = new RepositoryArea();
        this.repoPerson = new RepositoryPerson();
        this.repoUser = new RepositoryUser();
        this.repoAddress = new RepositoryAddress();
    }

    public ControllerCadastro(RepositoryPerson repoPerson, RepositoryUser repoUser,
                              RepositoryAddress repoAddress, RepositoryArea repoArea) {
        this.repoPerson = repoPerson;
        this.repoUser = repoUser;
        this.repoAddress = repoAddress;
        this.repoArea = repoArea;
    }

    public List<Area> listAreas() throws DataAccessException {
        try {
            return repoArea.findAllArea();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao carregar bairros/áreas.", e);
        }
    }

    public void register(String firstName, String lastName, String email,
                         char[] password, Integer idArea, String street, Integer number,
                         String cep, String complement, String reference)
            throws ValidationException, ConflictException, NotFoundException, DataAccessException {

        try {
            if (firstName == null || firstName.trim().isEmpty())
                throw new ValidationException("Nome é obrigatório.");

            if (email == null || email.trim().isEmpty())
                throw new ValidationException("E-mail é obrigatório.");

            String em = email.trim();
            if (!em.contains("@") || !em.contains(".com"))
                throw new ValidationException("E-mail inválido.");

            if (password == null || password.length < 4)
                throw new ValidationException("Senha deve ter pelo menos 4 caracteres.");

            if (idArea == null || idArea <= 0)
                throw new ValidationException("Selecione um bairro (região).");

            if (street == null || street.trim().isEmpty())
                throw new ValidationException("Rua é obrigatória.");

            if (repoPerson.findByEmailPerson(em) != null) {
                throw new ConflictException("E-mail já cadastrado no sistema.");
            }

            Area area = repoArea.findByIdArea(idArea);
            if (area == null) {
                throw new NotFoundException("O bairro selecionado é inválido.");
            }

            persistUser(firstName.trim(), lastName, em, password, area, street, number, cep, complement, reference);

        } catch (SQLException e) {
            throw new DataAccessException("Erro ao acessar o banco durante o cadastro.", e);
        } catch (ValidationException | ConflictException | NotFoundException | DataAccessException e) {
            throw e;
        } catch (Exception e) {
            throw new DataAccessException("Erro inesperado durante o cadastro: " + e.getMessage(), e);
        } finally {
            if (password != null) Arrays.fill(password, '\0');
        }
    }

    private void persistUser(String firstName, String lastName, String email,
                             char[] password, Area area, String street, Integer number,
                             String cep, String complement, String reference)
            throws SQLException, ValidationException, DataAccessException {

        Address address = AddressFactory.create(null, area, cep, street, number, complement, reference);

        Integer idAddress = repoAddress.createAddressAndReturnId(address);
        if (idAddress == null) {
            throw new DataAccessException("Erro ao salvar o endereço no banco de dados.", null);
        }
        address.setInteger(idAddress);

        Person person = PersonFactory.create(null, firstName, lastName, email, address);

        Integer idPerson = repoPerson.createPersonAndReturnId(person);
        if (idPerson == null) {
            throw new DataAccessException("Erro ao salvar dados pessoais. O e-mail pode já estar em uso.", null);
        }
        person.setId(idPerson);

        User user = UserFactory.createWithPassword(null, idPerson, firstName, lastName, email, password);
        boolean userCreated = repoUser.createUser(user);

        if (!userCreated) {
            throw new DataAccessException("Erro crítico ao criar o usuário de login.", null);
        }
    }
}