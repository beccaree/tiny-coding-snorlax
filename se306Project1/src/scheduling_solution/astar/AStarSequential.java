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

		while (true) {
//			solutionsPopped++;
			
			/* Log memory for optimization purposes */
//			maxMemory = Math.max(maxMemory, Runtime.getRuntime().totalMemory());

			// priority list of unexplored solutions
			PartialSolution currentSolution = unexploredSolutions.poll();

			// Check if partial solution contains all the vertices
			if (isComplete(currentSolution)) {
				return currentSolution;
			} else {
				expandPartialSolution(currentSolution);
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
	
}
