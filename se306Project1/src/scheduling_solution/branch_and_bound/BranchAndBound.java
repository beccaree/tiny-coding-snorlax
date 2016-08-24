package scheduling_solution.branch_and_bound;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.branch_and_bound.PartialSolution;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

/**
 * Basic Branch and Bound algorithm used to verify that the solution
 * the AStar algorithm returned was optimal
 */
public class BranchAndBound {
	
	public static GraphInterface<Vertex, DefaultWeightedEdge> graph;
	public static int numProcessors = 1;
	private static int bestBound = 10000;//The highest value we think it could have gone
	private static PartialSolution currentBest = null;
	public static Queue<PartialSolution> activeStates;
	private static HashSet<Vertex> startVertices;
	
	/**
	 * Calculates the optimal solution using the branch and bound algorithm
	 * @param graph	 - diagraph of all the vertex and their solutions
	 * @param numProcessors - number of processors 
	 * @return	the optimal solution 
	 */
	public PartialSolution calculateOptimalSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph, int numProcessors) {
		BranchAndBound.graph = graph;
		BranchAndBound.numProcessors = numProcessors;
		activeStates = new LinkedList<PartialSolution>();
		
		getStartStates();
		
		//If there are any vertex that can be put into the solution
		while (activeStates.size() != 0) {
			PartialSolution startState = activeStates.poll();
			depthFirstBranchAndBound(startState);
		}
		return BranchAndBound.currentBest;
	}
	
	/**
	 * Depth-First-Branch-And-Bound
	 * @param p	- partial solution which we are trying to expand on
	 */
	public static void depthFirstBranchAndBound(PartialSolution p) {
		
		int minimumFinishTime = getMinimumFinishTime(p);
		
		//It won't be better so don't bother
		if (minimumFinishTime > bestBound) {
			return;
		}
		
		if(isComplete(p)) {
			int finishTime = getFinishTime(p);
			bestBound = finishTime;
			currentBest = p;
			return;
		}
		
		ArrayList<PartialSolution> children = getChildren(p);
		for (PartialSolution nextPartialSolution : children) {
			depthFirstBranchAndBound(nextPartialSolution);
		}
	}
	
	/**
	 * Gets starting partial solutions
	 */
	public static void getStartStates() {
		
		startVertices = new HashSet<Vertex>();
		Set<Vertex> vertices = graph.vertexSet();
		
		//Add vertices in graph with no parents to startVertices
		for (Vertex v : vertices) {
			if(graph.inDegreeOf(v) == 0) {
				startVertices.add(v);
			}
		}
		
		//Create starting partial solutions with a start vertex in processor 1
		for(Vertex startVertex : startVertices) {
			for(int p = 0; p < numProcessors; p++) {
				PartialSolution startPartialSolution = new PartialSolution(graph, (byte) numProcessors, startVertex, (byte) 0);
				activeStates.add(startPartialSolution);
			}
		}
	}
	
	/**
	 * Gets the final finish time of the solution
	 * @param p - solution to get the finish time of
	 * @return finish time
	 */
	public static int getFinishTime(PartialSolution p) {
		
		int[] processorFinishTimes = p.getFinishTimes();		
		int finishTime = 0;			
		
		for (int i=0; i < processorFinishTimes.length; i++) {
			finishTime = Math.max(finishTime, processorFinishTimes[i]);
		}		
		return finishTime;
	}

	/**
	 * Gets all available children vertices of  vertices that have already 
	 * been allocated into the partial solution
	 * @param p - partial solution
	 * @return Available children vertices
	 */
	public static ArrayList<PartialSolution> getChildren(PartialSolution p) {
		
		ArrayList<PartialSolution> children = new ArrayList<PartialSolution>();
		HashSet<Vertex> availableVertices = p.getAvailableVertices();
		
		for (Vertex v : availableVertices) {
			for (int i = 0; i < numProcessors; i++) {
				PartialSolution child = new PartialSolution(graph, p, v, (byte) i);
				children.add(child);
			}
		}
		return children;
	}
	
	
	/**
	 * Check if all vertex have been used in a solution
	 * @param p - solution that is to be checked
	 * @return true - if solution is completed
	 */
	public static boolean isComplete(PartialSolution p) {
		return p.getUnallocatedVertices().size() == 0;
	}
	
	public static HashSet<Vertex> getStartVertices() {
		return BranchAndBound.startVertices;
	}
	
	
	public static int getMinimumFinishTime(PartialSolution p) {
		return p.getMinimumFinishTime();
	}
	
	/**
	 * Calculates the time taken for all tasks to be run
	 * sequentially in one processor
	 * @return sequential time calculated
	 */
	public static int getSequentialTime() {
		int sequentialTime = 0;
		
		for (Vertex v : graph.vertexSet()) {
			sequentialTime += v.getWeight();
		}
		return sequentialTime;
	}

}
