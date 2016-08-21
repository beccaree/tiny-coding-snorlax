package scheduling_solution.astar.fullyparallel;

import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.astar.PartialSolution;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

/**
 * Class created by AStarParallelThreadsSlow to run in parallel.
 * Shouldn't be used in its current state, as the other version is far better
 * @author Stefan
 *
 */
public class AStarRunnable implements Runnable{
	final int threadNumber; // What number thread the runnable is
	GraphInterface<Vertex, DefaultWeightedEdge> graph;
	final PriorityBlockingQueue<PartialSolution> unexploredSolutions;
	final PartialSolution[] resultList;
	final Set<PartialSolution> exploredSolutions;
	final byte numProcessors;
	
	public int solutionsPopped = 0;
	
	public AStarRunnable(int i, GraphInterface<Vertex, DefaultWeightedEdge> graph, PriorityBlockingQueue<PartialSolution> unexploredSolutions, PartialSolution[] resultList, Set<PartialSolution> exploredSolutions, byte numProcessors ) {
		this.threadNumber = i;
		this.graph = graph;
		this.unexploredSolutions = unexploredSolutions;
		this.resultList = resultList;
		this.exploredSolutions = exploredSolutions;
		this.numProcessors = numProcessors;
	}
	
	@Override
	public void run() {
		resultList[threadNumber] = performAStar();
		
	}
	private PartialSolution performAStar() {
		while (true) {
			solutionsPopped++;

			PartialSolution currentSolution = null;
			try {
				currentSolution = unexploredSolutions.poll(10000, TimeUnit.SECONDS);
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
			
			// check partial solution has all vertices allocated
			if (isComplete(currentSolution)) {
				return currentSolution;
			} else {
				for (Vertex v : currentSolution.getAvailableVertices()) {
					for (byte processor = 0; processor < numProcessors; processor++) {
						// add vertex into solution
						PartialSolution newSolution = new PartialSolution(
								graph, currentSolution, v, processor);


						if (isViable(newSolution)) {
							unexploredSolutions.add(newSolution);
						}
					}
				}
				exploredSolutions.add(currentSolution);
			}
		}
	}
	
	public boolean isViable(PartialSolution partialSolution) {
		if (exploredSolutions.contains(partialSolution) || partialSolution.getMinimumFinishTime() > PartialSolution.getSequentialTime()) {
			return false;
		}

		return true;
	}
	
	/**
	 * Checks if partial solution has allocated all vertices
	 * @param Partical solution to check
	 * @return	True - all vertices have been allocated
	 */
	public boolean isComplete(PartialSolution p) {
		return p.getUnallocatedVertices().size() == 0;
	}


}