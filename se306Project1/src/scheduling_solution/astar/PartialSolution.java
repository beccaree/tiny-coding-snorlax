package scheduling_solution.astar;

import java.awt.List;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane.MaximizeAction;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.astar.parallel.AStarParallel;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

@SuppressWarnings("unchecked")
public class PartialSolution {
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;

	protected final byte numProcessors;
	
	private int totalIdleTime;
	private int[] finishTimes;
	
	protected Integer hashcode = null;
	
	private HashMap<Vertex, AllocationInfo> allocatedVertices;
	protected HashSet<Vertex> availableVertices;
	private HashSet<Vertex> unallocatedVertices;
	
	protected int minimumFinishTime = 0;
	
	private static Integer sequentialTime = null; //null to make sure that we initialise it
	private static HashSet<Vertex> startingVertices;
	
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
		
		availableVertices = (HashSet<Vertex>) PartialSolution.startingVertices.clone(); 
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
	
	/**
	 * Gets the actual finishing time 
	 * @return
	 */
	public int getFinishTime() {
		int finishTime = 0;
		for (int i = 0; i < numProcessors; i++) {
			finishTime = Math.max(finishTime, finishTimes[i]);
		}
		return finishTime;
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
		maxHeuristic = Math.max(maxHeuristic, ((PartialSolution.sequentialTime + totalIdleTime) / numProcessors));
//		maxHeuristic = Math.max(maxHeuristic, calculateEarliestUnallocatedVertexFinishTime());
		minimumFinishTime = maxHeuristic;
	}
	
	/**
	 * This function calculates the maximum finish time of the currently available vertices.
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
	
	public static int getSequentialTime() {
		return PartialSolution.sequentialTime;
	}
	
	//Dont want to store a copy of this for all partial solutions
	public static void setSequentialTime(int time) {
		PartialSolution.sequentialTime = time;
	}
	
	public static void setStartingVertices(HashSet<Vertex> startingVertices) {
		PartialSolution.startingVertices = startingVertices;
	}

	@Override
	public int hashCode() {
		//I'm not sure if caching it actually helps, as this should only get called once.
		//This technically uses more memory because we are storing it
		if (hashcode == null) {
			hashcode = allocatedVertices.hashCode();
		}
		
		return hashcode;
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
	
	/**
	 * Used for verification, not to be called in final product
	 */
	public void verify() {
		
//		HashSet<Vertex>[] processor;
		HashSet<String[]> process;
		
		HashSet<String[]> processor0 = new HashSet<>();
		HashSet<String[]> processor1 = new HashSet<>();
		HashSet<String[]> processor2 = new HashSet<>();
		HashSet<String[]> processor3 = new HashSet<>();
		HashSet<String[]> processor4 = new HashSet<>();
		HashSet<String[]> processor5 = new HashSet<>();
		HashSet<String[]> processor6 = new HashSet<>();
		HashSet<String[]> processor7 = new HashSet<>();
		
		
		//Check that each vertex meets each parent's dependencies
		for (Map.Entry<Vertex, AllocationInfo> entry : allocatedVertices.entrySet()) {
			Vertex vertex = entry.getKey();
			AllocationInfo info = entry.getValue();	
			
			//Add vertex into their processor
			int processorNumber = info.getProcessorNumber();
			//[Name,Vertex Weight, Vertex Start Time]
			String[] vertInfo = {vertex.getName(),Integer.toString(vertex.getWeight()),Integer.toString(info.getStartTime())};			
			switch (processorNumber){
			case 0:	processor0.add(vertInfo);
					break;
			case 1:	processor1.add(vertInfo);
					break;
			case 2:	processor2.add(vertInfo);
					break;
			case 3:	processor3.add(vertInfo);
					break;
			case 4:	processor4.add(vertInfo);
					break;
			case 5:	processor5.add(vertInfo);
					break;
			case 6:	processor6.add(vertInfo);
					break;
			case 7:	processor7.add(vertInfo);
					break;
			}
			
			
			for (DefaultWeightedEdge e : graph.incomingEdgesOf(vertex)) {
				Vertex parent = graph.getEdgeSource(e);
				AllocationInfo parentInfo = allocatedVertices.get(parent);
				
				//add in the communication time if the current vertex is in a different processor to parent
				double communicationTime = 0;	
				if(parentInfo.getProcessorNumber()!= info.getProcessorNumber()){
					communicationTime = graph.getEdgeWeight(e);
				}	
				
				double currentParentFinishTime = parentInfo.getStartTime() + parent.getWeight()+communicationTime;
				
				//If current vertex starts before current parent vertex we are look at finishes
				if(info.getStartTime() < currentParentFinishTime) {
					System.out.println("===== Solution is incorrect! =====");
					System.out.println("Vertex " + vertex.getName() + " starts at " + info.getStartTime() + " but parent "  + parent.getName() + " finished at " + (parentInfo.getStartTime() + parent.getWeight()));
				}
			}
		}
		
		ArrayList<HashSet<String[]>> proc = new ArrayList<>();
		proc.add(processor0);
		proc.add(processor1);
		proc.add(processor2);
		proc.add(processor3);
		proc.add(processor4);
		proc.add(processor5);
		proc.add(processor6);
		proc.add(processor7);
		int processorNumber = 1;
		
		//Loop through and check no vertex overlap each other in each processor
		for(HashSet<String[]> p :proc){
			for(String[] vertex1 : p){
				int v1StartTime = Integer.parseInt(vertex1[2]);
				int v1FinishTime = (v1StartTime+Integer.parseInt(vertex1[1]));
				for(String[] vertex2 : p){
					int v2StartTime = Integer.parseInt(vertex2[2]);
					//If not the same vertex names and the start time of vertex 2 is not in the runtime of vertex 1
					if(!vertex1[0].equals(vertex2[0])&&(v2StartTime<v1FinishTime&&v2StartTime>v1StartTime)){
						System.out.println("===== Solution is incorrect! =====");
						System.out.println("Vertex "+vertex1[0]+" overlaps Vertex "+vertex2[0]+" in processor "+ processorNumber);
					}
				}
			}
			processorNumber++;
		}
		
	}
	
	
	
}

