package scheduling_solution.astar;

import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

public class PartialSolution {
	

	private Processor[] processors;
	private int numProcessors;
	
	private HashSet<Vertex> allocatedVertices;
	private HashSet<Vertex> unallocatedVertices;
	//private int numAllocatedVertices;
	private int maxBottomLevel;
	
	public PartialSolution(Set<Vertex> vertices, int numProcessors, Vertex v, int processorNumber) {
		//Brand new solution with a single vertex
		this.numProcessors = numProcessors;
		
		this.unallocatedVertices = new HashSet<>();
		for (Vertex vertex : vertices) {
			unallocatedVertices.add(vertex);
		}
		
		//Array of processors
		processors = new Processor [numProcessors];
		
		for (int i=0; i<numProcessors;i++){
			processors[i]=new Processor();
		}
		processors[processorNumber].add(new ProcessorTask(v, 0, processorNumber));

		allocatedVertices = new HashSet<>();
		allocatedVertices.add(v);
	}
	
	public PartialSolution(PartialSolution partialSolution, Vertex v, int processorNumber, int startTime) {
		//add the vertex to the new processor
		processors = new Processor [numProcessors];
		
		for (int i=0; i<numProcessors;i++){
			processors[i] = partialSolution.getProcessor(i).clone();
		}
		
		processors[processorNumber].add(new ProcessorTask(v, startTime, processorNumber));
		
		//Update internal variables so we don't have to calculate them when getting them
//		numAllocatedVertices = partialSolution.getNumberAllocatedVertices() + 1;
		allocatedVertices = (HashSet<Vertex>) partialSolution.getAllocatedVertices().clone();
		allocatedVertices.add(v);
		
		//Need to cast twice as a Set doesnt have a clone() method
		unallocatedVertices = (HashSet<Vertex>) partialSolution.getUnallocatedVertices().clone();
		unallocatedVertices.remove(v);
		
		maxBottomLevel = Math.max(partialSolution.maxBottomLevel, v.getBottomLevel());
	}
	
	public Processor[] getProcessors() {
		return processors;
	}
	
	public Processor getProcessor(int i) {
		return processors[i];
	}
	
	public int getMaxBottomLevel() {
		return maxBottomLevel;
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
	
	//TODO candidate for optimisation
	public List<Vertex> getAvailableVertices() {
		
		return null;
		/*
		 * Given a partial solution, what nodes can we schedule next?
		 */
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
	
	public HashSet<Vertex> getAllocatedVertices() {
		return allocatedVertices;
	}
	
	public HashSet<Vertex> getUnallocatedVertices() {
		return unallocatedVertices;
	}
	
	@Override
	public int hashCode() {
		//This is cachable I think, as a partialSolution shouldn't change. would it help though?
		return Arrays.hashCode(processors);
	}
	
	@Override
	public boolean equals(Object obj) {
		return Arrays.equals(processors, ((PartialSolution) obj).getProcessors());
	}	
	
}

