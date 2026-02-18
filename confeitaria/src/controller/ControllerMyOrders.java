package controller;

import exceptions.DataAccessException;
import exceptions.ValidationException;
import model.entities.OrderItemSummary;
import model.entities.OrderSummary;
import model.repositories.RepositoryMyOrders;

import java.sql.SQLException;
import java.util.List;

public class ControllerMyOrders {

    private final RepositoryMyOrders repo;

    public ControllerMyOrders() {
        this.repo = new RepositoryMyOrders();
    }

    public ControllerMyOrders(RepositoryMyOrders repo) {
        this.repo = repo;
    }

    public List<OrderSummary> listOrdersByUser(Integer idUser) throws ValidationException, DataAccessException {
        if (idUser == null || idUser <= 0)
            throw new ValidationException("Usuário inválido. Faça login novamente.");

        try {
            return repo.findOrdersByUser(idUser);
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao carregar pedidos.", e);
        }
    }

    public List<OrderItemSummary> listItems(Integer idOrder) throws ValidationException, DataAccessException {
        if (idOrder == null || idOrder <= 0)
            throw new ValidationException("Pedido inválido.");

        try {
            return repo.findItemsByOrder(idOrder);
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao carregar itens do pedido.", e);
        }
    }
}