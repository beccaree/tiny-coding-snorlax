package scheduling_solution.astar;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.visualisation.GraphVisualisation;

public class AStarSeq {
	public GraphInterface<Vertex, DefaultWeightedEdge> graph;
	public static HashSet<Vertex> startingVertices;
	
	PriorityQueue<PartialSolution> unexploredSolutions;
	Set<PartialSolution> exploredSolutions;
	final byte numProcessors;
	
	public static Integer sequentialTime = null;//Use an object so we get an error when it is not initialised
	
	public int solutionsPopped = 0;
	public int solutionsCreated = 0;
	public int solutionsPruned = 0;
	public long maxMemory = 0;
	
	public AStarSeq(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors) {
		this.graph = graph;
		unexploredSolutions = new PriorityQueue<>(1000, new PartialSolutionComparator());
		exploredSolutions = new HashSet<>();
		startingVertices = new HashSet<>();
		this.numProcessors = numProcessors;
	}
	
	/**
	 * Calculates the optimal solution
	 * @param graph - weighted digraph
	 * @return optimal PartialSolution object
	 */
	public PartialSolution calculateOptimalSolution() {
		
		// Create a crude upper bound for pruning
		sequentialTime = new Integer(0);
		for (Vertex v : graph.vertexSet()) {
			sequentialTime += v.getWeight();
		}
		
		//Get initial vertices of solution
		initialiseStartingVertices();
		initialiseStartStates();

		while (true) {
			solutionsPopped++;
			
			/* Log memory for optimisation purposes */
			maxMemory = Math.max(maxMemory, Runtime.getRuntime().totalMemory());

			// priority list of unexplored solutions
			PartialSolution currentSolution = unexploredSolutions.poll();

			// check partial solution has all vertices allocated
			if (isComplete(currentSolution)) {
				return currentSolution;
			} else {
				for (Vertex v : currentSolution.getAvailableVertices()) {
					for (byte processor = 0; processor < numProcessors; processor++) {
						// add vertex into solution
						PartialSolution newSolution = new PartialSolution(
								graph, currentSolution, v, processor);
						solutionsCreated++;
						// Only add the solution to the priority queue if it
						// passes the pruning check
						
						if (isViable(newSolution)) {
							unexploredSolutions.add(newSolution);
						}
					}
				}
				
				exploredSolutions.add(currentSolution);
			}
		}
	}

	/**
	 * Initialises the PriorityQueue with the possible starting states
	 */
	public void initialiseStartStates() {
		for (Vertex v : startingVertices) {
			unexploredSolutions.add(new PartialSolution(graph, numProcessors, v, (byte)0));//TODO is it ok to add them all to processor 0? Pretty sure it is
		}
	}
	
	public void initialiseStartingVertices() {
		for (Vertex v : graph.vertexSet()) {
			if (graph.inDegreeOf(v) == 0) {
				startingVertices.add(v);
			}
		}
	}
	
	/**
	 * Checks if partial solution has allocated all vertices
	 * @param Partical solution to check
	 * @return	True - all vertices have been allocated
	 */
	public boolean isComplete(PartialSolution p) {
		return p.getUnallocatedVertices().size() == 0;
	}
	
	public PriorityQueue<PartialSolution> getUnexploredSolutions() {
		return unexploredSolutions;
	}
	
	/*Not very object-oriented, but saves time due to not having to calculate it multiple times*/
	public static Integer getSequentialTime() {
		return sequentialTime;
	}
	
	
	/**
	 * Checks to see if a solution has no chance of being an optimal solution, using all pruning/bound checks
	 * Can get a simple upper bound by adding all vertices together (== running them all sequentially on one processor)
	 * Should check if it exists in the exploredSolutions Set
	 * @param partialSolution
	 * @return True - if the given ParticalSolution has a chance of being an optimal solution
	 */
	public boolean isViable(PartialSolution partialSolution) {
		if (exploredSolutions.contains(partialSolution) || partialSolution.getMinimumFinishTime() > sequentialTime ) {
			solutionsPruned++;
			return false;
		}

		return true;
	}
}
