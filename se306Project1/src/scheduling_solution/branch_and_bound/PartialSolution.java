package scheduling_solution.branch_and_bound;

import java.util.HashSet;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

/**
 * Partial solution class used by the Branch and Bound Algorithm
 */
public class PartialSolution extends scheduling_solution.astar.PartialSolution {
	
	/**
	 * Initialise the first partial solution using a starting vertex
	 * @param graph - digraph of tasks and their dependencies
	 * @param numProcessors - number of processors 
	 * @param v - starting vertex (has not parent vertices)
	 * @param processorNumber - processor to allocate vertex to
	 */
	public PartialSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, Vertex v,
			byte processorNumber) {
		super(graph, numProcessors, v, processorNumber);
	}

	/**
	 * Adds the given vertex into an existing partial solution
	 * @param graph - digraph of tasks and their dependencies
	 * @param partialSolution - partial solution to add vertex to
	 * @param v - vertex to be added in
	 * @param processorNumber- processor to allocate vertex to
	 */
	public PartialSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph,
			scheduling_solution.astar.PartialSolution partialSolution, Vertex v, byte processorNumber) {
		super(graph, partialSolution, v, processorNumber);
	}
	
	/**
	 * Use heuristics to calculate the partial solution's estimated finish time
	 * Results are used to order solutions in the priority queue 
	 */
	public void calculateMinimumFinishTime(PartialSolution p, Vertex v) {
		int maxHeuristic = Math.max(p.getMinimumFinishTime(), getAllocatedVertices().get(v).getStartTime() + v.getBottomLevel());
		maxHeuristic = Math.max(maxHeuristic, ((BranchAndBound.getSequentialTime() + getTotalIdleTime() / numProcessors)));
//		maxHeuristic = Math.max(maxHeuristic, calculateEarliestUnallocatedVertexFinishTime());
		minimumFinishTime = maxHeuristic;
	}

	@SuppressWarnings("unchecked")
	protected void getStartingVertices() {
		availableVertices = (HashSet<Vertex>) BranchAndBound.getStartVertices().clone(); //Not very object oriented either
	}
}
