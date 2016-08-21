package scheduling_solution.astar.fullyparallel;

import java.util.AbstractQueue;
import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.astar.AStarSeq;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.astar.PartialSolutionComparator;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.visualisation.GraphVisualisation;

/**
 * This class is a parallel version of AStar that attempt to parallelise the whole thing
 * by using a blocking queue.
 * Its alright sometimes, but others it is really bad e.g. Nodes_11 with 2 proc.
 * The other parallelised version is faster
 * @author Stefan
 *
 */
public class AStarParallelThreadsSlow extends AStarSeq{
	PriorityBlockingQueue<PartialSolution> unexploredSolutions;
	final int nThreads;
	
	
	public AStarParallelThreadsSlow(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, int nThreads) {
		super(graph, numProcessors);
		unexploredSolutions = new PriorityBlockingQueue<>(1000, new PartialSolutionComparator());
		this.nThreads = nThreads;
	}
	
	/**
	 * Calculates the optimal solution
	 * @param graph - weighted digraph
	 * @return optimal PartialSolution object
	 */
	public PartialSolution calculateOptimalSolution() {
		
		PartialSolution[] resultList = new PartialSolution[nThreads];
		
		initialise();

		//CREATE AND START THREADS HERE
		AStarRunnable[] runnables = new AStarRunnable[nThreads];
		Thread threads[] = new Thread[nThreads];
		
		for (int i = 1; i < nThreads; i++) {
			runnables[i] = new AStarRunnable(i, graph, unexploredSolutions, resultList, exploredSolutions, numProcessors);
			threads[i] = new Thread(runnables[i]);
			threads[i].start();
		}
		
		runnables[0] = new AStarRunnable(0, graph, unexploredSolutions, resultList, exploredSolutions, numProcessors);
		runnables[0].run();
		
		for (int i = 1; i < nThreads; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		PartialSolution bestSolution = resultList[0];
		resultList[0].printDetails();
		System.out.println("============");
		for (int i = 1; i < nThreads; i++) {
			resultList[i].printDetails();
			System.out.println("============");
			if (bestSolution.getFinishTime() < resultList[i].getFinishTime()) {
				bestSolution = resultList[i];
			}
		}
		
		for(int i = 0; i < nThreads; i++ ) {
			System.out.println("sol popped " + runnables[i].solutionsPopped);
		}
		
		return bestSolution;
		
	}
	
	
	
	public AbstractQueue<PartialSolution> getUnexploredSolutions() {
		return unexploredSolutions;
	}
	
	
	
	/**
	 * Checks to see if a solution has no chance of being an optimal solution, using all pruning/bound checks
	 * Can get a simple upper bound by adding all vertices together (== running them all sequentially on one processor)
	 * Should check if it exists in the exploredSolutions Set
	 * @param partialSolution
	 * @return True - if the given ParticalSolution has a chance of being an optimal solution
	 */
	
}
