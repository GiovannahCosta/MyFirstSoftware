package model.entities;

public class Size {
    private Integer id;
    private String name;
    private String yield;
    private String weight;
    private Double price;

    public Size(){}
	
	public Size(Integer id, String name, String yield, String weight, Double price) {
		setId(id);
        setName(name);
        setYield(yield);
        setWeight(weight);
        setPrice(price);
	}
	

    public void setId(Integer id){
        this.id = id;
    }


    public Integer getId() {
        return this.id;
    }


    public void setName(String name){
        this.name = name;
    }


    public String getName(){
        return this.name;
    }


    public void setYield(String yield){
        this.yield = yield;
    }


    public String getYield() {
        return this.yield;
    }

    public void setWeight(String weight){
        this.weight = weight;
    }
    
    public String getWeight() {
    	return this.weight;
    }

    public void setPrice(Double price){
        this.price = price;
    }

    public Double getPrice(){
        return this.price;
    }


    @Override
    public String toString() {
        return "Size [getId()=" + getId() + ", getName()=" + getName() + ", getYield()=" + getYield() + ", getWeight()="
                + getWeight() + ", getPrice()=" + getPrice() + "]";
    }

    

}
