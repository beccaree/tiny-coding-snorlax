package scheduling_solution.astar;

import scheduling_solution.graph.Vertex;

/**
 * Object to 
 * @author Team 8
 *
 */
public class ProcessorTask {

	private Vertex vertex;
	private int startTime;
	
	public ProcessorTask(Vertex vertex, int startTime) {
		this.vertex = vertex;
		this.startTime = startTime;
	}
	
	public ProcessorTask clone() {
		return new ProcessorTask(vertex, startTime);
	}
	
}
