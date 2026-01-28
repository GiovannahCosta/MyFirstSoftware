package model.entities;

public class Flavor {
	private Integer id;
	private String name;
	private String description;
	private FlavorLevel level;
	
	
	public Flavor() {}
	
	public Flavor(String name, String description, FlavorLevel level) {
		setName(name);
		setDescription(description);
		setLevel(level);
	}

	public Integer getId() {
		return this.id;
	}
	
	public void setId(Integer id) {
		this.id = id;
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

	public FlavorLevel getLevel() {
		return this.level;
	}

	public void setLevel(FlavorLevel level) {
		this.level = level;
	}

	@Override
	public String toString() {
		return "Flavor [getId()=" + getId() + ", getName()=" + getName() + ", getDescription()=" + getDescription()
				+ "]";
	}
	
	
	
}
