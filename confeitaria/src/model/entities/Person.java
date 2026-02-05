package model.entities;

public class Person {

	private Integer id;
	private String firstName;
	private String lastName;
	private String email;
	private Address address;
	
	public Person() {}
	
	public Person(String firstName, String lastName, String email, Address address) {
		setFirstName(firstName);
		setLastName(lastName);
		setEmail(email);
		setAddress(address);
	}
	
	public Person(String firstName, String lastName, String email) {
		setFirstName(firstName);
		setLastName(lastName);
		setEmail(email);
	}
	
	public void setId(Integer id){
		this.id = id;
	}
	
	public Integer getId() {
		return this.id;
	}
	
	public String getFirstName() {
		return this.firstName;
	}
	
	public void setFirstName(String firstName) {
		this.firstName = firstName;
	}
	
	public String getLastName() {
		return this.lastName;
	}
	
	public void setLastName(String lastName) {
		this.lastName = lastName;
	}
	
	public String getEmail() {
		return this.email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	

	public Address getAddress() {
		return address;
	}

	public void setAddress(Address address) {
		this.address = address;
	}
	
	
	@Override
	public String toString() {
		return "Person [getId()=" + getId() + ", getFirstName()=" + getFirstName() + ", getLastName()=" + getLastName()
				+ ", getEmail()=" + getEmail() + ", getAddress()=" + getAddress() + "]";
	}
	
	
}
