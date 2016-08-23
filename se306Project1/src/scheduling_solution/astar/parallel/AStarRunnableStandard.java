package scheduling_solution.astar.parallel;

import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.astar.AStar;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

/**
 * Runnable created by AStarParallelThreads to carry out the search in parallel.
 * The only difference is that we give it a PriorityQueue of PartialSolutions: it does not initialise it's own.
 */
public class AStarRunnableStandard extends AStar implements AStarRunnable{
	final int threadNumber; // What number thread the runnable is
	
	private PartialSolution optimalSolution = null;
	private int solutionsPopped= 0;
	
	public AStarRunnableStandard(int i, GraphInterface<Vertex, DefaultWeightedEdge> graph, PriorityQueue<PartialSolution> unexploredSolutions, Set<PartialSolution> exploredSolutions, byte numProcessors ) {
		super(graph, numProcessors);
		this.threadNumber = i;
		this.unexploredSolutions = unexploredSolutions;
		this.exploredSolutions = exploredSolutions;
	}
	
	@Override
	public void run() {
		while (shouldRunSequentially()) {
			PartialSolution currentSolution = unexploredSolutions.poll();
			solutionsPopped++;

			// Check if partial solution contains all the vertices
			if (isComplete(currentSolution)) {
				optimalSolution = currentSolution;
				System.out.println(solutionsPopped);
				return;
			} else {
				expandPartialSolution(currentSolution);
			}
		}
		
		/*This will be reached in the parallel version, as shouldRunSequentially() 
		  will return false after N iterations*/
	}
	
	public PartialSolution getOptimalSolution() {
		return optimalSolution;
	}
	
	/**
	 * The runnable should run until a solution is found
	 */
	@Override
	protected boolean shouldRunSequentially() {
		return true;
	}

}
