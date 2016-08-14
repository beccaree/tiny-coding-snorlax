package scheduling_solution.astar;

import java.util.Arrays;
import java.util.HashSet;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

public class PartialSolution {
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;

	private Processor[] processors;
	private byte numProcessors;
	
	private HashSet<Vertex> allocatedVertices;
	private HashSet<Vertex> unallocatedVertices;
//	private HashSet<Vertex> availableVertices;
	//private int numAllocatedVertices;

	/**
	 * Creates an new PartialSolution with an array for processors and their tasks
	 * Adds the initial vertex 
	 * @param graph				Digraph of all vertex and edges
	 * @param numProcessors		Number of processors that will be used
	 * @param v					Vertex to add to selected processor
	 * @param processorNumber	Processor to allocate vertex to
	 */
	public PartialSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, Vertex v, int processorNumber) {
		//Brand new solution with a single vertex
		this.graph = graph;
		this.numProcessors = numProcessors;
		
		this.unallocatedVertices = new HashSet<>();
		for (Vertex vertex : graph.vertexSet()) {
			unallocatedVertices.add(vertex);
		}
		
		//Array of processors
		processors = new Processor [numProcessors];
		
		//intialise each processor object in array
		for (int i=0; i<numProcessors;i++){
			processors[i]=new Processor();
		}
		
		//add initial vertex into selected processor
		processors[processorNumber].add(new ProcessorTask(v, 0, processorNumber));

		allocatedVertices = new HashSet<>();
		allocatedVertices.add(v);
		
		unallocatedVertices.remove(v);
	}
	

	/**
	 * Takes an existing partial solution and adds in a new task into a processor
	 * @param graph				Digraph of all vertex and edges
	 * @param partialSolution	Existing partial solution to be added to
	 * @param v					Vertex to add into solution
	 * @param processorNumber	Processor to add vertex to
	 * @param startTime			Start time of vertex that is to be added
	 */
	public PartialSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph, PartialSolution partialSolution, Vertex v, int processorNumber, int startTime) {
		this.graph = graph;
		this.numProcessors = partialSolution.numProcessors;//TODO getter
		
		//add the vertex to the new processor
		processors = new Processor [numProcessors];
		
		//clones all the existing processors and their tasks
		for (int i=0; i<numProcessors;i++){
			processors[i] = partialSolution.getProcessor(i).clone();
		}
		
		//adds in the new task into the given processor
		processors[processorNumber].add(new ProcessorTask(v, startTime, processorNumber));
		
		//Update internal variables so we don't have to calculate them when getting them
//		numAllocatedVertices = partialSolution.getNumberAllocatedVertices() + 1;
		allocatedVertices = (HashSet<Vertex>) partialSolution.getAllocatedVertices().clone();
		allocatedVertices.add(v);
		
		//Need to cast twice as a Set doesnt have a clone() method
		unallocatedVertices = (HashSet<Vertex>) partialSolution.getUnallocatedVertices().clone();
		unallocatedVertices.remove(v);
	}
	
	public Processor[] getProcessors() {
		return processors;
	}
	
	public Processor getProcessor(int i) {
		return processors[i];
	}
	
	//TODO optimisation. The bottom level heuristic should be calculated incrementally
	public int getMinimumTime() {
		int maxTimeToFinish = 0, totalIdleTime = 0;
		for (int i = 0; i < numProcessors; i++) {
//			totalIdleTime += processors[i].getIdleTime();
			for (ProcessorTask p : processors[i].tasks()) {
				int timeToFinish = p.getStartTime() + p.getVertex().getBottomLevel();
				if (timeToFinish > maxTimeToFinish) {
					maxTimeToFinish = timeToFinish;
				}
			}
		}
		return maxTimeToFinish;
		//Perfect load balance slows Nodes_11 down by ~40%
//		return Math.max(maxTimeToFinish, (AStar.getSequentialTime() + totalIdleTime) / numProcessors);
	}
	
	
	/**
	 * Returns that maximum total run time of all processors
	 * @return
	 */
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
	
	//TODO candidate for optimisation. Maybe its better to maintain this between PartialSolutions, and just calculate new ones
	//based on the vertex that is being added
	public HashSet<Vertex> getAvailableVertices() {
		HashSet<Vertex> availableVertices = new HashSet<>();
		
		outerloop:
		for (Vertex v : unallocatedVertices) {
			for (DefaultWeightedEdge e : graph.incomingEdgesOf(v)) {
				if (unallocatedVertices.contains(graph.getEdgeSource(e))) {//If any parent is unallocated
					continue outerloop;
				}
			}
			availableVertices.add(v); // Only make it here if we don't find an unallocated parent
			
		}
		return availableVertices;
	}
	
	/**
	 * Gets the ProcessorTask storing the given vertex
	 * @param v	Vertex 
	 * @return
	 */
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
	
	/**
	 * Used for debugging a solution
	 */
	public void printDetails() {
		System.out.println(allocatedVertices.size() + " allocated vertices, " + unallocatedVertices.size() + " unallocated vertices.");
		for (int i = 0; i < numProcessors; i++) {
			System.out.print("Processor " + i + " finishes at " + processors[i].getFinishTime());
			for (ProcessorTask p : processors[i].tasks()) {
				System.out.print(" || " + p.toString());
			}
			System.out.println();
		}
		
	}
	
}

