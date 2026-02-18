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

public class ControllerProductAdmin {

    private final RepositoryProduct repoProduct;
    private final RepositoryFlavor repoFlavor;
    private final RepositoryFlavorLevel repoLevel;
    private final RepositorySize repoSize;

    public ControllerProductAdmin() {
        this.repoProduct = new RepositoryProduct();
        this.repoFlavor = new RepositoryFlavor();
        this.repoLevel = new RepositoryFlavorLevel();
        this.repoSize = new RepositorySize();
    }

    public List<FlavorLevel> listFlavorLevels() throws DataAccessException {
        try {
            return repoLevel.findAllFlavorLevel();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao carregar níveis de sabor.", e);
        }
    }

    public List<Size> listSizes() throws DataAccessException {
        try {
            return repoSize.findAllSize();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao carregar tamanhos.", e);
        }
    }

    public List<Product> listProducts() throws DataAccessException {
        try {
            return repoProduct.findAllProduct();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao listar produtos.", e);
        }
    }

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