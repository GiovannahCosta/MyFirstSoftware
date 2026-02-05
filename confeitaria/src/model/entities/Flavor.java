package model.entities;

public class Flavor {
    private Integer id;
    private String name;
    private FlavorLevel level;
    private String description;

    public Flavor(){}

    public Flavor(String name, FlavorLevel level, String description){
        setName(name);
        setLevel(level);
        setDescription(description);
    }

    public Integer getId(){
        return this.id;
    }

    public void setId(Integer id){
        this.id = id;
    }

    public String getName(){
        return this.name;
    }

    public void setName(String name){
        this.name = name;
    }

    public FlavorLevel getLevel(){
        return this.level;
    }

    public void setLevel(FlavorLevel level){
        this.level = level;
    }

    public void setDescription(String description){
        this.description = description;
    }

    public String getDescription(){
        return this.description;
    }


    @Override
    public String toString() {
        return "Flavor [getId()=" + getId() + ", getName()=" + getName() + ", getLevel()=" + getLevel()
				+ ", getDescription()=" + getDescription() + "]";
    }

    

    
}
