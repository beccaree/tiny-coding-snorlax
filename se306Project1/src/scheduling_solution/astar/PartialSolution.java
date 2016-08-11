package scheduling_solution.astar;

import java.util.List;

import scheduling_solution.graph.Vertex;

/*
 * TODO
 * need to be able to get processor idle time for the load balance heuristic
 */
public class PartialSolution {

	Processor[] processors;
	int numProcessors;
	
	public PartialSolution(int numProcessors, Vertex v, int processorNumber) {
		//Brand new solution with a single vertex
		this.numProcessors = numProcessors;
		//Array of processors
		processors = new Processor [numProcessors];
		
		for (int i=0; i<numProcessors;i++){
			processors[i]=new Processor();
		}
		processors[processorNumber].add(new ProcessorTask(v, 0, processorNumber));
	}
	
	public PartialSolution(PartialSolution partialSolution, Vertex v, int processorNumber, int startTime) {
		//TODO Need to create a new solution which is the old + the new vertex. may be difficult to clone certain objects
		//add the vertex to the new processor
		processors = new Processor [numProcessors];
		
		for (int i=0; i<numProcessors;i++){
			processors[i] = partialSolution.getProcessor(i).clone();
		}
		
		processors[processorNumber].add(new ProcessorTask(v, startTime, processorNumber));
		
	}
	
	public Processor getProcessor(int i) {
		return processors[i];
	}
	
	public int getTimeLength(){
		int maxTime = 0;
		for (int i = 0; i < numProcessors; i++) {
			int processorTime = processors[i].getFinishTime();
			if (processorTime > maxTime) {
				maxTime = processorTime;
			}
		}
		return maxTime;
	}
	
	public List<Vertex> getAvavilableVertices() {
		return null;//TODO
	}
	
	public ProcessorTask getTask(Vertex v){
		for (int i=0; i < numProcessors; i++){
			for (ProcessorTask p : processors[i].tasks()) {
				if (p.isForVertex(v)) {
					return p;
				}
			}
		}
		//Shouldn't occur
		return null;
	}
	
	@Override
	public int hashCode() {
		//TODO will need this for the hashSet. maybe do equals() too?
		return 0;
	}
	
	
}

