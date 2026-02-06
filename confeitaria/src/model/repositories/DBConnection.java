package model.repositories;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

public class DBConnection {
	
	private static String URL = "jdbc:postgresql://localhost:5432/teste";
	private static String USER = "postgres";
	private static String SENHA = "134340";
	
	public static Connection getConnection() throws SQLException {
		return DriverManager.getConnection(URL, USER, SENHA);
	}

}