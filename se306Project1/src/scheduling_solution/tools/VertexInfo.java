package scheduling_solution.tools;

/**
 * This class stores the solution (start time and processor) for a vertex.
 * @author sabflik
 */
public class VertexInfo {
	
	private int startTime;
	private int processor;
	
	public VertexInfo(int startTime, int processors) {
		this.startTime = startTime;
		this.processor = processors;
	}
	
	/* (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ", Start="+startTime+", Processor="+processor;
	}
}
