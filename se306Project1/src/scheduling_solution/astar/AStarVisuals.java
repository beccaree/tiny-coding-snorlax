package scheduling_solution.astar;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.visualisation.GraphVisualisation;

public class AStarVisuals extends AStarSeq {
	
	private GraphVisualisation visualisation;

	public AStarVisuals(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, GraphVisualisation visualisation) {
		super(graph, numProcessors);
		this.visualisation = visualisation;
	}
	
public PartialSolution calculateOptimalSolution() {
		
		// Create a crude upper bound for pruning
		for (Vertex v : graph.vertexSet()) {
			super.sequentialTime += v.getWeight();
		}
		
		//Get initial vertices of solution
		initialiseStartingVertices();
		initialiseStartStates();

		while (true) {
			solutionsPopped++;

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

						/* Log memory for optimisation purposes */
						long mem = Runtime.getRuntime().totalMemory();
						if (mem > maxMemory) {
							maxMemory = mem;
						}
						solutionsCreated++;

						// Only add the solution to the priority queue if it
						// passes the pruning check

						if (super.isViable(newSolution)) {
							unexploredSolutions.add(newSolution);
						}
					}
				}
				exploredSolutions.add(currentSolution);
				visualisation.updateQueueSize(unexploredSolutions.size(), exploredSolutions.size());
			}
		}
	}

}
