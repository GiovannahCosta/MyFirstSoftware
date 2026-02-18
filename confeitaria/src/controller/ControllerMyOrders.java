package controller;

import exceptions.DataAccessException;
import exceptions.ValidationException;
import model.entities.OrderItemSummary;
import model.entities.OrderSummary;
import model.repositories.RepositoryMyOrders;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller responsável pela view de "Meus Pedidos"
 * Este controller centraliza a lógica de validação de ids e a consulta de pedidos e itens no banco de dados via {@link RepositoryMyOrders}.
 * Responsável por validar ids informados pela View, carregar lista de pedidos do usuário, carregar lista de itens de um pedido, converter {@link SQLException} em {@link DataAccessException}
 */

public class ControllerMyOrders {
	
	/**
     * Repositório especializado para consultas de pedidos e itens (JOINs/consultas de relatório).
     */
    private final RepositoryMyOrders repo;
    
    /**
     * Construtor padrão.
     * Instancia o repositório concreto.
     */
    public ControllerMyOrders() {
        this.repo = new RepositoryMyOrders();
    }
    
    /**
     * Construtor com injeção de dependência.
     * @param repo repositório a ser utilizado (não deve ser null)
     */
    public ControllerMyOrders(RepositoryMyOrders repo) {
        this.repo = repo;
    }

    /**
     * Lista os pedidos do usuário logado
     * Valida {@code idUser}
     * Chama {@link RepositoryMyOrders#findOrdersByUser(Integer)}
     * Converte {@link SQLException} em {@link DataAccessException}
     * @param idUser id do usuário logado
     * @return lista de pedidos (pode ser vazia)
     * @throws ValidationException se {@code idUser} for inválido
     * @throws DataAccessException se ocorrer falha no acesso ao banco
     */
    public List<OrderSummary> listOrdersByUser(Integer idUser) throws ValidationException, DataAccessException {
        if (idUser == null || idUser <= 0)
            throw new ValidationException("Usuário inválido. Faça login novamente.");

        try {
            return repo.findOrdersByUser(idUser);
        } catch (SQLException e) {
            throw new DataAccessException("Erro ao carregar pedidos.", e);
        }
    }
    
    /**
     * Lista de itens de um pedido específico.
     * Valida {@code idOrder}
     * Chama {@link RepositoryMyOrders.findItemsByOrder(Integer)}.
     * Converte {@link SQLException} em {@link DataAccessException}.
     * @param idOrder id do pedido selecionado
     * @return lista de itens (pode ser vazia)
     * @throws ValidationException se {@code idOrder} for inválido
     * @throws DataAccessException se ocorrer falha no acesso ao banco
     */
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