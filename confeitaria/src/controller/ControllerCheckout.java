package controller;

import exceptions.DataAccessException;
import exceptions.ValidationException;
import model.entities.Product;
import model.repositories.RepositoryOrder;
import model.repositories.RepositoryOrderItems;
import model.repositories.RepositoryProduct;

import java.sql.SQLException;
import java.sql.Timestamp;
import java.time.Instant;
import java.util.Map;

public class ControllerCheckout {

    private final RepositoryOrder repoOrder;
    private final RepositoryOrderItems repoOrderItems;
    private final RepositoryProduct repoProduct;

    public ControllerCheckout() {
        this.repoOrder = new RepositoryOrder();
        this.repoOrderItems = new RepositoryOrderItems();
        this.repoProduct = new RepositoryProduct();
    }

    public ControllerCheckout(RepositoryOrder repoOrder, RepositoryOrderItems repoOrderItems, RepositoryProduct repoProduct) {
        this.repoOrder = repoOrder;
        this.repoOrderItems = repoOrderItems;
        this.repoProduct = repoProduct;
    }

    public Integer confirmOrder(Integer idUser,
                                Double totalPrice,
                                String delivery,
                                String observations,
                                Map<Integer, Integer> cartItems)
            throws ValidationException, DataAccessException {

        if (idUser == null || idUser <= 0)
            throw new ValidationException("Usuário inválido. Faça login novamente.");

        if (totalPrice == null)
            throw new ValidationException("Total do pedido é obrigatório.");

        if (totalPrice < 0)
            throw new ValidationException("Total do pedido não pode ser negativo.");

        if (delivery == null || delivery.trim().isEmpty())
            throw new ValidationException("Forma de recebimento é obrigatória.");

        String del = delivery.trim();

        if (cartItems == null || cartItems.isEmpty())
            throw new ValidationException("Carrinho vazio. Adicione itens antes de finalizar.");

        try {
            Integer idOrder = repoOrder.createOrderAndReturnId(
                    idUser,
                    Timestamp.from(Instant.now()),
                    totalPrice,
                    del,
                    (observations != null && observations.trim().isEmpty()) ? null : observations
            );

            if (idOrder == null)
                throw new DataAccessException("Não foi possível criar o pedido.", null);

            for (Map.Entry<Integer, Integer> entry : cartItems.entrySet()) {
                Integer productId = entry.getKey();
                Integer qty = entry.getValue();

                if (productId == null) continue;
                if (qty == null || qty <= 0) continue;

                Product p = repoProduct.findByIdProduct(productId);
                if (p == null) continue;

                double unit = computeUnitPrice(p);

                boolean okItem = repoOrderItems.createOrderItem(idOrder, productId, qty, unit);
                if (!okItem)
                    throw new DataAccessException("Não foi possível salvar um item do pedido.", null);
            }

            return idOrder;

        } catch (SQLException e) {
            throw new DataAccessException("Erro ao confirmar pedido.", e);
        }
    }

    private static double computeUnitPrice(Product p) {
        double base = p.getBasePrice() != null ? p.getBasePrice() : 0.0;
        double sizePrice = (p.getSize() != null && p.getSize().getPrice() != null) ? p.getSize().getPrice() : 0.0;
        double levelPrice = (p.getFlavor() != null && p.getFlavor().getLevel() != null && p.getFlavor().getLevel().getPrice() != null)
                ? p.getFlavor().getLevel().getPrice() : 0.0;

        return base + sizePrice + levelPrice;
    }
}