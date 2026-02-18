package model.entities;

import java.sql.Timestamp;

public class OrderSummary {
    private Integer id;
    private Timestamp datetime;
    private Double totalPrice;
    private String delivery;
    private String observations;

    public OrderSummary(Integer id, Timestamp datetime, Double totalPrice, String delivery, String observations) {
        this.id = id;
        this.datetime = datetime;
        this.totalPrice = totalPrice;
        this.delivery = delivery;
        this.observations = observations;
    }

    public Integer getId() { return id; }
    public Timestamp getDatetime() { return datetime; }
    public Double getTotalPrice() { return totalPrice; }
    public String getDelivery() { return delivery; }
    public String getObservations() { return observations; }
}