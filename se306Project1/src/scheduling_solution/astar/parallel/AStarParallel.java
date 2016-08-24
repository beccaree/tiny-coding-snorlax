package scheduling_solution.astar.parallel;

import java.util.PriorityQueue;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.astar.AStarSequential;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.astar.PartialSolutionComparator;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

/**
 * Performs a certain number of iterations sequentially, then divides up the priority queue among n threads
 * Explored solutions is shared but not thread safe: a thread safe Set slows down the program, meaning that 
 * the decreased pruning is better than a blocking Set.
 *
 */
public class AStarParallel extends AStarSequential {
	protected int nThreads;
	
	//Number of solutions to create. 1000 seems to provide a good balance
	protected final static int SOLUTIONS_TO_CREATE = 1000;
	
	public AStarParallel(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, int nThreads) {
		super(graph, numProcessors);
		this.nThreads = nThreads;
	}
	
	@Override
	public PartialSolution calculateOptimalSolution() {
		PartialSolution solution = super.calculateOptimalSolution();
		
		//Will return null if the solution was not found in the sequential part
		if (solution != null) {
			return solution;
		}
		return calculateOptimalInParallel();
	}
	
	/**
	 * The parallel version should only run sequentially until we create the desired
	 * number of solutions.
	 */
	@Override
	protected boolean shouldRunSequentially() {
		return unexploredSolutions.size() < (nThreads + SOLUTIONS_TO_CREATE);
	}
	
	protected PartialSolution calculateOptimalInParallel() {
		// After the desired number of solutions is reached, perform the search
		// in parallel
		PriorityQueue<PartialSolution>[] queues = new PriorityQueue[nThreads];

		// Initialize the queues of each thread
		for (int i = 0; i < nThreads; i++) {
			queues[i] = new PriorityQueue<PartialSolution>(1000, new PartialSolutionComparator());
		}

		// Rotate through the list of queues, popping a solution for each until
		// it is empty
		PartialSolution ps;
		int index = 0;
		
		//If we are running in parallel with one thread, don't bother popping all solutions only to re-add them
		if (nThreads == 1) {
			queues[0] = unexploredSolutions;
		} else {
			while ((ps = unexploredSolutions.poll()) != null) {
				queues[index].add(ps);
				index++;

				if (index == nThreads) { // Go back to queue[0]
					index = 0;
				}
			}
		}
		
		unexploredSolutions = null; // Prevent further accidental access

		AStarRunnable[] runnables = new AStarRunnable[nThreads];
		Thread threads[] = new Thread[nThreads];

		createAndStartThreads(runnables, threads, queues);

		// Wait for all threads to finish
		for (int i = 1; i < nThreads; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}

		return findOptimalSolution(runnables);
		
	}

	protected void createAndStartThreads(AStarRunnable[] runnables, Thread[] threads, PriorityQueue<PartialSolution>[] queues) {
		// Initialise the other threads and start them
		for (int i = 1; i < nThreads; i++) {
			runnables[i] = new AStarRunnableStandard(i, graph, queues[i], exploredSolutions, numProcessors);
			threads[i] = new Thread(runnables[i]);
			threads[i].start();
		}

		// This is the main thread, doesn't need to be wrapped in a thread
		runnables[0] = new AStarRunnableStandard(0, graph, queues[0], exploredSolutions, numProcessors);
		runnables[0].run();
	}
	
	protected PartialSolution findOptimalSolution(AStarRunnable[] runnables) {
		PartialSolution bestSolution = runnables[0].getOptimalSolution();
		for (int i = 1; i < nThreads; i++) {
			if (bestSolution.getFinishTime() < runnables[i].getOptimalSolution().getFinishTime()) {
				bestSolution = runnables[i].getOptimalSolution();
			}
		}
		return bestSolution;
	}
}
