package model.entities;

import java.util.Arrays;

import services.EncryptionService;

public class User extends Person {
	
	private Integer idUser;
	private String passwordHash;
	
	public User() {
		super();
	}
	
	public User(Integer idUser, String firstName, String lastName, String email, char[] password) throws Exception{
		super(firstName, lastName, email);
		this.passwordHash = EncryptionService.hashPassword(password);
		Arrays.fill(password,'0');
	}
	
	public void setIdUser(Integer idUser){
		this.idUser = idUser;
	}
	public Integer getIdUser() {
		return this.idUser;
	}
	
	public String getPasswordHash() {
		return passwordHash;
	}


	@Override
	public String toString() {
		return "User [getIdUser()=" + getIdUser() + ", getPasswordHash()=" + getPasswordHash() + "]";
	}
	
	
	
}
