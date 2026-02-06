package model.entities;

import java.util.List;

/**
 * Área/região de entrega (bairro ou zona). Usada no endereço para cálculo de taxa (fee).
 */
public class Area {
	private Integer id;
    private String name;
    private double fee;

    public Area() {}

    public Area(String name, double fee) {
        setName(name);
        setFee(fee);
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getFee() {
        return fee;
    }

    public void setFee(double fee) {
        this.fee = fee;
    }


    @Override
    public String toString() {
        return this.name;
    }
}
