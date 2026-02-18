package model.entities;

public class OrderItemSummary {
    private String productName;
    private Integer quantity;
    private Double priceAtMoment;

    public OrderItemSummary(String productName, Integer quantity, Double priceAtMoment) {
        this.productName = productName;
        this.quantity = quantity;
        this.priceAtMoment = priceAtMoment;
    }

    public String getProductName() { return productName; }
    public Integer getQuantity() { return quantity; }
    public Double getPriceAtMoment() { return priceAtMoment; }

    public Double getTotal() {
        double unit = priceAtMoment != null ? priceAtMoment : 0.0;
        int qty = quantity != null ? quantity : 0;
        return unit * qty;
    }
}