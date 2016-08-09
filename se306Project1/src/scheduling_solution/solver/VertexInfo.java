package scheduling_solution.solver;

/**
 * This class stores the solution (start time and processor) for a vertex.
 * @author Team 8
 */

public class VertexInfo {
	
	private int startTime;
	private int processor;
	
	public VertexInfo(int startTime, int processor) {
		this.startTime = startTime;
		this.processor = processor;
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	/* 
	 * Formats vertex solution string
	 * (non-Javadoc)
	 * @see java.lang.Object#toString()
	 */
	public String toString() {
		return ", Start=" + startTime + ", Processor=" + processor;
	}
}
