package scheduling_solution.astar;

import java.util.List;

import scheduling_solution.graph.Vertex;

/*
 * TODO
 * need to be able to get processor idle time for the load balance heuristic
 */
public class PartialSolution {

	public PartialSolution(int numProcessors, Vertex v, int processor) {
		//Brand new solution with a single vertex
	}
	
	public PartialSolution(PartialSolution partialSolution, Vertex v, int processor) {
		//TODO Need to create a new solution which is the old + the new vertex. may be difficult to clone certain objects
		//add the vertex to the new processor
	}
	
	public int getLength(){
		return 0; //TODO return length of schedule/finish time of latest node
	}
	
	public List<Vertex> getAvavilableVertices() {
		return null;//TODO
	}
	
	@Override
	public int hashCode() {
		//TODO will need this for the hashSet. maybe do equals() too?
		return 0;
	}
	
	
}

