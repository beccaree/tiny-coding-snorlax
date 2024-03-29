package scheduling_solution.graph;

/**
 * Used to store information about a task
 */
public class Vertex {

	private String name;
	private int weight;
	private int bottomLevel;
	private int numbOfUse = 0;
	private final static int UPDATE_FREQUENCY = 10000;
	
	public Vertex(String name, int weight) {
		this.name = name;
		this.weight = weight;
	}
	
	//---Get Methods---
	
	/**
	 * Get name of Vertex
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
	
	/**
	 * Get the number of times this vertex is used in a valid solution
	 * @return int number of use
	 */
	public int getNumbOfUse() {
		return numbOfUse;
	}
	
	//---Set methods---
	
	/**
	 * Set new value to bottom level variable
	 * @param value from bottomLevel
	 */
	public void setBottomLevel(int value) {
		bottomLevel = value;
	}
	
	/**
	 * Set the nodes weight
	 * @param weight
	 */
	public void setWeight(int weight) {
		this.weight = weight;
	}
	
	public boolean incrementNumbUsed() {
		this.numbOfUse++;
		
		if (this.numbOfUse % UPDATE_FREQUENCY == 1) {
			return true;
		}
		return false;
	}
	
	@Override
	public String toString() {
		return name + "(" + weight + ")";
	}
	
	@Override
	public int hashCode() {
		return name.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		Vertex v = (Vertex) obj;
		return this.name.equals(v.getName());
	}
	

}
