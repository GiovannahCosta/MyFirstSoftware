package model.entities;

public enum Size {
	Mine (1, "8 a 10 pessoas", "1.3kg"),
	PP (2, "20 a 25 pessoas", "2.3kg"),
	P (3, "35 a 40 pessoas", "3.8kg"),
	M (4, "50 pessoas", "7kg"),
	G (5, "50 pessoas", "9kg");

	
	private final int id;
    private final String yield;
    private final String weight;
	
	Size(int id, String yield, String weight) {
		this.id = id;
        this.yield = yield;
        this.weight = weight;
		
	}
	
    public int getId() {
        return id;
    }

    public String getYield() {
        return yield;
    }
    
    public String getWeight() {
    	return weight;
    }
}
