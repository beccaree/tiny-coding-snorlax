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
	private int processorNumber; //TODO waste of memory
	
	public ProcessorTask(Vertex vertex, int startTime, int processorNumber) {
		this.vertex = vertex;
		this.startTime = startTime;
	}
	
	public int getStartTime() {
		return startTime;
	}
	
	public int getProcessorNumber() {
		return processorNumber;
	}
	
	public Vertex getVertex() {
		return vertex;
	}
	
	public ProcessorTask clone() {
		return new ProcessorTask(vertex, startTime, processorNumber);
	}
	
	public boolean isForVertex(Vertex v) {
		return this.vertex == v;
	}

	@Override
	public int hashCode() {
		return vertex.hashCode();
	}

	@Override
	public boolean equals(Object obj) {
		ProcessorTask p = (ProcessorTask) obj;
		return this.vertex.equals(p.getVertex());//TODO is the vertex sufficient
	}
	
	
	
}
