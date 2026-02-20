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
 * Controller responsável pela View de cadastro de produtos (Admin).
 * Centraliza as operações administrativas relacionadas ao catálogo:
 * listar níveis de sabor, listar tamanhos, listar produtos, criar produto com novo sabor e excluir produto.
 * Este controller é chamado pela ViewProducts, evitando que a UI faça consultas SQL diretamente.
 */
public class ControllerProductAdmin {

	/**
     * Repositório de produtos.
     * Usado para listar, inserir e excluir Product.
     */
    private final RepositoryProduct repoProduct;

    /**
     * Repositório de sabores.
     * Usado para inserir Flavor antes de inserir um Product.
     */
    private final RepositoryFlavor repoFlavor;

    /**
     * Repositório de níveis de sabor.
     * Usado para carregar os níveis no combo da tela admin.
     */
    private final RepositoryFlavorLevel repoLevel;

    /**
     * Repositório de tamanhos.
     * Usado para carregar os tamanhos no combo da tela admin.
     */
    private final RepositorySize repoSize;

    /**
     * Construtor padrão.
     * Instancia os repositórios concretos usados pelo controller.
     */
    public ControllerProductAdmin() {
        this.repoProduct = new RepositoryProduct();
        this.repoFlavor = new RepositoryFlavor();
        this.repoLevel = new RepositoryFlavorLevel();
        this.repoSize = new RepositorySize();
    }

    /**
     * Lista os níveis de sabor disponíveis.
     *
     * Funcionamento:
     * 1. Consulta o banco via repoLevel.
     * 2. Converte SQLException em DataAccessException.
     *
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
     *
     * Funcionamento:
     * 1. Chama {@link RepositorySize#findAllSize()}.
     * 2. Converte SQLException em DataAccessException.
     *
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
     * Lista todos os produtos cadastrados.
     *
     * Funcionamento:
     * 1. Busca no repositório de produtos.
     * 2. Converte SQLException em DataAccessException.
     *
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
     * Cria um produto e, junto, cria um novo sabor associado ao nível selecionado.
     *
     * Funcionamento:
     * 1. Valida o preço base.
     * 2. Cria um novo Flavor via {@link FlavorFactory}.
     * 3. Persiste o sabor via {@link RepositoryFlavor#createFlavorAndReturnId(Flavor)} e obtém o id gerado.
     * 4. Cria um Product via {@link ProductFactory} usando o Flavor recém-criado.
     * 5. Persiste o produto via {@link RepositoryProduct#createProduct(Product)}.
     * 6. Converte SQLException em DataAccessException.
     *
     * @param productName nome do produto
     * @param basePrice preço base (obrigatório)
     * @param flavorName nome do sabor a criar
     * @param level nível do sabor selecionado
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
     *
     * Funcionamento:
     * 1. Valida se product e product.getId() existem.
     * 2. Chama {@link RepositoryProduct#deleteProduct(Product)}.
     * 3. Se o repositório retornar false, lança DataAccessException.
     * 4. Converte SQLException em DataAccessException.
     *
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