package scheduling_solution.astar;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

@SuppressWarnings("unchecked")
public class PartialSolution {
	protected GraphInterface<Vertex, DefaultWeightedEdge> graph;

	protected byte numProcessors;
	
	protected int totalIdleTime;
	protected int[] finishTimes;
	
	protected Integer hashcode = null;
	
	protected HashMap<Vertex, AllocationInfo> allocatedVertices;
	protected HashSet<Vertex> availableVertices;
	protected HashSet<Vertex> unallocatedVertices;
	
	protected int minimumFinishTime = 0;
	
	/**
	 * Creates an new PartialSolution with an array for processors and their tasks
	 * Adds the initial vertex 
	 * @param graph				Digraph of all vertex and edges
	 * @param numProcessors		Number of processors that will be used
	 * @param v					Vertex to add to selected processor
	 * @param processorNumber	Processor to allocate vertex to
	 */
	public PartialSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, Vertex v, byte processorNumber) {
		//Brand new solution with a single vertex
		this.graph = graph;
		this.numProcessors = numProcessors;
		
		totalIdleTime = 0;
		finishTimes = new int[numProcessors];
		finishTimes[processorNumber] = v.getWeight();
		
		allocatedVertices = new HashMap<>();
		allocatedVertices.put(v, new AllocationInfo(processorNumber, 0));
		
		unallocatedVertices = new HashSet<>();
		unallocatedVertices.addAll(graph.vertexSet());
		unallocatedVertices.remove(v);
		
		getStartingVertices();
		availableVertices.remove(v);
		updateAvailableVertices(v);
		
		minimumFinishTime = v.getBottomLevel();
	}
	

	/**
	 * Takes an existing partial solution and adds in a new task into a processor
	 * @param graph				Digraph of all vertex and edges
	 * @param partialSolution	Existing partial solution to be added to
	 * @param v					Vertex to add into solution
	 * @param processorNumber	Processor to add vertex to
	 * @param startTime			Start time of vertex that is to be added
	 */
	public PartialSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph, PartialSolution partialSolution, Vertex v, byte processorNumber) {
		this.graph = graph;
		this.numProcessors = partialSolution.numProcessors;//TODO getter

		totalIdleTime = partialSolution.getTotalIdleTime();
		allocatedVertices = (HashMap<Vertex, AllocationInfo>) partialSolution.getAllocatedVertices().clone();
		unallocatedVertices = (HashSet<Vertex>) partialSolution.getUnallocatedVertices().clone();
		availableVertices = (HashSet<Vertex>) partialSolution.getAvailableVertices().clone();

		finishTimes = partialSolution.getFinishTimes().clone();
		
		int startTime = calculateStartTime(v, processorNumber);
		
		allocatedVertices.put(v, new AllocationInfo(processorNumber, startTime));
		unallocatedVertices.remove(v);
		
		updateAvailableVertices(v);
		availableVertices.remove(v);
		
		totalIdleTime += (startTime - finishTimes[processorNumber]);
		
		finishTimes[processorNumber] = (startTime + v.getWeight());
		
		calculateMinimumFinishTime(partialSolution, v);
	}
	
	public int getTotalIdleTime() {
		return totalIdleTime;
	}
	
	public int[] getFinishTimes() {
		return finishTimes;
	}
	
	public int getMinimumFinishTime() {
		return minimumFinishTime;
	}
	
	public HashMap<Vertex, AllocationInfo> getAllocatedVertices() {
		return allocatedVertices;
	}
	
	/**
	 * Gets a hashset of all the available unallocated vertex whose parents vertices are all allocated
	 * @return
	 */
	public HashSet<Vertex> getAvailableVertices() {
		return availableVertices;
	}
	
	public HashSet<Vertex> getUnallocatedVertices() {
		return unallocatedVertices;
	}
		
	/**
	 * Adds any children of the vertex to be added which have all of their parents allocated
	 * @param vertexToBeAdded : should not be in unallocated at the time of this method call
	 */
	protected void updateAvailableVertices(Vertex vertexToBeAdded) {
		
		for (DefaultWeightedEdge e1 : graph.outgoingEdgesOf(vertexToBeAdded)) {
			
			Vertex targetVertex = graph.getEdgeTarget(e1);
			boolean hasAllocatedParent = false;
			
			for(DefaultWeightedEdge e2 : graph.incomingEdgesOf(targetVertex)) {
				if (unallocatedVertices.contains(graph.getEdgeSource(e2))){
					hasAllocatedParent = true;
					break;
				}
			}
			if (!hasAllocatedParent) {
				availableVertices.add(targetVertex);
			}
		}
	}
	
	/**
	 * Calculates the earliest time a vertex can be added to a processor
	 * @param vertexToAdd
	 * @param processorNumber
	 * @return
	 */
	protected int calculateStartTime(Vertex vertexToAdd, byte processorNumber) {
		int maxStartTime = 0;
		for (DefaultWeightedEdge e : graph.incomingEdgesOf(vertexToAdd)) {
			Vertex sourceVertex = graph.getEdgeSource(e);
			AllocationInfo sourceVertexInfo = allocatedVertices.get(sourceVertex);
			int finishTime = sourceVertexInfo.getStartTime() + sourceVertex.getWeight();
			
			if (processorNumber != sourceVertexInfo.getProcessorNumber()) {
				finishTime += graph.getEdgeWeight(e);
			}
			
			maxStartTime = Math.max(maxStartTime, finishTime);
		}
		
		return Math.max(maxStartTime, finishTimes[processorNumber]);
	}
	
	/**
	 * Calculates the largest underestimate of when a solution could finish for use in the comparator.
	 * It is the maximum of the parent solutions finish time and the finish time of the last allocated vertex
	 * @param p
	 * @param v
	 */
	public void calculateMinimumFinishTime(PartialSolution p, Vertex v) {
		int maxHeuristic = Math.max(p.getMinimumFinishTime(), allocatedVertices.get(v).getStartTime() + v.getBottomLevel());
		maxHeuristic = Math.max(maxHeuristic, ((AStar.getSequentialTime() + totalIdleTime) / numProcessors));
//		maxHeuristic = Math.max(maxHeuristic, calculateEarliestUnallocatedVertexFinishTime());
		minimumFinishTime = maxHeuristic;
	}
	
	/**
	 * This function calculates the maximum finish time of the currently available vertices..
	 * Doesn't appear to prune any solutions for any graph, so it is commented out above.
	 * @return
	 */
	private int calculateEarliestUnallocatedVertexFinishTime() {
		int minDataReadyTime = 0;
		
		for (Vertex v : availableVertices) {
			int minStartTime = Integer.MAX_VALUE;
			for (byte proc = 0; proc < numProcessors; proc++) {
				minStartTime = Math.min(minStartTime, calculateStartTime(v, proc));
			}
			minDataReadyTime = Math.max(minDataReadyTime, minStartTime);
		}
		return minDataReadyTime;
	}
		
	@Override
	public int hashCode() {
		//I'm not sure if caching it actually helps, as this should only get called once.
		//This technically uses more memory
		if (hashcode == null) {
			hashcode = allocatedVertices.hashCode();
		}
		
		return hashcode;
	}
	
	@Override
	public boolean equals(Object obj) {
		return allocatedVertices.equals( ((PartialSolution) obj).getAllocatedVertices()); 
	}	
	
	protected void getStartingVertices() {
		availableVertices = (HashSet<Vertex>) AStar.startingVertices.clone(); //Not very object oriented either
	}
	
	/**
	 * Used for debugging a solution
	 */
	public void printDetails() {
		System.out.println(unallocatedVertices.size() + " unallocated vertices.");
		for (Map.Entry<Vertex, AllocationInfo> entry : allocatedVertices.entrySet()) {
			Vertex v = entry.getKey();
			AllocationInfo a = entry.getValue();
			System.out.println("Task: " + v.getName() + " starts at " + a.getStartTime() + " on processor " + a.getProcessorNumber() + " and finished at " + (a.getStartTime() + v.getWeight()));
		}
	}
	
	public void verify() {
		for (Map.Entry<Vertex, AllocationInfo> entry : allocatedVertices.entrySet()) {
			Vertex vertex = entry.getKey();
			AllocationInfo info = entry.getValue();
			for (DefaultWeightedEdge e : graph.incomingEdgesOf(vertex)) {
				Vertex parent = graph.getEdgeSource(e);
				AllocationInfo parentInfo = allocatedVertices.get(parent);
				if(info.getStartTime() < parentInfo.getStartTime() + parent.getWeight()) {
					System.out.println("===== Solution is incorrect! =====");
					System.out.println("Vertex " + vertex.getName() + " starts at " + info.getStartTime() + " but parent "  + parent.getName() + " finished at " + (parentInfo.getStartTime() + parent.getWeight()));
				}
			}
		}
	}
	
}

