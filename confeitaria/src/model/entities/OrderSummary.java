package model.entities;

import java.sql.Timestamp;

/**
 * DTO (objeto de transferência de dados) que representa um resumo de pedido.
 *
 * Esta classe é usada principalmente no caso de uso "Meus Pedidos", quando a aplicação precisa
 * listar pedidos do usuário de forma rápida, sem carregar a entidade completa {@link Order}
 * e sem carregar a lista de itens.
 *
 * Normalmente é montada a partir de consultas com JOIN/relatório no banco de dados
 * (por exemplo, em {@code RepositoryMyOrders}) e enviada para o controller e a view
 * responsáveis por exibição.
 */
public class OrderSummary {

    /**
     * Identificador do pedido (chave primária).
     * Corresponde ao id da tabela de pedidos no banco de dados.
     */
    private Integer id;

    /**
     * Data e hora em que o pedido foi criado/registrado.
     * Usa {@link Timestamp} por ser o formato retornado com frequência em consultas JDBC.
     */
    private Timestamp datetime;

    /**
     * Valor total do pedido.
     * Pode representar o total já consolidado no banco (somatório dos itens, com regras aplicadas).
     */
    private Double totalPrice;

    /**
     * Tipo de entrega/retirada no formato de texto.
     *
     * Observação:
     * Em algumas partes do sistema o tipo pode ser representado por enum (ex.: DeliveryType),
     * mas este DTO usa {@link String} pois costuma refletir diretamente o valor retornado pela consulta.
     */
    private String delivery;

    /**
     * Observações do pedido (campo livre).
     * Pode ser null quando o usuário não informou observações.
     */
    private String observations;

    /**
     * Constrói um resumo de pedido com todos os campos.
     *
     * Funcionamento:
     * 1. Recebe os valores já prontos (normalmente vindos do banco via relatório/consulta).
     * 2. Atribui diretamente nos campos internos do DTO.
     *
     * @param id identificador do pedido
     * @param datetime data e hora do pedido
     * @param totalPrice total do pedido
     * @param delivery tipo de entrega/retirada em texto
     * @param observations observações do pedido (pode ser null)
     */
    public OrderSummary(Integer id, Timestamp datetime, Double totalPrice, String delivery, String observations) {
        this.id = id;
        this.datetime = datetime;
        this.totalPrice = totalPrice;
        this.delivery = delivery;
        this.observations = observations;
    }

    /**
     * Retorna o identificador do pedido.
     *
     * @return id do pedido
     */
    public Integer getId() {
        return id;
    }

    /**
     * Retorna a data e hora do pedido.
     *
     * @return data e hora do pedido
     */
    public Timestamp getDatetime() {
        return datetime;
    }

    /**
     * Retorna o valor total do pedido.
     *
     * @return total do pedido
     */
    public Double getTotalPrice() {
        return totalPrice;
    }

    /**
     * Retorna o tipo de entrega/retirada em formato de texto.
     *
     * @return tipo de entrega/retirada
     */
    public String getDelivery() {
        return delivery;
    }

    /**
     * Retorna as observações do pedido.
     *
     * @return observações do pedido (pode ser null)
     */
    public String getObservations() {
        return observations;
    }
}