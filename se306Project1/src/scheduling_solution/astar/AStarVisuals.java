package scheduling_solution.astar;

import java.util.HashSet;
import java.util.PriorityQueue;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.astar.parallel.AStarParallel;
import scheduling_solution.astar.parallel.AStarRunnable;
import scheduling_solution.astar.parallel.AStarRunnableVisuals;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.solver.BottomLevelCalculator;
import scheduling_solution.visualisation.GraphVisualisation;

public class AStarVisuals extends AStarParallel {
	public int solutionsPopped = 0;
	public int solutionsCreated = 0;
	public int solutionsPruned = 0;
	public long maxMemory = 0;
	
	private GraphVisualisation visualisation;
	private boolean isParallel;

	public AStarVisuals(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, int numThreads, GraphVisualisation visualisation, boolean isParallel) {
		super(graph, numProcessors, numThreads);
		this.visualisation = visualisation;
		this.isParallel = isParallel;
	}
	
	/**
	 * This class needs to create AStarRunnableVisuals instead of AStarRunnableStandards
	 */
	@Override
	protected void createAndStartThreads(AStarRunnable[] runnables, Thread[] threads, PriorityQueue<PartialSolution>[] queues) {
		// Initialise the other threads and start them
		for (int i = 1; i < nThreads; i++) {
			runnables[i] = new AStarRunnableVisuals(i, graph, queues[i], exploredSolutions, numProcessors,
					visualisation, this);
			threads[i] = new Thread(runnables[i]);
			threads[i].start();
		}

		// This is the main thread
		runnables[0] = new AStarRunnableVisuals(0, graph, queues[0], exploredSolutions, numProcessors, visualisation, this);
		runnables[0].run();
	}

	//Overridden methods to increment statistics when called
	@Override
	protected void createViableSolution(PartialSolution solution, Vertex v, byte processor) {
		PartialSolution newSolution = new PartialSolution(graph, solution, v, processor);
		// Only add the solution to the priority queue if it passes the pruning check
		if (isViable(newSolution)) {
			unexploredSolutions.add(newSolution);
			
			for (Vertex vertex: newSolution.getAllocatedVertices().keySet()) {
				if (vertex.incrementNumbUsed()) {
					visualisation.changeNodeColour(vertex.getName(), vertex.getNumbOfUse());
				}
				solutionsCreated++;
			}

		}
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
	public void initialise() {
		BottomLevelCalculator.calculate(graph);
		
		// Create a crude upper bound for pruning
		int sequentialTime = 0;
		for (Vertex v : graph.vertexSet()) {
			sequentialTime += v.getWeight();
		}
		PartialSolution.setSequentialTime(sequentialTime);
		
		//Get all nodes of indegree 0
		HashSet<Vertex> startingVertices = new HashSet<>();
		for (Vertex v : graph.vertexSet()) {
			if (graph.inDegreeOf(v) == 0) {
				startingVertices.add(v);
			}
		}	
		
		PartialSolution.setStartingVertices(startingVertices);

		for (Vertex v : startingVertices) {
			if (v.incrementNumbUsed()) {
				visualisation.changeNodeColour(v.getName(), v.getNumbOfUse());
			}
			unexploredSolutions.add(new PartialSolution(graph, numProcessors, v, (byte)0));
		}
	}
	
	@Override
	protected boolean shouldRunSequentially() {
		//If running in parallel, it should stop running when enough solutions are created
		return !isParallel || unexploredSolutions.size() < (nThreads + SOLUTIONS_TO_CREATE);
	}

}
