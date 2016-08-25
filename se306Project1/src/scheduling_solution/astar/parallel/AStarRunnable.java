package scheduling_solution.astar.parallel;

import scheduling_solution.astar.PartialSolution;

/**
 * Interface which requires implementing classes to be able to 
 * return a PartialSolution (specifically, AStarRunnable and AStarRunnableVisuals)
 *
 */
public interface AStarRunnable extends Runnable{
	public PartialSolution getOptimalSolution();
}
