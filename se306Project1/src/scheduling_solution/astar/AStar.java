package scheduling_solution.astar;

import java.util.Collections;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import java.util.concurrent.PriorityBlockingQueue;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.visualisation.GraphVisualisation;

public class AStar {
	public GraphInterface<Vertex, DefaultWeightedEdge> graph;
	public static HashSet<Vertex> startingVertices;
	
	PriorityBlockingQueue<PartialSolution> unexploredSolutions;
	Set<PartialSolution> exploredSolutions = Collections.synchronizedSet(new HashSet<PartialSolution>());
	final byte numProcessors;
	
	public static int sequentialTime = 0;
	
	public int solutionsPopped = 0;
	public int solutionsCreated = 0;
	public int solutionsPruned = 0;
	public long maxMemory = 0;
	
	public AStar(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors) {
		this.graph = graph;
		unexploredSolutions = new PriorityBlockingQueue<>(1000, new PartialSolutionComparator());
		exploredSolutions = new HashSet<>();
		startingVertices = new HashSet<>();
		this.numProcessors = numProcessors;
	}
	
	/**
	 * Calculates the optimal solution
	 * @param graph - weighted digraph
	 * @return optimal PartialSolution object
	 */
	public PartialSolution calculateOptimalSolution(int nThreads) {
		
		PartialSolution[] resultList = new PartialSolution[nThreads];
		
		// Create a crude upper bound for pruning
		for (Vertex v : graph.vertexSet()) {
			sequentialTime += v.getWeight();
		}
		
		//Get initial vertices of solution
		initialiseStartingVertices();
		initialiseStartStates();

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
	
	
	/**
	 * Initialises the PriorityQueue with the possible starting states
	 */
	public void initialiseStartStates() {
		for (Vertex v : startingVertices) {
			unexploredSolutions.add(new PartialSolution(graph, numProcessors, v, (byte)0));//TODO is it ok to add them all to processor 0? Pretty sure it is
		}
	}
	
	public void initialiseStartingVertices() {
		for (Vertex v : graph.vertexSet()) {
			if (graph.inDegreeOf(v) == 0) {
				startingVertices.add(v);
			}
		}
	}
	
	
	
	public PriorityBlockingQueue<PartialSolution> getUnexploredSolutions() {
		return unexploredSolutions;
	}
	
	/*Not very object-oriented, but saves time due to not having to calculate it multiple times*/
	public static int getSequentialTime() {
		return sequentialTime;
	}
	
	
	/**
	 * Checks to see if a solution has no chance of being an optimal solution, using all pruning/bound checks
	 * Can get a simple upper bound by adding all vertices together (== running them all sequentially on one processor)
	 * Should check if it exists in the exploredSolutions Set
	 * @param partialSolution
	 * @return True - if the given ParticalSolution has a chance of being an optimal solution
	 */
	
}

