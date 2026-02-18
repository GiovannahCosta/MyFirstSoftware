package controller;

import app.CartSession;
import exceptions.DataAccessException;
import model.entities.Product;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class ControllerCartView {

    private final ControllerShop controllerShop;

    public ControllerCartView() {
        this.controllerShop = new ControllerShop();
    }

    public ControllerCartView(ControllerShop controllerShop) {
        this.controllerShop = controllerShop;
    }

    public CartViewData loadCartData() throws DataAccessException {
        List<CartRow> rows = new ArrayList<>();
        double subtotal = 0.0;

        for (Map.Entry<Integer, Integer> entry : CartSession.getItems().entrySet()) {
            Integer productId = entry.getKey();
            Integer qty = entry.getValue();

            if (productId == null || qty == null || qty <= 0) continue;

            Product p = controllerShop.findProductById(productId); // pode retornar null
            if (p == null) continue;

            double unit = computeUnitPrice(p);
            double total = unit * qty;
            subtotal += total;

            rows.add(new CartRow(productId, p.getName(), qty, unit, total));
        }

        return new CartViewData(rows, subtotal);
    }

    public void removeByProductId(Integer productId) {
        if (productId == null) return;
        CartSession.remove(productId);
    }

    private static double computeUnitPrice(Product p) {
        double base = p.getBasePrice() != null ? p.getBasePrice() : 0.0;
        double sizePrice = (p.getSize() != null && p.getSize().getPrice() != null) ? p.getSize().getPrice() : 0.0;
        double levelPrice = (p.getFlavor() != null && p.getFlavor().getLevel() != null && p.getFlavor().getLevel().getPrice() != null)
                ? p.getFlavor().getLevel().getPrice() : 0.0;
        return base + sizePrice + levelPrice;
    }

   
    public static class CartRow {
        private final Integer productId;
        private final String productName;
        private final Integer qty;
        private final Double unit;
        private final Double total;

        public CartRow(Integer productId, String productName, Integer qty, Double unit, Double total) {
            this.productId = productId;
            this.productName = productName;
            this.qty = qty;
            this.unit = unit;
            this.total = total;
        }

        public Integer getProductId() { return productId; }
        public String getProductName() { return productName; }
        public Integer getQty() { return qty; }
        public Double getUnit() { return unit; }
        public Double getTotal() { return total; }
    }

    public static class CartViewData {
        private final List<CartRow> rows;
        private final Double subtotal;

        public CartViewData(List<CartRow> rows, Double subtotal) {
            this.rows = rows;
            this.subtotal = subtotal;
        }

        public List<CartRow> getRows() { return rows; }
        public Double getSubtotal() { return subtotal; }
    }
}