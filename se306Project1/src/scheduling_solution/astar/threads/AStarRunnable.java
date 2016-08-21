package scheduling_solution.astar.threads;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;
import java.util.concurrent.TimeUnit;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.astar.PartialSolution;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

/**
 * Runnable created by AStarParallelThreads to carry out the search in parallel.
 * The only difference is that we give it a PriorityQueue of PartialSolutions: it does not initialise it's own.
 */
public class AStarRunnable implements Runnable{
	final int threadNumber; // What number thread the runnable is
	GraphInterface<Vertex, DefaultWeightedEdge> graph;
	final PriorityQueue<PartialSolution> unexploredSolutions;
	final PartialSolution[] resultList;
	final Set<PartialSolution> exploredSolutions;
	final byte numProcessors;
	
	public int solutionsPopped = 0;
	
	public AStarRunnable(int i, GraphInterface<Vertex, DefaultWeightedEdge> graph, PriorityQueue<PartialSolution> unexploredSolutions, PartialSolution[] resultList, Set<PartialSolution> exploredSolutions, byte numProcessors ) {
		this.threadNumber = i;
		this.graph = graph;
		this.unexploredSolutions = unexploredSolutions;
		this.resultList = resultList;
		this.exploredSolutions = exploredSolutions;
		this.numProcessors = numProcessors;
	}
	
	
	/**
	 * Put the result of the search into an array so we can access if from AStarParallelThreads
	 */
	@Override
	public void run() {
		resultList[threadNumber] = performAStar();
	}
	
	//Methods below are pretty much copies of AStarSeq.java methods. Some form of inheritance would be good here
	private PartialSolution performAStar() {
		while (true) {
			solutionsPopped++;

			// priority list of unexplored solutions
//			System.out.println(unexploredSolutions.size());
			
			PartialSolution currentSolution = unexploredSolutions.poll();
			
			// check partial solution has all vertices allocated
			if (isComplete(currentSolution)) {
				System.out.println(threadNumber + " finished, popped " + solutionsPopped);
				return currentSolution;
			} else {
				for (Vertex v : currentSolution.getAvailableVertices()) {
					for (byte processor = 0; processor < numProcessors; processor++) {
						// add vertex into solution
						PartialSolution newSolution = new PartialSolution(
								graph, currentSolution, v, processor);

						/* Log memory for optimisation purposes */
						long mem = Runtime.getRuntime().totalMemory();
//						if (mem > maxMemory) {
//							maxMemory = mem;
//						}
//						solutionsCreated++;

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
	
	public boolean isViable(PartialSolution partialSolution) {
		if (exploredSolutions.contains(partialSolution) || partialSolution.getMinimumFinishTime() > PartialSolution.getSequentialTime()) {
//			solutionsPruned++;
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
