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
	final int numProcessors;
	
	
	public AStar(GraphInterface<Vertex, DefaultWeightedEdge> graph, int numProcessors) {
		this.graph = graph;
		unexploredSolutions = new PriorityQueue<>(new PartialSolutionComparator());
		exploredSolutions = new HashSet<>();
		this.numProcessors = numProcessors;
	}
	
	/**
	 * Calculates the optimal solution
	 * @param graph - weighted digraph
	 * @return 
	 */
	public PartialSolution calculateOptimalSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph) {
		
		//Get initial vertices of solution
		getStartStates();
		 
		 while (true) {
			 //priority list of unexplored solutions
			PartialSolution currentSolution = unexploredSolutions.poll();
			
			//check partial solution has all vertices allocated
			if (isComplete(currentSolution)) {
				return currentSolution;
			} else {
				for (Vertex v : currentSolution.getAvailableVertices()) {
					for (int processor= 1; processor < numProcessors; processor++) {
						//Get the start time of the new vertex that is too be added to solution
						int startTime = calculateStartTime(currentSolution, v, processor);
						
						//add vertex into solution
						PartialSolution newSolution = new PartialSolution(currentSolution, v, processor, startTime);
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
	private void getStartStates() {
		for (Vertex v : graph.vertexSet()) {
			if (graph.inDegreeOf(v) == 0) {
				unexploredSolutions.add(new PartialSolution(numProcessors, v, 1));//TODO is it ok to add them all to processor 1?
			}
		}
		
	}
	
	/**
	 * Checks if partial solution has allocated all vertices
	 * @param Partical solution to  check
	 * @return
	 */
	private boolean isComplete(PartialSolution p) {
		return true; //TODO
	}
	
	/**
	 * Calculates the start time of the given Vertex in the allocated processor
	 * @param partialSolution	Solution thus far
	 * @param v					Vertex to find start time for
	 * @param processorNumber	Processor allocated to
	 * @return
	 */
	private int calculateStartTime(PartialSolution partialSolution, Vertex v, int processorNumber) {
		int maxStartTime = 0;
		for (DefaultWeightedEdge e : graph.incomingEdgesOf(v)) {
			Vertex sourceVertex = graph.getEdgeSource(e);
			ProcessorTask processorTask = partialSolution.getTask(sourceVertex);
			int startTime = processorTask.getStartTime();
			if (processorTask.getProcessorNumber() != processorNumber) {
				startTime += graph.getEdgeWeight(e);
			}
			if (startTime > maxStartTime) {
				maxStartTime = startTime;
			}
		}
		
		return maxStartTime + v.getWeight();
	}
	
	private boolean isViable(PartialSolution partialSolution) {
		//This method will be large: will do all checks to see if a solution has no chance to be optimal using all pruning/bound checks
		//Can get a simple upper bound just by adding all vertices together (== running them all sequentially on one processor)
		return true;
	}
	
	

}

