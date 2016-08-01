package project1;

public class Vertex {

	//variables
	private String name;
	private int weight;
	private int bottomLevel;
	
	//Get methods
	/**
	 * Get Name of Vertex
	 * @return String name
	 */
	public String getName(){
		return name;
	}
	
	/**
	 * Get weight of vertex
	 * @return int weight
	 */
	public int getWeight() {
		return weight;
	}
	
	/**
	 * Get bottom level (critical path from one node)
	 * @return int critical path from one node
	 */
	public int getBottomLevel() {
		return bottomLevel;
	}
	
	//Set methods
	/**
	 * Set new value to bottom level variable
	 * @param value from bottomLevel
	 */
	public void SetBottomLevel(int value) {
		bottomLevel = value;
	}
	
}
