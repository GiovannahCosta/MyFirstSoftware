package model.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTables {
	
	public static void createTablePerson() {
		String createTablePerson = "CREATE TABLE IF NOT EXISTS"
				+ " person (id SERIAL PRIMARY KEY,"
				+ "first_name VARCHAR(30) NOT NULL,"
				+ "last_name VARCHAR(30),"
				+ "email TEXT NOT NULL UNIQUE,"
				+ "id_address TEXT NOT NULL,"
				+ "CONSTRAINT fk_address FOREIGN KEY (id_address) REFERENCES address(id) ON DELETE CASCADE"
				+ ");";
		
		try(Connection conn = DBConnection.getConnection())
		{
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(createTablePerson);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Create person sucessfull");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createTableUser() {
		String createTableUser = "CREATE TABLE IF NOT EXISTS"
				+ " user (id SERIAL PRIMARY KEY,"
				+ "id_person INTEGER NOT NULL UNIQUE,"
				+ "login VARCHAR(50) NOT NULL UNIQUE,"
				+ "password_hash TEXT NOT NULL,"
				+ "CONSTRAINT fk_person FOREIGN KEY (id_person) REFERENCES person(id) ON DELETE CASCADE"
				+ ");";
		
		try(Connection conn = DBConnection.getConnection())
		{
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(createTableUser);
				
				if(stmt != null) stmt.close();
				
			}catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Create user succesfull");
		}catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void createTableArea() {
		String createTableArea = "CREATE TABLE IF NOT EXISTS"
				+ " area (id SERIAL PRIMARY KEY,"
				+ "name TEXT NOT NULL UNIQUE,"
				+ "fee DECIMAL(10, 2) NOT NULL"
				+ ");";
		
		try(Connection conn = DBConnection.getConnection())
		{
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(createTableArea);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Create area sucessfull");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void createTableAddress() {
		String createTableAddress = "CREATE TABLE IF NOT EXISTS"
				+ "address (id SERIAL PRIMARY KEY,"
				+ "id_area INTEGER NOT NULL,"
				+ "cep VARCHAR(8),"
				+ "street TEXT NOT NULL,"
				+ "number INTEGER,"
				+ "complement TEXT,"
				+ "reference TEXT,"
				+ "CONSTRAINT fk_area FOREIGN KEY (id_area) REFERENCES area(id) ON DELETE RESTRICT"
				+ ");";
		
		try(Connection conn = DBConnection.getConnection())
		{
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(createTableAddress);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Create address sucessfull");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void createTableOrder() {
		String createTableOrder = "CREATE TABLE IF NOT EXISTS"
				+ " order (id SERIAL PRIMARY KEY,"
				+ "id_user INTEGER NOT NULL,"
				+ "datetime DATETIME NOT NULL,"
				+ "total_price DECIMAL(10, 2) NOT NULL,"
				+ "delivery ENUM NOT NULL,"
				+ "observations TEXT,"
				+ "CONSTRAINT fk_user FOREIGN KEY (id_user) REFERENCES user(id) ON DELETE CASCADE"
				+ ");";
		
		
		try(Connection conn = DBConnection.getConnection())
		{
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(createTableOrder);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Create order sucessfull");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void createTableProduct() {
		String createTableProduct = "CREATE TABLE IF NOT EXISTS"
				+ " product (id SERIAL PRIMARY KEY,"
				+ "name VARCHAR(20) NOT NULL,"
				+ "flavor TEXT NOT NULL,"
				+ "flavor_level ENUM,"
				+ "size ENUM,"
				+ "price DECIMAL(10,2) NOT NULL,"
				+ "description TEXT"
				+ ");";
		
		try(Connection conn = DBConnection.getConnection()){
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(createTableProduct);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Create product sucessfull");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void createTableOrderItems() {
		String createTableOrderItems = "CREATE TABLE IF NOT EXISTS"
				+ " order_items (id SERIAL PRIMARY KEY,"
				+ "id_order INTEGER NOT NULL,"
				+ "id_product INTEGER NOT NULL,"
				+ "quantity INTEGER NOT NULL,"
				+ "price_at_moment DECIMAL(10, 2) NOT NULL,"
				+ "CONSTRAINT fk_order FOREIGN KEY (id_order) REFERENCES order(id) ON DELETE CASCADE,"
				+ "CONSTRAINT fk_product FOREIGN KEU (id_product) REFERENCES producr(id)"
				+ ");";
	
		try(Connection conn = DBConnection.getConnection()){
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(createTableOrderItems);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Create order_items sucessfull");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	
	
	}	
	
}
