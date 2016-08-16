package scheduling_solution.astar;

import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

//TODO dont pass graph in constructor, make it static and access it from elsewhere to save memory
public class PartialSolution {
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;

	private byte numProcessors;
	
	private int[] idleTimes;
	private int[] finishTimes;
	
	private HashMap<Vertex, AllocationInfo> allocatedVertices;
	private HashSet<Vertex> availableVertices;
	private HashSet<Vertex> unallocatedVertices;
	
	private int minimumFinishTime = 0;
	
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
		
		idleTimes = new int[numProcessors];
		finishTimes = new int[numProcessors];
		finishTimes[processorNumber] = v.getWeight();
		
		allocatedVertices = new HashMap<>();
		allocatedVertices.put(v, new AllocationInfo(processorNumber, 0));
		
		unallocatedVertices = new HashSet<>();
		unallocatedVertices.addAll(graph.vertexSet());
		unallocatedVertices.remove(v);
		
		availableVertices = (HashSet<Vertex>) AStar.startingVertices.clone(); //Not very object oriented either
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
		
		allocatedVertices = (HashMap<Vertex, AllocationInfo>) partialSolution.getAllocatedVertices().clone();
		unallocatedVertices = (HashSet<Vertex>) partialSolution.getUnallocatedVertices().clone();
		availableVertices = (HashSet<Vertex>) partialSolution.getAvailableVertices().clone();
		idleTimes = partialSolution.getIdleTimes().clone();
		finishTimes = partialSolution.getFinishTimes().clone();
		
		int startTime = calculateStartTime(v, processorNumber);
		
		allocatedVertices.put(v, new AllocationInfo(processorNumber, startTime));
		unallocatedVertices.remove(v);
		
		updateAvailableVertices(v);
		availableVertices.remove(v);
		
		idleTimes[processorNumber] += (startTime - finishTimes[processorNumber]);
		
		finishTimes[processorNumber] = (startTime + v.getWeight());
		
		calculateMinimumFinishTime(partialSolution, v);
		
	}
	
	//Could implement a clone() method and return a new object instead of all this stuff?
	public int[] getIdleTimes() {
		return idleTimes;
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
	private void updateAvailableVertices(Vertex vertexToBeAdded) {
		
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
	private int calculateStartTime(Vertex vertexToAdd, byte processorNumber) {
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
		int j = Math.max(p.getMinimumFinishTime(), allocatedVertices.get(v).getStartTime() + v.getBottomLevel());
//		minimumFinishTime = a;//TODO use load balance here as well
//		int i = 0;
//		
//		for (Map.Entry<Vertex, AllocationInfo> entry : allocatedVertices.entrySet()) {
//			Vertex vertex = entry.getKey();
//			AllocationInfo a = entry.getValue();
//			i = Math.max(i, a.getStartTime() + vertex.getBottomLevel());
//		}
//		if (i != j) {
//			System.out.println("i != j");
//		}
		
		minimumFinishTime = j;
	}
	
	@Override
	public int hashCode() {
		//This is cachable I think, as a partialSolution shouldn't change. would it help though?
		return allocatedVertices.hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		return allocatedVertices.equals( ((PartialSolution) obj).getAllocatedVertices()); 
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

