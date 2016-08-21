package scheduling_solution.astar;


import java.util.ArrayList;

import java.util.AbstractQueue;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.solver.BottomLevelCalculator;
import scheduling_solution.visualisation.GraphVisualisation;

/**
 * Standard sequential version of AStar
 */
public class AStarSeq {
	public GraphInterface<Vertex, DefaultWeightedEdge> graph;
	
	protected PriorityQueue<PartialSolution> unexploredSolutions;
	protected Set<PartialSolution> exploredSolutions;
	protected final byte numProcessors;
	
	public int solutionsPopped = 0;
	public int solutionsCreated = 0;
	public int solutionsPruned = 0;
	public long maxMemory = 0;
	
	public AStarSeq(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors) {
		this.graph = graph;
		unexploredSolutions = new PriorityQueue<>(1000, new PartialSolutionComparator());
		exploredSolutions = new HashSet<>();
		this.numProcessors = numProcessors;
	}
	
	/**
	 * Calculates the optimal solution
	 * @param graph - weighted digraph
	 * @return optimal PartialSolution object
	 */
	public PartialSolution calculateOptimalSolution() {
		
		
		initialise();

		while (true) {
			solutionsPopped++;
			
			/* Log memory for optimisation purposes */
			maxMemory = Math.max(maxMemory, Runtime.getRuntime().totalMemory());

			// priority list of unexplored solutions
			PartialSolution currentSolution = unexploredSolutions.poll();

			// check partial solution has all vertices allocated
			if (isComplete(currentSolution)) {
				return currentSolution;
			} else {
				for (Vertex v : currentSolution.getAvailableVertices()) {
					for (byte processor = 0; processor < numProcessors; processor++) {
						// add vertex into solution
						PartialSolution newSolution = new PartialSolution(
								graph, currentSolution, v, processor);
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
	}
	
	/**
	 * Initialises a crude upper bound (sequentialTime) as well as the starting
	 * vertices and solutions.
	 * The starting vertices and sequential time are stored statically in PartialSolution.java
	 */
	public void initialise() {
		BottomLevelCalculator.calculate(graph);
		
		// Create a crude upper bound for pruning
		int sequentialTime = 0;
		for (Vertex v : graph.vertexSet()) {
			sequentialTime += v.getWeight();
		}
		PartialSolution.setSequentialTime(sequentialTime);
		
		HashSet<Vertex> startingVertices = new HashSet<>();
		for (Vertex v : graph.vertexSet()) {
			if (graph.inDegreeOf(v) == 0) {
				startingVertices.add(v);
				
			}
		}	
		
		PartialSolution.setStartingVertices(startingVertices);

		for (Vertex v : startingVertices) {
			unexploredSolutions.add(new PartialSolution(graph, numProcessors, v, (byte)0));
		}
			
	}

	
	/**
	 * Checks if partial solution has allocated all vertices
	 * @param Partical solution to check
	 * @return	True - all vertices have been allocated
	 */
	public boolean isComplete(PartialSolution p) {
		return p.getUnallocatedVertices().size() == 0;
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
	public boolean isViable(PartialSolution partialSolution) {
		if (exploredSolutions.contains(partialSolution) || partialSolution.getMinimumFinishTime() > PartialSolution.getSequentialTime()/*||checkPermutations(exploredSolutions, partialSolution)*/ ) {
			solutionsPruned++;
			return false;
		}

		return true;
	}
	
	
	private boolean checkPermutations(Set<PartialSolution> exploredSolutions, PartialSolution newPartialSolution) {

		//Goes through the all the explored partial solutions
		for (PartialSolution explored : exploredSolutions) {
			
			//Goes through each individual processor for the current explored solution
			for(ArrayList<String> exploredSolutionProcessor : explored.ganttChart){
				System.out.println("new Solution");
				
				//Create an array containing all the processor numbers
				ArrayList<String> duplicateProcessor = new ArrayList<>();
				for (int i = 0; i<numProcessors;i++){
					duplicateProcessor.add(Integer.toString(i));
				}
				
				//Goes through all current non-duplicate processor
				//Check if any of the the processors are duplicates of the current explored solution processor
//				for (int i:duplicateProcessor){					
//					ArrayList<String> newSolutionProcessor = newPartialSolution.ganttChart.get(i);	
//					
//					//If they are the same, remove the processor number from the array
//					if (exploredSolutionProcessor.equals(newSolutionProcessor)){
//						duplicateProcessor.remove(i);
//					}	
//					
//				}
				
//				for (int i=0;i<duplicateProcessor.size();i++){					
//					ArrayList<String> newSolutionProcessor = newPartialSolution.ganttChart.get(i);	
					
					for(ArrayList<String> newSolutionProcessor: newPartialSolution.ganttChart){
						
						for (int i = 0; i<numProcessors;i++){
							duplicateProcessor.add(Integer.toString(i));
						}
											
						for (int i=0;i<duplicateProcessor.size();i++){	
						
						System.out.println("explored "+exploredSolutionProcessor);
						System.out.println("new sol "+newSolutionProcessor);
						System.out.println("dupliacate arry "+i+" has value "+duplicateProcessor.get(i));
						
						if (exploredSolutionProcessor.equals(newSolutionProcessor)&& !duplicateProcessor.get(i).equals("*")){
							duplicateProcessor.set(i, "*");
							System.out.println("changed to "+duplicateProcessor.get(i));
//							duplicateProcessor.remove(i);
							
						}							
					}
					
					//If they are the same, remove the processor number from the array
					
				}
				
				boolean match = true;
				
				for(String duplicate:duplicateProcessor){
					if(!duplicate.equals("*")){
						match = false;
						break;
					}
				}
				if(match){
				System.out.println("match");
				return true;
				}
				
				//If all processors were found to be a duplicate
				//new partial solution is a permutation of a previous explored solution
//				if(duplicateProcessor.isEmpty()){
//					return true;
//				}
			}
		}
		return false;
//		return true;
	}
	
	
	
}
