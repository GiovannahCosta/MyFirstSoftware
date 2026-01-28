package model.entities;

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
		return this.id;
	}
	
	public String getName() {
		return this.name;
	}
	
	public void setName(String name) {
		this.name = name;
	}
	
	public double getFee() {
		return this.fee;
	}
	
	public void setFee(double fee) {
		this.fee = fee;
	}

	@Override
	public String toString() {
		return "Area [getId()=" + getId() + ", getName()=" + getName() + ", getFee()=" + getFee() + "]";
	}
	
}
