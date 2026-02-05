package model.entities;

public class OrderItems {
	private Integer id;
	private Product product;
	private Integer quantity;
	private Double priceAtMoment;
	
	public OrderItems() {}
	
	public OrderItems(Product product, Integer quantity) {
		setProduct(product);
		setQuantity(quantity);
	}
	
	public OrderItems(Product product, Integer quantity, Double priceAtMoment) {
		setProduct(product);
		setQuantity(quantity);
		setPriceAtMoment(priceAtMoment != null ? priceAtMoment : (product != null && product.getBasePrice() != null ? product.getBasePrice() : 0.0));
	}

	public Integer getId() {
		return this.id;
	}


	public Product getProduct() {
		return this.product;
	}

	public void setProduct(Product product) {
		this.product = product;
	}

	public Integer getQuantity() {
		return this.quantity;
	}

	public void setQuantity(Integer quantity) {
		this.quantity = quantity;
	}

	public Double getPriceAtMoment() {
		return this.priceAtMoment;
	}

	public void setPriceAtMoment(Double priceAtMoment) {
		this.priceAtMoment = priceAtMoment;
	}
	
	public Double subtotal() {
		Integer qty = getQuantity();
		Double price = getPriceAtMoment();
		return (qty != null && price != null) ? qty * price : 0.0;
	}

	@Override
	public String toString() {
		return "OrderItems [getId()=" + getId() + ", getProduct()=" + getProduct() + ", getQuantity()=" + getQuantity()
				+ ", getPriceAtMoment()=" + getPriceAtMoment() + ", subtotal()=" + subtotal() + "]";
	}
	
	
	
	
	
}
