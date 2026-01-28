package model.repositories;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import model.entities.Flavor;
import model.entities.FlavorLevel;

public class RepositoryFlavor {
	
	
	private static final String findById = "SELECT * FROM flavor WHERE id = ?";
	private static final String findByLevel = "SELECT * FROM flavors WHERE flavor_level = ?";
	
	
	private Flavor mapResultSetToFlavor(ResultSet rs) throws SQLException {
        Flavor flavor = new Flavor();
        flavor.setId(rs.getInt("id"));
        flavor.setName(rs.getString("name"));
        
        String levelStr = rs.getString("flavor_level");
        if (levelStr != null) {
            flavor.setLevel(FlavorLevel.valueOf(levelStr));
        }
        return flavor;
    }
  
	
	private List<Flavor> findByFilter(String sql, Integer filterValue) {
        List<Flavor> list = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement pstmt = conn.prepareStatement(sql)) {
            
        	pstmt.setInt(1, filterValue);
            
            try (ResultSet rs = pstmt.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFlavor(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
	
	private List<Flavor> findByFilter(String sql, String filterValue) {
        List<Flavor> list = new ArrayList<>();
        
        try (Connection conn = DBConnection.getConnection();
             PreparedStatement ps = conn.prepareStatement(sql)) {
            
            ps.setString(1, filterValue);
            
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    list.add(mapResultSetToFlavor(rs));
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return list;
    }
	
	public List<Flavor> findByLevel(FlavorLevel level) {
        return findByFilter(findByLevel, level.name());
    }
	
	public List<Flavor> findById(Integer id) {
        return findByFilter(findById, id);
    }
}
