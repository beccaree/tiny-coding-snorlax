package scheduling_solution.astar;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.visualisation.GraphVisualisation;

public class AStarVisuals extends AStarSequential {
	
	private GraphVisualisation visualisation;

	public AStarVisuals(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, GraphVisualisation visualisation) {
		super(graph, numProcessors);
		this.visualisation = visualisation;
	}
	
	public PartialSolution calculateOptimalSolution() {
		
		initialise();

		while (true) {
			solutionsPopped++;

			maxMemory = Math.max(maxMemory, Runtime.getRuntime().totalMemory());
			// priority list of unexplored solutions
			PartialSolution currentSolution = unexploredSolutions.poll();

			// check partial solution has all vertices allocated
			if (isComplete(currentSolution)) {
				return currentSolution;
			} else {
				expandPartialSolution(currentSolution);
				visualisation.updateQueueSize(unexploredSolutions.size(), exploredSolutions.size());
			}
		}
	}
	
	@Override
	public void expandPartialSolution(PartialSolution solution) {
		for (Vertex v : solution.getAvailableVertices()) {
			for (byte processor = 0; processor < numProcessors; processor++) {
				// add vertex into solution
				PartialSolution newSolution = new PartialSolution(graph, solution, v, processor);
				// Only add the solution to the priority queue if it
				// passes the pruning check

				if (isViable(newSolution)) {
					unexploredSolutions.add(newSolution);
				} else {
					solutionsPruned++;
				}
				
				//TODO is there a better way to increment these rather than overriding the whole method?
				solutionsCreated++; 

				// Only adds vertex to leftmost empty processor
				if (solution.getFinishTimes()[processor] == 0) {
					break;
				}

			}
		}
		
		exploredSolutions.add(solution);
		
	}

}
