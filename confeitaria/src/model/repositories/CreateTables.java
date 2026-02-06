package model.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

public class CreateTables {

	/**
	 * Cria todas as tabelas na ordem das dependências (FK).
	 * Ordem: area → address → person → flavor_level → flavor → size → user → product → order → order_items.
	 */
	public static void createAllTables() {
		createTableArea();
		createTableAddress();
		createTablePerson();
		createTableFlavorLevel();
		createTableFlavor();
		createTableSize();
		createTableUser();
		createTableProduct();
		createTableOrder();
		createTableOrderItems();
	}

	/**
	 * Remove todos os dados das tabelas (ordem reversa das FKs).
	 * Útil para testes que precisam de banco limpo a cada execução.
	 */
	public static void truncateAllTables() {
		String sql = "TRUNCATE order_items, \"order\", product, \"user\", size, flavor, flavor_level, person, address RESTART IDENTITY CASCADE";
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createTablePerson() {
		String createTablePerson = "CREATE TABLE IF NOT EXISTS"
				+ " person (id SERIAL PRIMARY KEY,"
				+ "first_name VARCHAR(30) NOT NULL,"
				+ "last_name VARCHAR(30),"
				+ "email TEXT NOT NULL UNIQUE,"
				+ "id_address INTEGER NOT NULL,"
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
			
			System.out.println("Create person successful");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	public static void createTableUser() {
		String createTableUser = "CREATE TABLE IF NOT EXISTS"
				+ " \"user\" (id SERIAL PRIMARY KEY,"
				+ "id_person INTEGER NOT NULL UNIQUE,"
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
			
			System.out.println("Create user successful");
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
			
			System.out.println("Create area successful");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void createTableAddress() {
		String createTableAddress = "CREATE TABLE IF NOT EXISTS "
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
			
			System.out.println("Create address successful");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void createTableOrder() {
		String createTableOrder = "CREATE TABLE IF NOT EXISTS"
				+ " \"order\" (id SERIAL PRIMARY KEY,"
				+ "id_user INTEGER NOT NULL,"
				+ "datetime TIMESTAMP NOT NULL,"
				+ "total_price DECIMAL(10, 2) NOT NULL,"
				+ "delivery VARCHAR(20) NOT NULL,"
				+ "observations TEXT,"
				+ "CONSTRAINT fk_user FOREIGN KEY (id_user) REFERENCES \"user\"(id) ON DELETE CASCADE"
				+ ");";
		
		
		try(Connection conn = DBConnection.getConnection())
		{
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(createTableOrder);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Create order successful");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	}
	
	
	public static void createTableProduct() {
		String createTableProduct = "CREATE TABLE IF NOT EXISTS"
				+ " product (id SERIAL PRIMARY KEY,"
				+ "name VARCHAR(20) NOT NULL,"
				+ "id_flavor INTEGER NOT NULL,"
				+ "id_size INTEGER NOT NULL,"
				+ "description TEXT,"
				+ "CONSTRAINT fk_flavor FOREIGN KEY (id_flavor) REFERENCES flavor(id),"
				+ "CONSTRAINT fk_size FOREIGN KEY (id_size) REFERENCES \"size\"(id)"
				+ ");";
		
		try(Connection conn = DBConnection.getConnection()){
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(createTableProduct);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Create product successful");
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
				+ "CONSTRAINT fk_order FOREIGN KEY (id_order) REFERENCES \"order\"(id) ON DELETE CASCADE,"
				+ "CONSTRAINT fk_product FOREIGN KEY (id_product) REFERENCES product(id)"
				+ ");";
	
		try(Connection conn = DBConnection.getConnection()){
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(createTableOrderItems);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Create order_items successful");
		} catch(SQLException e) {
			e.printStackTrace();
		}
	
	
	}	


	public static void createTableFlavorLevel() {
		String sql = "CREATE TABLE IF NOT EXISTS flavor_level ("
				+ "id SERIAL PRIMARY KEY,"
				+ "name VARCHAR(12) NOT NULL,"
				+ "price DECIMAL(10, 2) NOT NULL"
				+ ");";
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
			System.out.println("Create flavor_level successful");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	public static void createTableFlavor(){
		String createTableFlavor = "CREATE TABLE IF NOT EXISTS"
			+ " flavor (id SERIAL PRIMARY KEY,"
			+ "name TEXT NOT NULL,"
			+ "id_flavor_level INTEGER NOT NULL,"
			+ "price DECIMAL(10, 2) NOT NULL,"
			+ "description TEXT,"
			+ "CONSTRAINT fk_flavor_level FOREIGN KEY (id_flavor_level) REFERENCES flavor_level(id)"
			+ ");";

			try(Connection conn = DBConnection.getConnection()){
			try {
				Statement stmt = conn.createStatement();
				stmt.execute(createTableFlavor);
			} catch(SQLException e) {
				e.printStackTrace();
			}
			
			System.out.println("Create flavor successful");
		} catch(SQLException e) {
			e.printStackTrace();
		}

	}

	/**
	 * Tabela size (tamanhos). Nome entre aspas pois "size" é palavra reservada em SQL.
	 */
	public static void createTableSize() {
		String sql = "CREATE TABLE IF NOT EXISTS \"size\" ("
				+ "id SERIAL PRIMARY KEY,"
				+ "name VARCHAR(4) NOT NULL,"
				+ "yield VARCHAR(20) NOT NULL,"
				+ "weight VARCHAR(10) NOT NULL,"
				+ "price DECIMAL(10, 2) NOT NULL"
				+ ");";
		try (Connection conn = DBConnection.getConnection();
				Statement stmt = conn.createStatement()) {
			stmt.execute(sql);
			System.out.println("Create size successful");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
	
}
