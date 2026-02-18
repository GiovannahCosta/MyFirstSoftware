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

/**
 * Controller responsável pelo caso de uso de Cadastro (registro de um novo usuário).
 * O cadastro envolve múltiplas tabelas/entidades: {@link Address} (endereço), {@link Address} (endereço),{@link User} (credenciais / senha) 
 * A View coleta dados do formulário e chama este controller.
 * O controller valida regras, consulta dependências (ex.: área) e persiste entidades.
 * Exceções são tipadas para a UI reagir corretamente
 * este controller usa {@link AddressFactory}, {@link PersonFactory} e {@link UserFactory} para centralizar validações e criação das entidades.
 *
 */

public class ControllerCadastro {
	
	/**
	 * Repositório para persistência e consulta de {@link Person}
	 * Usado para verificar se o email já existe e para inserir a pessoa.
	 */
    private final RepositoryPerson repoPerson;
    
    
    /**
     * Repositório para persistência e consulta de {@link User}
     * Usado para inserir credenciais (hash de senha ligado a uma {@link Person}).
     */
    private final RepositoryUser repoUser;
    
    /**
     * Repositório para persistência e consulta de {@link Address}.
     * Usado para inserir endereço (que é FK em {@code person}).
     */
    private final RepositoryAddress repoAddress;
    
    /**
     * Repositório para persistência e consulta de {@link Area}.
     * Usado para listar áreas na tela e validar a área selecionada no cadastro.
     */
    private final RepositoryArea repoArea;
    
    /**
     * Construtor padrão.
     * Instancia os repositórios concretos para uso direto pelas Views.
     */
    public ControllerCadastro() {
        this.repoArea = new RepositoryArea();
        this.repoPerson = new RepositoryPerson();
        this.repoUser = new RepositoryUser();
        this.repoAddress = new RepositoryAddress();
    }
    
    /**
     * Construtor com injeção de dependências.
     * Útil em testes automatizados ou para controlar instâncias de repositório.
     * @param repoPerson repositório de pessoas
     * @param repoUser repositório de usuários
     * @param repoAddress repositório de endereços
     * @param repoArea repositório de áreas
     */
    public ControllerCadastro(RepositoryPerson repoPerson, RepositoryUser repoUser,
                              RepositoryAddress repoAddress, RepositoryArea repoArea) {
        this.repoPerson = repoPerson;
        this.repoUser = repoUser;
        this.repoAddress = repoAddress;
        this.repoArea = repoArea;
    }
    
    
    /**
     * Lista bairros/áreas para preencher o combobox da tela de cadastro.
     * Chama {@link RepositoryArea#findAllArea()}
     * Converte {@link SQLException} em {@link DataAccessException}.
     * @return lista de áreas (pode ser vazia)
     * @throws DataAccessException em falhas de acesso ao banco
     */
    public List<Area> listAreas() throws DataAccessException {
        try {
            return repoArea.findAllArea();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao carregar bairros/áreas.", e);
        }
    }
    
    
    /**
     * Realiza o cadastro completo de um usuário.
     * Este método valida regras de entrada e persiste os dados na ordem correta por dependência
     * Valida campos obrigatórios (nome, email, senha, área, rua etc.)
     * Verifica duplicidade de email em {@code person} (UNIQUE) via {@link RepositoryPerson}.
     * Valida se a {@link Area} selecionada existe via {@link RepositoryArea}
     * Persiste endereço (gera id_address)
     * Persiste pessoa (gera id_person)
     * Cria usuário (hash de senha) e persiste em {@code user}
     * limpa o array de senha em memória ao final.
     * @param firstName limpa o array de senha em memória ao final.
     * @param lastName sobrenome (opcional)
     * @param email email (obrigatório)
     * @param password senha em char[] (obrigatória, mínimo definido pela regra)
     * @param idArea id da área selecionada (obrigatório)
     * @param street rua (obrigatório)
     * @param number número (opcional)
     * @param cep CEP (opcional, mas geralmente recomendado)
     * @param complement complemento (opcional)
     * @param reference referência (opcional)
     * @throws ValidationException validação de campos
     * @throws ConflictException email duplicado
     * @throws NotFoundException área inválida
     * @throws DataAccessException falha ao acessar o banco
     */
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
    
    
    /**
     * Persiste um usuário completo (endereço + pessoa + usuário).
     * Cria {@link Address} via {@link AddressFactory} e persiste, obtendo id_address.
     * Cria {@link Address} via {@link AddressFactory} e persiste, obtendo id_address.
     * Cria {@link Person} via {@link PersonFactory} e persiste, obtendo id_person.
     * Este método é {@code private} porque representa implementação interna do caso de uso de cadastro.
     * @param firstName nome já normalizado (trim)
     * @param lastName sobrenome
     * @param email email já normalizado (trim)
     * @param password senha (char[])
     * @param area senha (char[])
     * @param street rua
     * @param number número
     * @param cep cep
     * @param complement complemento
     * @param reference referência
     * @throws SQLException referência
     * @throws ValidationException se alguma factory rejeitar dados
     * @throws DataAccessException falha ao acessar o banco
     */
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