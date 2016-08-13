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
	
	int upperBound = 0;
	
	public int solutionsCreated = 0;
	
	public int pruned = 0;
	
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
	public PartialSolution calculateOptimalSolution() {
		
		for (Vertex v : graph.vertexSet()) {
			upperBound += v.getWeight();
		}
		
		//Get initial vertices of solution
		getStartStates();
		 
		 while (true) {
			 solutionsCreated++;
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
						solutionsCreated++;
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
	 * @return
	 */
	private boolean isComplete(PartialSolution p) {
		return p.getAllocatedVertices().size() == graph.vertexSet().size();
	}
	
	/**
	 * Calculates the start time of the given Vertex in the allocated processor
	 * Checks all parent nodes of the vertex and calculates when it would be able to start after that vertex.
	 * The maximum value is returned.
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
			int finishTime = processorTask.getStartTime() + sourceVertex.getWeight();
			if (processorTask.getProcessorNumber() != processorNumber) {
				finishTime += graph.getEdgeWeight(e);
			}
			if (finishTime > maxStartTime) {
				maxStartTime = finishTime;
			}
		}
		
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
	 * @return
	 */
	private boolean isViable(PartialSolution partialSolution) {
		//TODO the closed set doesnt prune that many? is equals() correct?
		if (exploredSolutions.contains(partialSolution) || (partialSolution.getTimeLength() + partialSolution.getMaxBottomLevel())>upperBound ) {
			pruned++;
			return false;
		}

		return true;
	}
	
	public PriorityQueue<PartialSolution> getUnexploredSolutions() {
		return unexploredSolutions;
	}

}

