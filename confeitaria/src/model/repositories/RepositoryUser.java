package model.repositories;


import model.entities.Person;
import model.entities.User;
import java.sql.Connection;
import java.sql.SQLException; 
import java.sql.PreparedStatement;
import java.sql.ResultSet;

import model.entities.User;

public class RepositoryUser {

	private static final String insertUser = "INSERT INTO user(firstName, lastName, email, password_hash) VALUES (?,?) "
			+ "ON CONFLICT (email) DO NOTHING";


	public boolean createUser(User user) throws SQLException {
		try (Connection conn = DBConnection.getConnection();
			PreparedStatement stmt = conn.prepareStatement(insertUser)) { 
			
			stmt.setString(1, user.getFirstName());
			stmt.setString(2, user.getLastName());
			stmt.setString(3, user.getEmail());
			stmt.setString(4, user.getPasswordHash());
			
			int rowsAffected = stmt.executeUpdate();
			
			return rowsAffected > 0;
			
		} catch (SQLException e) {
			e.printStackTrace();
			return false;
		}
	}
	

}
