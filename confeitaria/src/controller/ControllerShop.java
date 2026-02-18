package controller;

import exceptions.DataAccessException;
import model.entities.Product;
import model.repositories.RepositoryProduct;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller responsável pelo caso de uso "Loja" (Shop).
 * Serve como camada intermediária entre as Views de compra (ex.: listagem de produtos)
 * e os repositórios de dados, encapsulando:
 * a consulta de produtos no banco (via {@link RepositoryProduct});
 * a conversão de {@link SQLException} em {@link DataAccessException} com mensagens amigáveis.
 * Em um padrão MVC, este controller é usado por telas como {@code ViewShopProducts} e também
 * por controllers auxiliares (ex.: carrinho) que precisem carregar produtos pelo id.
 */

public class ControllerShop {
	
	/**
	 * Repositório de produtos (camada de persistência).
	 * É usado para executar operações de leitura no banco (SELECT) relacionadas a {@link Product}.
     * O controller não contém SQL: ele delega ao repositório.
	 */
    private final RepositoryProduct repoProduct;
    
    /**
     * Construtor padrão.
     * Cria uma instância padrão de {@link RepositoryProduct}, permitindo uso simples nas Views
     * sem necessidade de injeção manual.
     */
    public ControllerShop() {
        this.repoProduct = new RepositoryProduct();
    }
    
    /**
     * Construtor com injeção de dependência.
     * Útil para testes (ex.: fornecer um repositório mock/fake) ou para controlar melhor as dependências.
     * @param repoProduct instância de repositório a ser utilizada (não deve ser null)
     */
    public ControllerShop(RepositoryProduct repoProduct) {
        this.repoProduct = repoProduct;
    }
    
    
    /**
     * Lista todos os produtos cadastrados.
     * Chama {@link RepositoryProduct#findAllProduct()} para buscar os dados do banco.
     * Se ocorrer {@link SQLException}, converte para {@link DataAccessException}
     * @return lista de produtos (pode ser vazia; em geral não deve ser null dependendo do repositório)
     * @throws DataAccessException se ocorrer falha ao acessar o banco
     */
    public List<Product> listAllProducts() throws DataAccessException {
        try {
            return repoProduct.findAllProduct();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao carregar produtos.", e);
        }
    }
    
    
    /**
     * Busca um produto pelo id
     * Usado principalmente quando temos apenas o id (ex.: carrinho mantém Map&lt;productId, qty&gt;).
     * Chama {@link RepositoryProduct#findByIdProduct(Integer)}.
     * Converte falhas de acesso em {@link DataAccessException}.
     * @param id id do produto
     * @return produto encontrado ou null se não existir
     * @throws DataAccessException se ocorrer falha ao acessar o banco
     */
    public Product findProductById(Integer id) throws DataAccessException {
        try {
            return repoProduct.findByIdProduct(id);
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao buscar produto do carrinho.", e);
        }
    }
}