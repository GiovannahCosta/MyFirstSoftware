package controller;

import exceptions.DataAccessException;
import model.entities.Product;
import model.repositories.RepositoryProduct;

import java.sql.SQLException;
import java.util.List;

public class ControllerShop {

    private final RepositoryProduct repoProduct;

    public ControllerShop() {
        this.repoProduct = new RepositoryProduct();
    }

    public ControllerShop(RepositoryProduct repoProduct) {
        this.repoProduct = repoProduct;
    }

    public List<Product> listAllProducts() throws DataAccessException {
        try {
            return repoProduct.findAllProduct();
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao carregar produtos.", e);
        }
    }

    public Product findProductById(Integer id) throws DataAccessException {
        try {
            return repoProduct.findByIdProduct(id);
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao buscar produto do carrinho.", e);
        }
    }
}