package scheduling_solution.astar;

import java.util.AbstractQueue;
import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

/**
 * Abstract class that contains all of the methods and fields needed by an astar
 * implementation. The only method which is left to subclasses is the
 * calculateOptimalSolution() method
 * 
 * @author Stefan
 *
 */
public abstract class AStar {

	public GraphInterface<Vertex, DefaultWeightedEdge> graph;
	protected PriorityQueue<PartialSolution> unexploredSolutions;
	protected Set<PartialSolution> exploredSolutions;
	protected byte numProcessors;
	
	public AStar(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors) {
		this.graph = graph;
		this.numProcessors = numProcessors;
	}

	public AbstractQueue<PartialSolution> getUnexploredSolutions() {
		return unexploredSolutions;
	}

	/**
	 * Creates fresh copies of the open and closed set
	 */
	protected void initialiseDataStructures() {
		this.unexploredSolutions = new PriorityQueue<>(1000, new PartialSolutionComparator());
		this.exploredSolutions = new HashSet<>();
	}

	
	/**
	 * Calculates the optimal solution and returns it
	 * @return
	 */
	public PartialSolution calculateOptimalSolution() {
		while (shouldRunSequentially()) {
			PartialSolution currentSolution = unexploredSolutions.poll();
//			solutionsPopped++;

			// Check if partial solution contains all the vertices
			if (isComplete(currentSolution)) {
				return currentSolution;
			} else {
				expandPartialSolution(currentSolution);
			}
		}
		
		/*This will be reached in the parallel version, as shouldRunSequentially() 
		  will return false after N iterations*/
		return null;
	}

	/**
	 * Method that should be overridden by subclasses which indicates
	 * when to break the while loop in calculateOptimalSolution
	 * @return
	 */
	protected abstract boolean shouldRunSequentially();
	
	/**
	 * Checks if partial solution has allocated all vertices
	 * 
	 * @param Partial
	 *            solution to check
	 * @return True - all vertices have been allocated
	 */
	public boolean isComplete(PartialSolution p) {
		return p.getUnallocatedVertices().size() == 0;
	}

	/**
	 * Checks to see if a solution has no chance of being an optimal solution,
	 * using all pruning/bound checks Can get a simple upper bound by adding all
	 * vertices together (== running them all sequentially on one processor)
	 * Should check if it exists in the exploredSolutions Set
	 * 
	 * @param partialSolution
	 * @return True - if the given ParticalSolution has a chance of being an
	 *         optimal solution
	 */
	protected boolean isViable(PartialSolution partialSolution) {
		if (exploredSolutions.contains(partialSolution)
				|| partialSolution.getMinimumFinishTime() > PartialSolution.getSequentialTime()) {
//			solutionsPruned++;
			return false;
		}

		return true;
	}

	/**
	 * Creates all viable children of a partialsolution and adds then to the
	 * unexplored queue. Once all children are created, the solution can be 
	 * added to the explored set.
	 * @param solution
	 */
	protected void expandPartialSolution(PartialSolution solution) {
		for (Vertex v : solution.getAvailableVertices()) {
			for (byte processor = 0; processor < numProcessors; processor++) {
				
//				solutionsCreated++;
				
				createViableSolution(solution, v, processor);

				// Only adds vertex to leftmost empty processor
				if (solution.getFinishTimes()[processor] == 0) {
					break;
				}

			}
		}
		exploredSolutions.add(solution);
	}
	
	
	/**
	 * Creates a child solution, but does not add it to the priority queue if it
	 * does not pass the pruning check
	 * @param solution
	 * @param v
	 * @param processor
	 */
	protected void createViableSolution(PartialSolution solution, Vertex v, byte processor) {
		PartialSolution newSolution = new PartialSolution(graph, solution, v, processor);
		// Only add the solution to the priority queue if it passes the pruning check
		if (isViable(newSolution)) {
			unexploredSolutions.add(newSolution);
		}
	}
	
}
