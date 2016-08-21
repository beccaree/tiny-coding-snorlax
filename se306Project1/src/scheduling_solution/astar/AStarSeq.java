package scheduling_solution.astar;

import java.util.AbstractQueue;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.solver.BottomLevelCalculator;
import scheduling_solution.visualisation.GraphVisualisation;

/**
 * Standard sequential version of AStar
 */
public class AStarSeq {
	public GraphInterface<Vertex, DefaultWeightedEdge> graph;
	
	protected PriorityQueue<PartialSolution> unexploredSolutions;
	protected Set<PartialSolution> exploredSolutions;
	protected final byte numProcessors;
	
	public int solutionsPopped = 0;
	public int solutionsCreated = 0;
	public int solutionsPruned = 0;
	public long maxMemory = 0;
	
	public AStarSeq(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors) {
		this.graph = graph;
		unexploredSolutions = new PriorityQueue<>(1000, new PartialSolutionComparator());
		exploredSolutions = new HashSet<>();
		this.numProcessors = numProcessors;
	}
	
	/**
	 * Calculates the optimal solution
	 * @param graph - weighted digraph
	 * @return optimal PartialSolution object
	 */
	public PartialSolution calculateOptimalSolution() {
		
		
		initialise();

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
	 * Initialises a crude upper bound (sequentialTime) as well as the starting
	 * vertices and solutions.
	 * The starting vertices and sequential time are stored statically in PartialSolution.java
	 */
	public void initialise() {
		BottomLevelCalculator.calculate(graph);
		
		// Create a crude upper bound for pruning
		int sequentialTime = 0;
		for (Vertex v : graph.vertexSet()) {
			sequentialTime += v.getWeight();
		}
		PartialSolution.setSequentialTime(sequentialTime);
		
		HashSet<Vertex> startingVertices = new HashSet<>();
		for (Vertex v : graph.vertexSet()) {
			if (graph.inDegreeOf(v) == 0) {
				startingVertices.add(v);
				
			}
		}	
		
		PartialSolution.setStartingVertices(startingVertices);

		for (Vertex v : startingVertices) {
			unexploredSolutions.add(new PartialSolution(graph, numProcessors, v, (byte)0));
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
	
	public AbstractQueue<PartialSolution> getUnexploredSolutions() {
		return unexploredSolutions;
	}
	
	
	/**
	 * Checks to see if a solution has no chance of being an optimal solution, using all pruning/bound checks
	 * Can get a simple upper bound by adding all vertices together (== running them all sequentially on one processor)
	 * Should check if it exists in the exploredSolutions Set
	 * @param partialSolution
	 * @return True - if the given ParticalSolution has a chance of being an optimal solution
	 */
	public boolean isViable(PartialSolution partialSolution) {
		if (exploredSolutions.contains(partialSolution) || partialSolution.getMinimumFinishTime() > PartialSolution.getSequentialTime() ) {
			solutionsPruned++;
			return false;
		}

		return true;
	}
}
