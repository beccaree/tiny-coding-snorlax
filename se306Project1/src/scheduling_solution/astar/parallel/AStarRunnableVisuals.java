package scheduling_solution.astar.parallel;

import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.astar.AStarVisuals;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.visualisation.GraphVisualisation;

/**
 * Visual version of the AStarRunnable class
 * 
 * @author Team 8
 *
 */
public class AStarRunnableVisuals extends AStarVisuals implements AStarRunnable{
	
	private GraphVisualisation visualisation;	
	final int threadNumber;
	
	private PartialSolution optimalSolution = null;
	AStarVisuals parent;

	public AStarRunnableVisuals(int i, GraphInterface<Vertex, DefaultWeightedEdge> graph, 
			PriorityQueue<PartialSolution> unexploredSolutions, Set<PartialSolution> exploredSolutions, 
			byte numProcessors, GraphVisualisation visualisation, AStarVisuals parent) {
		
		super(graph, numProcessors, i, visualisation, true);
		this.threadNumber = i;
		this.unexploredSolutions = unexploredSolutions;
		this.exploredSolutions = exploredSolutions;
		this.visualisation = visualisation;
		this.parent = parent;
	}
	
	@Override
	public void run() {
		while (true) {
			PartialSolution currentSolution = unexploredSolutions.poll();
			
			// check partial solution has all vertices allocated
			if (isComplete(currentSolution)) {
				this.optimalSolution = currentSolution;
				incrementStatistics();
				return;
			} else {
				expandPartialSolution(currentSolution);
				visualisation.updateQueueSize(threadNumber, unexploredSolutions.size(), exploredSolutions.size());
			}
		}
	}
	
	@Override
	protected void expandPartialSolution(PartialSolution solution) {
		for (Vertex v : solution.getAvailableVertices()) {
			for (byte processor = 0; processor < numProcessors; processor++) {
				
				createViableSolution(solution, v, processor);

				// Only adds vertex to leftmost empty processor
				if (solution.getFinishTimes()[processor] == 0) {
					break;
				}

			}
		}
		exploredSolutions.add(solution);
		
		solutionsPopped++;
		maxMemory = Math.max(maxMemory, Runtime.getRuntime().totalMemory());
		visualisation.updateQueueSize(threadNumber, unexploredSolutions.size(), exploredSolutions.size());
	}
	
	@Override
	public PartialSolution getOptimalSolution() {
		return optimalSolution;
	}

	/**
	 * Increments parent statistics when it is complete, so final Gantt chart can have Final statistics
	 */
	private void incrementStatistics() {
		parent.solutionsCreated += this.solutionsCreated;
		parent.solutionsPopped += this.solutionsPopped;
		parent.solutionsPruned += this.solutionsPruned;
		parent.maxMemory = Math.max(parent.maxMemory, this.maxMemory);
	}
}
