package model.entities;

public class Product {
	private Integer id;
	private String name;
	private String description;
	private Double price;
	private Size size;
	private String flavor;
	private FlavorLevel level;
	
	public Product() {}
	
	public Product(String name, String flavor, FlavorLevel level, Size size, Double price) {
		setName(name);
		setFlavor(flavor);
		setLevel(level);
		setSize(size);
		setPrice(price);
	}
	
	public Product(String name, String flavor, FlavorLevel level, Size size, Double price, String description) {
		setName(name);
		setDescription(description);
		setFlavor(flavor);
		setLevel(level);
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
	
	
	public String getFlavor() {
		return this.flavor;
	}

	public void setFlavor(String flavor) {
		this.flavor = flavor;
	}


	public FlavorLevel getLevel(){
		return this.level;
	}

	public void setLevel(FlavorLevel level){
		this.level = level;
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
