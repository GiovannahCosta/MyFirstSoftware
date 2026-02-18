package controller;

import exceptions.DataAccessException;
import exceptions.ValidationException;
import model.entities.Flavor;
import model.entities.FlavorLevel;
import model.entities.Product;
import model.entities.Size;
import model.factories.FlavorFactory;
import model.factories.ProductFactory;
import model.repositories.RepositoryFlavor;
import model.repositories.RepositoryFlavorLevel;
import model.repositories.RepositoryProduct;
import model.repositories.RepositorySize;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller responsável pela view Cadastro de Produtos.
 * Centraliza as operações administrativas relacionadas ao catálogo
 * lista níveis de sabor (FlavorLevel)
 * lista tamanhos (Size)
 * lista produtos
 * criar produto criando um sabor novo associado ao nível selecionado
 * exclui produto
 * Este controller é chamado por {@code ViewProducts} (tela admin), ajudando a evitar SQL na UI.
 */
public class ControllerProductAdmin {
	
	/**
     * Repositório de produtos.
     * Usado para listar, inserir e excluir {@link Product}.
     */
    private final RepositoryProduct repoProduct;
    
    /**
     * Repositório de sabores.
     * Usado para inserir {@link Flavor} antes de inserir um {@link Product}.
     */
    private final RepositoryFlavor repoFlavor;
    
    /**
     * Repositório de níveis de sabor (FlavorLevel).
     * Usado para carregar o combo de níveis na tela de admin.
     */
    private final RepositoryFlavorLevel repoLevel;
    
    /**
     * Repositório de tamanhos (Size).
     * Usado para carregar o combo de tamanhos na tela de admin.
     */
    private final RepositorySize repoSize;
    
    /**
     * Construtor padrão.
     * Instancia os repositórios concretos.
     */
    public ControllerProductAdmin() {
        this.repoProduct = new RepositoryProduct();
        this.repoFlavor = new RepositoryFlavor();
        this.repoLevel = new RepositoryFlavorLevel();
        this.repoSize = new RepositorySize();
    }
    
    /**
     * Lista os níveis de sabor disponíveis.
     * Converte {@link SQLException} em {@link DataAccessException}
     * @return lista de níveis (pode ser vazia)
     * @throws DataAccessException se houver falha ao acessar o banco
     */
    public List<FlavorLevel> listFlavorLevels() throws DataAccessException {
        try {
            return repoLevel.findAllFlavorLevel();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao carregar níveis de sabor.", e);
        }
    }
    
    
    /**
     * Lista os tamanhos disponíveis.
     * Chama {@link RepositorySize#findAllSize()}
     * Converte {@link SQLException} em {@link DataAccessException}
     * @return lista de tamanhos (pode ser vazia)
     * @throws DataAccessException se houver falha ao acessar o banco
     */
    public List<Size> listSizes() throws DataAccessException {
        try {
            return repoSize.findAllSize();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao carregar tamanhos.", e);
        }
    }
    
    
    /**
     * Lista todos os produtos cadastrados
     * @return lista de produtos
     * @throws DataAccessException se houver falha ao acessar o banco
     */
    public List<Product> listProducts() throws DataAccessException {
        try {
            return repoProduct.findAllProduct();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao listar produtos.", e);
        }
    }
    
    /**
     * Cria um produto e, junto, cria um novo sabor ({@link Flavor}) associado ao nível selecionado.
     * Valida campos mínimos (ex.: {@code basePrice} obrigatório)
     * Cria {@link Flavor} via {@link FlavorFactory}
     * Persiste o sabor via {@link RepositoryFlavor#createFlavorAndReturnId(Flavor)}
     * Cria {@link Product} via {@link ProductFactory} usando o sabor recém-criado
     * Persiste o produto via {@link RepositoryProduct#createProduct(Product)}.
     *
     * @param productName nome do produto
     * @param basePrice preço base (obrigatório)
     * @param flavorName nome do sabor a criar
     * @param level nível do sabor selecionado (usado para o sabor)
     * @param size tamanho selecionado
     * @param description descrição opcional
     * @throws ValidationException se dados obrigatórios estiverem inválidos
     * @throws DataAccessException se ocorrer falha ao salvar no banco
     */
    public void createProductWithNewFlavor(String productName,
                                           Double basePrice,
                                           String flavorName,
                                           FlavorLevel level,
                                           Size size,
                                           String description)
            throws ValidationException, DataAccessException {

        try {
            if (basePrice == null) throw new ValidationException("Preço base é obrigatório.");

            
            Flavor newFlavor = FlavorFactory.create(null, flavorName, level, null);
            Integer newFlavorId = repoFlavor.createFlavorAndReturnId(newFlavor);
            if (newFlavorId == null) {
                throw new DataAccessException("Não foi possível criar o sabor.", null);
            }
            newFlavor.setId(newFlavorId);

            
            Product product = ProductFactory.create(null, productName, newFlavor, size, basePrice, description);
            boolean ok = repoProduct.createProduct(product);
            if (!ok) {
                throw new DataAccessException("Não foi possível salvar o produto.", null);
            }

        } catch (SQLException e) {
            throw new DataAccessException("Erro ao salvar produto no banco.", e);
        }
    }
    
    /**
     * Exclui um produto.
     * Valida se o produto e o id existem
     * Chama {@link RepositoryProduct.deleteProduct(Product)}
     * Se o repositório retornar false, lança {@link DataAccessException}.
     * @param product produto selecionado na tabela da tela admin
     * @throws ValidationException se produto/id forem inválidos
     * @throws DataAccessException se ocorrer falha ao excluir no banco
     */
    public void deleteProduct(Product product) throws ValidationException, DataAccessException {
        if (product == null || product.getId() == null) {
            throw new ValidationException("Selecione um produto válido para excluir.");
        }

        try {
            boolean ok = repoProduct.deleteProduct(product);
            if (!ok) {
                throw new DataAccessException("Não foi possível excluir o produto.", null);
            }
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao excluir produto no banco.", e);
        }
    }
}