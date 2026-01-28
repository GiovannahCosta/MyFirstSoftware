package model.repositories;

import model.entities.Size;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import model.entities.Flavor;
import model.entities.FlavorLevel;
import model.entities.Product;

public class RepositoryProduct {
	private static final String insertProduct = "";
	private static final String findBySize = "SELECT * FROM product WHERE size = ?";
	private static final String findByFlavorLevel = "SELECT p.* FROM product p " +
	"JOIN flavor f ON p.flavor_id = f.id " +
			"WHERE f.level = ?";
	private static final String findByFlavor = "SELECT * FROM products WHERE flavor_id = ?";
	
	//isso aqui é pra pegar o id do flavor e achar o level dele
	private RepositoryFlavor repositoryFlavor;
    public RepositoryProduct(RepositoryFlavor repositoryFlavor) {
        this.repositoryFlavor = repositoryFlavor;
    }
	
	private Product mapResultSetToProduct(ResultSet rs) throws SQLException {
		Product p = new Product();
        p.setId(rs.getInt("id"));
        p.setName(rs.getString("name"));
        p.setPrice(rs.getDouble("price"));
        p.setSize(Size.valueOf(rs.getString("size")));
	    
        //mantém a lista, mas como é por id o filtro, só vai retornar um flavor, então vamos pegar a primeira opção
        int flavorId = rs.getInt("flavor_id");
        List<Flavor> list = repositoryFlavor.findById(flavorId);
        if (!list.isEmpty()) {
            p.setFlavor(list.get(0));
        }
	    
	    return p;
    }
	
	private List<Product> findByFilter(String sql, String filterValue) {
		List<Product> list = new ArrayList<>();
		try(Connection conn = DBConnection.getConnection();
				PreparedStatement pstmt = conn.prepareStatement(sql))
		{
			pstmt.setString(1, filterValue);
			
			try(ResultSet rs = pstmt.executeQuery()) {
				while (rs.next()) {
                    list.add(mapResultSetToProduct(rs)); 
                }
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}
		
		return list;
	}
	
	public List<Product> findBySize(Size size) {
		return findByFilter(findBySize, size.name());
	}
	
	
	public List<Product> findByFlavorLevel(FlavorLevel level){
		return findByFilter(findByFlavorLevel, level.name());
	}
	
	public List<Product> findByFlavor(Flavor flavor){
		return findByFilter(findByFlavor, String.valueOf(flavor.getId()));
	}
	
}
