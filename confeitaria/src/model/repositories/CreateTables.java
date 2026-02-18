package model.repositories;

import java.sql.Connection;
import java.sql.SQLException;
import java.sql.Statement;

/**
 * Classe utilitária responsável por criar a estrutura do banco (DDL).
 * Cria todas as tabelas necessárias para o funcionamento do sistema.
 * Garante que a aplicação consiga iniciar em um banco "limpo" sem scripts manuais.
 * 
 * Como funciona:
 * - Usa {@code CREATE TABLE IF NOT EXISTS} para evitar erro caso a tabela já exista.
 * - Cria tabelas na ordem correta de dependências (chaves estrangeiras).
 * - Obtém conexão através de {@link DBConnection#getConnection()}.
 * 
 * Ordem (FK): area → address → person → flavor_level → flavor → size → user → product → order → order_items
 */
public class CreateTables {

	/**
	 * Cria todas as tabelas na ordem das dependências (FK).
	 * Ordem: area → address → person → flavor_level → flavor → size → user → product → order → order_items.
	 * Chama os métodos {@code createTableX()} na sequência correta.
	 * Cada método cria a tabela com {@code CREATE TABLE IF NOT EXISTS}.
	 * a ordem é importante para evitar erro ao criar chaves estrangeiras (FK) apontando para tabelas que ainda não existem.
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
	 * Reinicia o banco para um estado vazio, preservando a estrutura
	 * Útil para testes que precisam de banco limpo a cada execução.
	 * Executa um {@code TRUNCATE} com {@code RESTART IDENTITY} para zerar sequences
	 * Usa {@code CASCADE} para respeitar dependências
	 * 
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
	
	/**
	 * Cria a tabela {@code person}.
	 * tabela {@code person} referencia {@code address} via {@code id_address}.
	 * Por isso, {@code address} deve existir antes.
	 */
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
	
	/**
	 * Cria a tabela {@code "user"} (palavra reservada, por isso com aspas).
	 *  tabela {@code "user"} referencia {@code person} via {@code id_person}.
	 */
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
	
	/**
	 * Cria a tabela {@code area}.
	 * Representa bairros/regiões com taxa de entrega ({@code fee}).
	 */
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
	
	
	/**
	 * Cria a tabela {@code address}.
	 * Guarda endereço e referencia {@code area} via {@code id_area}.
	 */
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
	
	/**
	 * Cria a tabela {@code "order"} (palavra reservada, por isso com aspas).
	 * Guarda o cabeçalho do pedido: usuário, data/hora, total, forma de recebimento (entrega/retirada) e observações.
	 */
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
	
	/**
	 * Cria a tabela {@code product}.
	 * Referencia {@code flavor} e {@code size} para compor o produto final, além de armazenar o {@code base_price}.
	 */
	public static void createTableProduct() {
	    String createTableProduct = "CREATE TABLE IF NOT EXISTS"
	            + " product (id SERIAL PRIMARY KEY,"
	            + "name TEXT NOT NULL,"
	            + "id_flavor INTEGER NOT NULL,"
	            + "id_size INTEGER NOT NULL,"
	            + "base_price DECIMAL(10, 2) NOT NULL,"
	            + "description TEXT,"
	            + "CONSTRAINT fk_flavor FOREIGN KEY (id_flavor) REFERENCES flavor(id) ON DELETE RESTRICT,"
	            + "CONSTRAINT fk_size FOREIGN KEY (id_size) REFERENCES size(id) ON DELETE RESTRICT"
	            + ");";

	    try(Connection conn = DBConnection.getConnection()) {
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
	
	/**
	 * Cria a tabela {@code order_items}.
	 * Guarda os itens do pedido (produto, quantidade) e o preço no momento da compra ({@code price_at_moment}).
	 * Isso evita que alterações futuras no preço alterem pedidos antigos.
	 */
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

	/**
	 * Cria a tabela {@code flavor_level}.
	 * Representa níveis do sabor (ex.: Tradicional, Especial) com preço adicional.
	 */
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
	
	
	/**
	 * Cria a tabela {@code flavor}.
	 * Representa o sabor do produto e referencia {@code flavor_level} via {@code id_flavor_level}.
	 */
	public static void createTableFlavor(){
	    String createTableFlavor = "CREATE TABLE IF NOT EXISTS"
	            + " flavor (id SERIAL PRIMARY KEY,"
	            + "name TEXT NOT NULL,"
	            + "id_flavor_level INTEGER NOT NULL,"
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
	 * Cria a tabela {@code size}.
	 * Guarda opções de tamanho (nome, rendimento, peso) e preço adicional.
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
