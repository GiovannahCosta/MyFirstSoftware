package controller;

import model.repositories.RepositoryPerson;
import model.repositories.RepositoryUser;
import model.repositories.RepositoryAddress;
import model.repositories.RepositoryArea;
import model.entities.Address;
import model.entities.Area;
import model.entities.Person;
import model.entities.User;

import javax.swing.*;

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
    
    /*public boolean isEmailAlreadyRegistered(String email) {
        try {
            return repoPerson.findByEmailPerson(email) != null;
        } catch (SQLException e) {
            e.printStackTrace();
            return true;
        }
    }*/
    
    
    public List<Area> listAreas() throws SQLException {
    	return repoArea.findAllArea();
    }
    
    private void checkEmpty(String firstName, String email, char[] password, Integer idArea, String street) throws Exception {
    	if (firstName == null || firstName.trim().isEmpty()) {
            throw new Exception("O campo Nome é obrigatório.");
        }
        if (email == null || email.trim().isEmpty()) {
            throw new Exception("O campo E-mail é obrigatório.");
        }
        if (password == null || password.length == 0) {
            throw new Exception("A senha é obrigatória.");
        }
        if (idArea == null) {
            throw new Exception("Selecione um bairro.");
        }
        if (street == null || street.trim().isEmpty()) {
            throw new Exception("O endereço (Rua) é obrigatório.");
        }
    	
    }
    
    public void register(String firstName, String lastName, String email,
            char[] password, Integer idArea, String street, Integer number,
            String cep, String complement, String reference) throws Exception{
    	try {
    		checkEmpty(firstName, email, password, idArea, street);
    	
    		
	    	if (repoPerson.findByEmailPerson(email.trim()) != null) {
	            throw new Exception("E-mail já cadastrado no sistema.");
	        }
	    	
	    	Area area = repoArea.findByIdArea(idArea);
	    	if (area == null) {
	            throw new Exception("A área selecionada é inválida.");
	        }
	    	
	    	persistUser(firstName, lastName, email, password, area, street, number, cep, complement, reference);
    	} catch(Exception e) {
    		throw e;
    	} finally {
    		clearPassword(password);
    	}
    	
    }
    
    
    private void persistUser(String firstName, String lastName, String email,
            char[] password, Area area, String street, Integer number,
            String cep, String complement, String reference) throws Exception {
    	
    	Address address = buildAddress(area, street, number, cep, complement, reference);
    	Integer idAddress = repoAddress.createAddressAndReturnId(address);

    	if (idAddress == null) {
    		throw new Exception("Erro ao salvar o endereço no banco de dados.");
    	}
    	address.setInteger(idAddress); 


    	Person person = new Person(firstName.trim(), trimOrNull(lastName), email.trim(), address);
    	Integer idPerson = repoPerson.createPersonAndReturnId(person);

    	if (idPerson == null) {
    		throw new Exception("Erro ao salvar dados pessoais. O E-mail pode já estar em uso.");
    	}
    	person.setId(idPerson);


    	User user = new User(idPerson, firstName.trim(), trimOrNull(lastName), email.trim(), password);
    	boolean userCreated = repoUser.createUser(user);

    	if (!userCreated) {
    		throw new Exception("Erro crítico ao criar o usuário de login.");
    	}

    }
    
    
    private static String trimOrNull(String s) {
        if(s == null) {
        	return null;
        }
        
        return s.trim();
    }
    
    
    private void clearPassword(char[] password) {
        if (password != null) {
            for (int i = 0; i < password.length; i++) {
                password[i] = '0';
            }
        }
    }
    
    private static Address buildAddress(Area area, String street, Integer number,
            String cep, String complement, String reference) {
    	Address address = new Address();
    	address.setArea(area);
    	address.setCep(trimOrNull(cep));
    	address.setStreet(street.trim());
    	address.setNumber(number);
    	address.setComplement(trimOrNull(complement));
    	address.setReference(trimOrNull(reference));
    	return address;
}
    
    
}