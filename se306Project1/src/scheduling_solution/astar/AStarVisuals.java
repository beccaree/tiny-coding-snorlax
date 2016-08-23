package scheduling_solution.astar;

import java.util.PriorityQueue;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.astar.parallel.AStarRunnableVisuals;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.visualisation.GraphVisualisation;

public class AStarVisuals extends AStarSequential {
	private int nThreads;
	
	public int solutionsPopped = 0;// TODO should these be here, or in
	// visualisation subclasses
	public int solutionsCreated = 0;
	public int solutionsPruned = 0;
	public long maxMemory = 0;
	
	//Number of solutions to create
	private static final int SOLUTIONS_TO_CREATE = 1000;
	
	private GraphVisualisation visualisation;
	private boolean isParallel;

	public AStarVisuals(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, int numThreads, GraphVisualisation visualisation, boolean isParallel) {
		super(graph, numProcessors);
		this.visualisation = visualisation;
		this.isParallel = isParallel;
		this.nThreads = numThreads;
	}
	
	@Override
	public PartialSolution calculateOptimalSolution() {
		
		initialise();

		PartialSolution partialSolution = super.calculateOptimalSolution();
		
		//If it is sequential or it is running in parallel but finished, return it
		if (partialSolution != null) {
			return partialSolution;
		}
		
		// SHOULD ONLY EXECUTE IF IS PARALLEL-------------------------------------------->
		//After the desired number of solutions is reached, perform the search in parallel
		if (nThreads == 0) { nThreads = 1; }
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
		
		AStarRunnableVisuals[] runnables = new AStarRunnableVisuals[nThreads];
		Thread threads[] = new Thread[nThreads];
		
		//Initialise the other threads and start them
		for (int i = 1; i < nThreads; i++) {
			runnables[i] = new AStarRunnableVisuals(i, graph, queues[i], exploredSolutions, numProcessors, visualisation);
			threads[i] = new Thread(runnables[i]);
			threads[i].start();
		}
		
		//This is the main thread
		runnables[0] = new AStarRunnableVisuals(0, graph, queues[0], exploredSolutions, numProcessors, visualisation);
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
		PartialSolution bestSolution = runnables[0].calculateOptimalSolution();
		for (int i = 1; i < nThreads; i++) {
			if (bestSolution.getFinishTime() < runnables[i].calculateOptimalSolution().getFinishTime()) {
				bestSolution = runnables[i].calculateOptimalSolution();
			}
		}
		return bestSolution;
		// ---------------------------------------------------------------------------->
	}
	

	//Overridden methods to increment statistics when called
	@Override
	protected void createViableSolution(PartialSolution solution, Vertex v, byte processor) {
		super.createViableSolution(solution, v, processor);
		solutionsCreated++;
	}
	
	@Override
	protected boolean isViable(PartialSolution partialSolution) {
		boolean isViable = super.isViable(partialSolution);
		if (!isViable) {
			solutionsPruned++;
		}
		return isViable;
	}
	
	@Override
	protected void expandPartialSolution(PartialSolution solution) {
		super.expandPartialSolution(solution);
		solutionsPopped++;
		maxMemory = Math.max(maxMemory, Runtime.getRuntime().totalMemory());
		visualisation.updateQueueSize(0, unexploredSolutions.size(), exploredSolutions.size());
	}
	
	@Override
	protected boolean shouldRunSequentially() {
		return !isParallel || unexploredSolutions.size() < (nThreads + SOLUTIONS_TO_CREATE);
	}

}
