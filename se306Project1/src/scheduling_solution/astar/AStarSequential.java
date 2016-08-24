package scheduling_solution.astar;


import java.util.HashSet;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.solver.BottomLevelCalculator;

/**
 * Standard sequential version of AStar
 */
public class AStarSequential extends AStar{
	
	public AStarSequential(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors) {
		super(graph, numProcessors);
		initialiseDataStructures();
	}
	
	/**
	 * Calculates the optimal solution
	 * @param graph - weighted digraph
	 * @return optimal PartialSolution object
	 */
	public PartialSolution calculateOptimalSolution() {
		initialise();
		return super.calculateOptimalSolution();
	}
	
	/**
	 * Initialises a crude upper bound (sequentialTime) as well as the starting
	 * vertices and solutions.
	 * The starting vertices and sequential time are stored statically in PartialSolution.java
	 * to save on memory/computation time.
	 */
	public void initialise() {
		BottomLevelCalculator.calculate(graph);
		
		// Create a crude upper bound for pruning
		int sequentialTime = 0;
		for (Vertex v : graph.vertexSet()) {
			sequentialTime += v.getWeight();
		}
		PartialSolution.setSequentialTime(sequentialTime);
		
		//Get all nodes of indegree 0
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
	 * Method to be overridden in the parallel version.
	 * Should return false when we should break the sequential for loop
	 * @return
	 */
	protected boolean shouldRunSequentially() {
		return true;
	}
}
