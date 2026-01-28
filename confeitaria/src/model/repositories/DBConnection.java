package model.repositories;

import java.sql.Connection;

import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	
	// Usa variáveis de ambiente se disponíveis, senão usa valores padrão
	private static final String ADDRESS_IP = System.getenv("DB_HOST") != null 
		? System.getenv("DB_HOST") 
		: "127.0.0.1";
	private static final String ADDRESS_PORT = System.getenv("DB_PORT") != null 
		? System.getenv("DB_PORT") 
		: "5434";
	private static final String NAME_DATABASE = System.getenv("DB_NAME") != null 
		? System.getenv("DB_NAME") 
		: "confeitaria";
	private static final String USER = System.getenv("DB_USER") != null 
		? System.getenv("DB_USER") 
		: "admin";
	private static final String PASSWORD = System.getenv("DB_PASSWORD") != null 
		? System.getenv("DB_PASSWORD") 
		: "12345";
	
	public static Connection getConnection() throws SQLException {
		
		return DriverManager.getConnection("jdbc:postgresql://"+ ADDRESS_IP + ":" 
		+ ADDRESS_PORT + "/" + 
		NAME_DATABASE,
		USER,
		PASSWORD);
	}

}