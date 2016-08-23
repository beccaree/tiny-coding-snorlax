package scheduling_solution.astar.parallel;

import scheduling_solution.astar.PartialSolution;

public interface AStarRunnable extends Runnable{
	public PartialSolution getOptimalSolution();
}
