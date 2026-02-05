package model.entities;

import java.util.Arrays;

import services.EncryptionService;

public class User extends Person {
	
	private Integer idUser;
	private String passwordHash;
	
	public User() {
		super();
	}
	
	public User(Integer idUser, Integer idPerson, String firstName, String lastName, String email, char[] password) throws Exception {
		super(firstName, lastName, email);
		this.idUser = idUser;
		if (idPerson != null) {
			setId(idPerson);
		}
		this.passwordHash = EncryptionService.hashPassword(password);
		Arrays.fill(password, '0');
	}

	/**
	 * Construtor para criar usuário com id da person (para persistência).
	 * Equivalente a new User(null, idPerson, firstName, lastName, email, password).
	 */
	public User(Integer idPerson, String firstName, String lastName, String email, char[] password) throws Exception {
		this(null, idPerson, firstName, lastName, email, password);
	}
	
	public void setIdUser(Integer idUser){
		this.idUser = idUser;
	}
	public Integer getIdUser() {
		return this.idUser;
	}
	
	public void setPasswordHash(String passwordHash){
		this.passwordHash = passwordHash;
	}


	public String getPasswordHash() {
		return passwordHash;
	}


	@Override
	public String toString() {
		return "User [getIdUser()=" + getIdUser() + ", getPasswordHash()=" + getPasswordHash() + "]";
	}
	
	
	
}
