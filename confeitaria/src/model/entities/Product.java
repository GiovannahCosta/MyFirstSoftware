package model.entities;

public class Product {
	private Integer id;
	private String name;
	private String description;
	private Double price;
	private Size size;
	private Flavor flavor;
	
	public Product() {}
	
	public Product(String name, Flavor flavor, Size size, Double price) {
		setName(name);
		setFlavor(flavor);
		setSize(size);
		setPrice(price);
	}
	
	public Product(String name, Flavor flavor, Size size, Double price, String description) {
		setName(name);
		setDescription(description);
		setFlavor(flavor);
		setSize(size);
		setPrice(price);
	}
	
	public void setId(Integer id) {
		this.id = id;
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
	
	public String getDescription() {
		return this.description;
	}
	
	public void setDescription(String description) {
		this.description = description;
	}
	
	
	public Flavor getFlavor() {
		return this.flavor;
	}

	public void setFlavor(Flavor flavor) {
		this.flavor = flavor;
	}

	public Size getSize() {
		return this.size;
	}
	
	public void setSize(Size size) {
		this.size = size;
	}

	public Double getPrice() {
		return this.price;
	}

	public void setPrice(Double price) {
		this.price = price;
	}

	@Override
	public String toString() {
		return "Product [getId()=" + getId() + ", getName()=" + getName() + ", getDescription()=" + getDescription()
				+ ", getFlavor()=" + getFlavor() + ", getSize()=" + getSize()
				+ ", getPrice()=" + getPrice() + "]";
	}	
	
	
	
}
