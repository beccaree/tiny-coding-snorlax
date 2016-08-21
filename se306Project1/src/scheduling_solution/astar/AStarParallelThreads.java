package scheduling_solution.astar;

import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

/**
 * Performs a certain number of iterations sequentially, then divides up the priority queue among n threads
 * Explored solutions is shared but not thread safe: a thread safe Set slows down the program, meaning that 
 * the decreased pruning is better than a blocking Set.
 *
 */
public class AStarParallelThreads extends AStarSeq {
	final int nThreads;
	
	//Number of solutions to create
	private final static int SOLUTIONS_TO_CREATE = 1000;
	
	public AStarParallelThreads(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, int nThreads) {
		super(graph, numProcessors);
		this.nThreads = nThreads;
	}
	
	@Override
	public PartialSolution calculateOptimalSolution() {
		initialise();
		
		//Run it sequentially until we have the desired number of solutions
		while (unexploredSolutions.size() < (nThreads + SOLUTIONS_TO_CREATE)) {

			// priority queue of unexplored solutions
			PartialSolution currentSolution = unexploredSolutions.poll();

			// Check if partial solution has all vertices allocated
			if (isComplete(currentSolution)) {
				return currentSolution;
			} else {
				for (Vertex v : currentSolution.getAvailableVertices()) {
					for (byte processor = 0; processor < numProcessors; processor++) {
						// Create new solution that is a clone of it's parent + the new vertex
						PartialSolution newSolution = new PartialSolution(graph, currentSolution, v, processor);
						solutionsCreated++;
						
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
		
		//After the desired number of solutions is reached, perform the search in parallel
		PriorityQueue<PartialSolution>[] queues = new PriorityQueue[nThreads];
		
		//Initialise the queues
		for (int i = 0; i < nThreads; i++) {
			queues[i] = new PriorityQueue<PartialSolution>(new PartialSolutionComparator());
		}
		
		//Rotate through the list of queues, popping a solution for each until it is empty
		PartialSolution ps;
		int n = 0;
		while ((ps = unexploredSolutions.poll()) != null) {
			queues[n].add(ps);
			n++;
			
			if (n == nThreads) { //Go back to queue[0]
				n = 0;
			}
		}
		unexploredSolutions = null; //Prevent further accidental access 
		
		PartialSolution[] resultList = new PartialSolution[nThreads];
		AStarRunnable[] runnables = new AStarRunnable[nThreads];
		Thread threads[] = new Thread[nThreads];
		
		//Initialise the other threads and start them
		for (int i = 1; i < nThreads; i++) {
			runnables[i] = new AStarRunnable(i, graph, queues[i], resultList, exploredSolutions, numProcessors);
			threads[i] = new Thread(runnables[i]);
			threads[i].start();
		}
		
		//This is the main thread
		runnables[0] = new AStarRunnable(0, graph, queues[0], resultList, exploredSolutions, numProcessors);
		runnables[0].run();
		
		//Wait for all threads to finish
		for (int i = 1; i < nThreads; i++) {
			try {
				threads[i].join();
			} catch (InterruptedException e) {
				e.printStackTrace();
			}
		}
		
		//Get the shortest result and return it
		PartialSolution bestSolution = resultList[0];
		for (int i = 1; i < nThreads; i++) {
			if (bestSolution.getFinishTime() < resultList[i].getFinishTime()) {
				bestSolution = resultList[i];
			}
		}
		return bestSolution;
		
	}
	

}
