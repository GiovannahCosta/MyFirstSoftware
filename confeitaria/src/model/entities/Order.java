package model.entities;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Order {
	private Integer id;
	private Integer idUser;
	private LocalDateTime dateTime;
	private Double totalPrice;
	private String observations;
	private DeliveryType delivery;
	private List<OrderItems> orderItems;
	
	public Order() {}
	
	public Order(Integer idUser, LocalDateTime dateTime, Double totalPrice, DeliveryType delivery, String observations) {
		setIdUser(idUser);
		setDateTime(dateTime);
		setTotalPrice(totalPrice);
		setObservations(observations);
		setDelivery(delivery);
		this.orderItems = new ArrayList<>();
	}

	public Integer getId() {
		return id;
	}


	public LocalDateTime getDateTime() {
		return dateTime;
	}

	public void setDateTime(LocalDateTime dateTime) {
		this.dateTime = dateTime;
	}

	public Double getTotalPrice() {
		return totalPrice;
	}

	public void setTotalPrice(Double totalPrice) {
		this.totalPrice = totalPrice;
	}

	public void setId(Integer id) {
		this.id = id;
	}

	public void setIdUser(Integer idUser) {
		this.idUser = idUser;
	}

	public Integer getIdUser() {
		return idUser;
	}

	public void calculateTotalPrice() {
		double sum = 0.0;
		if (orderItems != null) {
			for (OrderItems o : orderItems) {
				sum += o.getPriceAtMoment() != null ? o.getPriceAtMoment() * o.getQuantity() : 0.0;
			}
		}
		this.totalPrice = sum;
	}	

	public String getObservations() {
		return observations;
	}

	public void setObservations(String observations) {
		this.observations = observations;
	}

	public DeliveryType getDelivery() {
		return delivery;
	}

	public void setDelivery(DeliveryType delivery) {
		this.delivery = delivery;
	}
	
	public List<OrderItems> getOrderItems(){
		return this.orderItems;
	}
	

	public void setOrderItems(List<OrderItems> orderItems) {
        if (orderItems == null) {
            this.orderItems = new ArrayList<>();
        } else {
            this.orderItems = orderItems;
        }
    }
	
	public void addItem(Product product, int quantity) {
		this.orderItems.add(new OrderItems(product, quantity));
	}
	
	public void removeAllSameItems(Product product) {
		for (int i = this.orderItems.size() - 1; i >= 0; i--) {
	        OrderItems item = this.orderItems.get(i);
	        if (item.getProduct().getId() == product.getId()) {
	            this.orderItems.remove(i);
	        }
	    }
		
	}
	
	public void removeOneItem(Product product, int quantityToSubtract) {
		for (int i = this.orderItems.size() - 1; i >= 0; i--) {
	        OrderItems item = this.orderItems.get(i);
	        
	        if (item.getProduct().getId() == product.getId()) {
	            int newQuantity = item.getQuantity() - quantityToSubtract;

	            if (newQuantity > 0) {
	                item.setQuantity(newQuantity);
	            } else {
	                this.orderItems.remove(i);
	            }
	            
	            break; 
	        }
	    }
	}

	@Override
	public String toString() {
		return "Order [id=" + id + ", dateTime=" + dateTime + ", orderItems=" + getOrderItems() + ", totalPrice=" + totalPrice
				+ ", observations=" + observations + ", delivery=" + delivery + "]";
	}
	
	
	
	
	
	
}
