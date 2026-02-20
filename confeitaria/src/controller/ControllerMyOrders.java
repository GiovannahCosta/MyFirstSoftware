package controller;

import exceptions.DataAccessException;
import exceptions.ValidationException;
import model.entities.OrderItemSummary;
import model.entities.OrderSummary;
import model.repositories.RepositoryMyOrders;

import java.sql.SQLException;
import java.util.List;

/**
 * Controller responsável pela View de "Meus Pedidos".
 * Centraliza a validação de ids informados pela View e a consulta de pedidos e itens no banco de dados via {@link RepositoryMyOrders}.
 * Converte {@link SQLException} em {@link DataAccessException} para padronizar o erro para as camadas superiores.
 */
public class ControllerMyOrders {

	/**
     * Repositório especializado para consultas de pedidos e itens.
     * É responsável por executar consultas de relatório e joins, retornando resumos de pedidos e itens.
     */
    private final RepositoryMyOrders repo;

    /**
     * Construtor padrão.
     * Instancia o repositório concreto RepositoryMyOrders.
     */
    public ControllerMyOrders() {
        this.repo = new RepositoryMyOrders();
    }

    /**
     * Construtor com injeção de dependência.
     * Permite fornecer um repositório já configurado (por exemplo, para testes).
     *
     * @param repo repositório a ser utilizado (não deve ser null)
     */
    public ControllerMyOrders(RepositoryMyOrders repo) {
        this.repo = repo;
    }

    /**
     * Lista os pedidos de um usuário.
     *
     * Funcionamento:
     * 1. Valida o idUser.
     * 2. Chama {@link RepositoryMyOrders#findOrdersByUser(Integer)} para carregar os pedidos.
     * 3. Converte {@link SQLException} em {@link DataAccessException}.
     *
     * @param idUser id do usuário logado
     * @return lista de pedidos (pode ser vazia)
     * @throws ValidationException se idUser for inválido
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
     * Lista os itens de um pedido específico.
     *
     * Funcionamento:
     * 1. Valida o idOrder.
     * 2. Chama {@link RepositoryMyOrders#findItemsByOrder(Integer)} para carregar os itens do pedido.
     * 3. Converte {@link SQLException} em {@link DataAccessException}.
     *
     * @param idOrder id do pedido selecionado
     * @return lista de itens (pode ser vazia)
     * @throws ValidationException se idOrder for inválido
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