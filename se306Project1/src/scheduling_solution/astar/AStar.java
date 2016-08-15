package scheduling_solution.astar;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

public class AStar {
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;
	PriorityQueue<PartialSolution> unexploredSolutions;
	Set<PartialSolution> exploredSolutions;
	final byte numProcessors;
	
	private static int sequentialTime = 0;
	
	public int solutionsPopped = 0;
	public int solutionsCreated = 0;
	public int solutionsPruned = 0;
	public long maxMemory = 0;
	
	public AStar(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors) {
		this.graph = graph;
		unexploredSolutions = new PriorityQueue<>(new PartialSolutionComparator());
		exploredSolutions = new HashSet<>();
		this.numProcessors = numProcessors;
	}
	
	/**
	 * Calculates the optimal solution
	 * @param graph - weighted digraph
	 * @return optimal PartialSolution object
	 */
	public PartialSolution calculateOptimalSolution() {
		
		for (Vertex v : graph.vertexSet()) {
			sequentialTime += v.getWeight();
		}
		
		//Get initial vertices of solution
		getStartStates();
		 
		 while (true) {
			solutionsPopped++;
			
			 //priority list of unexplored solutions
			PartialSolution currentSolution = unexploredSolutions.poll();
			
			//check partial solution has all vertices allocated
			if (isComplete(currentSolution)) {
				return currentSolution;
			} else {
				for (Vertex v : currentSolution.getAvailableVertices()) {
					for (int processor= 0; processor < numProcessors; processor++) {
						//Get the start time of the new vertex that is too be added to solution
						int startTime = calculateStartTime(currentSolution, v, processor);
						
						//add vertex into solution
						PartialSolution newSolution = new PartialSolution(graph, currentSolution, v, processor, startTime);
						
						//Store the greatest total amount of memory 
						long mem = Runtime.getRuntime().totalMemory();
						if (mem > maxMemory) {
							maxMemory = mem;
						}
						solutionsCreated++;
						
						//Check if new solution is worth further exploring
						if (isViable(newSolution)) {
							unexploredSolutions.add(newSolution);
						}
					}
				}
				exploredSolutions.add(currentSolution);
			}
			
		 }
	}

	/**
	 * Initialises the PriorityQueue with the possible starting states
	 */
	public void getStartStates() {
		for (Vertex v : graph.vertexSet()) {
			if (graph.inDegreeOf(v) == 0) {
				unexploredSolutions.add(new PartialSolution(graph, numProcessors, v, 0));//TODO is it ok to add them all to processor 1?
			}
		}
		
	}
	
	/**
	 * Checks if partial solution has allocated all vertices
	 * @param Partical solution to check
	 * @return	True - all vertices have been allocated
	 */
	public boolean isComplete(PartialSolution p) {
		return p.getAllocatedVertices().size() == graph.vertexSet().size();
		
	}
	
	public PriorityQueue<PartialSolution> getUnexploredSolutions() {
		return unexploredSolutions;
	}
	
	/*Not very object-oriented*/
	public static int getSequentialTime() {
		return sequentialTime;
	}
	
	
	/**
	 * Calculates the start time of the given Vertex in the allocated processor
	 * Checks all parent nodes of the vertex and calculates when it would be able to start after that vertex.
	 * The maximum value is returned.
	 * @param partialSolution	Solution thus far
	 * @param v					Vertex to find start time for
	 * @param processorNumber	Processor allocated to
	 * @return start time of the given vertex
	 */
	public int calculateStartTime(PartialSolution partialSolution, Vertex v, int processorNumber) {
		//great start time value for vertex v
		int maxStartTime = 0;
		
		//Go through all the Source vertex (vertices that vertex v must be executed after)
		for (DefaultWeightedEdge e : graph.incomingEdgesOf(v)) {
			Vertex sourceVertex = graph.getEdgeSource(e);
			
			//Find the greatest finish time of out of all the source vertices
			ProcessorTask processorTask = partialSolution.getTask(sourceVertex);
			int finishTime = processorTask.getStartTime() + sourceVertex.getWeight();	
			//If the process to be added is not on the same processor, add the edge weight
			if (processorTask.getProcessorNumber() != processorNumber) {
				finishTime += graph.getEdgeWeight(e);
			}
			if (finishTime > maxStartTime) {
				maxStartTime = finishTime;
			}
		}
		//check if finish time of the processor is greater than greatest start time value of vertex v
		int processorFinishTime = partialSolution.getProcessor(processorNumber).getFinishTime();		
		if (processorFinishTime > maxStartTime) {
			maxStartTime = processorFinishTime;
		}
		
		return maxStartTime;
	}
	
	/**
	 * Checks to see if a solution has no chance of being an optimal solution, using all pruning/bound checks
	 * Can get a simple upper bound by adding all vertices together (== running them all sequentially on one processor)
	 * Should check if it exists in the exploredSolutions Set
	 * @param partialSolution
	 * @return True - if the given ParticalSolution has a chance of being an optimal solution
	 */
	private boolean isViable(PartialSolution partialSolution) {
		//Check if solution has already been explored or if the minimum time of solution is greater than the current time
		if (exploredSolutions.contains(partialSolution) || partialSolution.getMinimumTime() > sequentialTime ) {
			solutionsPruned++;
			return false;
		}

		return true;
	}
	
}

