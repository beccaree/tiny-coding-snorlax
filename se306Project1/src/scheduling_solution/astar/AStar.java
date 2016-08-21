package scheduling_solution.astar;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.visualisation.GraphVisualisation;

public class AStar {
	public GraphInterface<Vertex, DefaultWeightedEdge> graph;
	public static HashSet<Vertex> startingVertices;
	
	PriorityQueue<PartialSolution> unexploredSolutions;
	Set<PartialSolution> exploredSolutions;
	final byte numProcessors;
	
	public static int sequentialTime = 0;
	
	public int solutionsPopped = 0;
	public int solutionsCreated = 0;
	public int solutionsPruned = 0;
	public long maxMemory = 0;
	
	public AStar(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors) {
		this.graph = graph;
		unexploredSolutions = new PriorityQueue<>(1000, new PartialSolutionComparator());
		exploredSolutions = new HashSet<>();
		startingVertices = new HashSet<>();
		this.numProcessors = numProcessors;
	}
	
	/**
	 * Calculates the optimal solution
	 * @param graph - weighted digraph
	 * @return optimal PartialSolution object
	 */
	public PartialSolution calculateOptimalSolution() {
		
		// Create a crude upper bound for pruning
		for (Vertex v : graph.vertexSet()) {
			sequentialTime += v.getWeight();
		}
		
		//Get initial vertices of solution
		initialiseStartingVertices();
		initialiseStartStates();

		while (true) {
			solutionsPopped++;

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

						/* Log memory for optimisation purposes */
						long mem = Runtime.getRuntime().totalMemory();
						if (mem > maxMemory) {
							maxMemory = mem;
						}
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
	
	/**
	 * Checks if partial solution has allocated all vertices
	 * @param Partical solution to check
	 * @return	True - all vertices have been allocated
	 */
	public boolean isComplete(PartialSolution p) {
		return p.getUnallocatedVertices().size() == 0;
	}
	
	public PriorityQueue<PartialSolution> getUnexploredSolutions() {
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
	public boolean isViable(PartialSolution partialSolution) {
		if (exploredSolutions.contains(partialSolution) || partialSolution.getMinimumFinishTime() > sequentialTime/*||checkPermutations(exploredSolutions, partialSolution)*/ ) {
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

