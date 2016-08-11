package scheduling_solution.astar;

import java.util.List;

import scheduling_solution.graph.Vertex;

/*
 * TODO
 * need to be able to get processor idle time for the load balance heuristic
 */
public class PartialSolution {

	Processor[] processors;
	
	public PartialSolution(int numProcessors, Vertex v, int processorNumber) {
		//Brand new solution with a single vertex
		
		//Array of processors
		processors = new Processor [numProcessors];
		
		for (int i=0; i<numProcessors;i++){
			processors[i]=new Processor();
		}
		processors[processorNumber].add(new ProcessorTask(v,0));
	}
	
	public PartialSolution(PartialSolution partialSolution, int numProcessors, Vertex v, int processor) {
		//TODO Need to create a new solution which is the old + the new vertex. may be difficult to clone certain objects
		//add the vertex to the new processor
		processors = new Processor [numProcessors];
		
		for (int i=0; i<numProcessors;i++){
			processors[i] = partialSolution.getProcessor(i).clone();
			
		}
		
	}
	
	public Processor getProcessor(int i) {
		return processors[i];
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

