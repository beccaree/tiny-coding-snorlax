package scheduling_solution.branch_and_bound;

import java.util.HashSet;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

public class PartialSolution extends scheduling_solution.astar.PartialSolution {
	
	public PartialSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, Vertex v,
			byte processorNumber) {
		super(graph, numProcessors, v, processorNumber);
	}

	public PartialSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph,
			scheduling_solution.astar.PartialSolution partialSolution, Vertex v, byte processorNumber) {
		super(graph, partialSolution, v, processorNumber);
	}
	
	public void calculateMinimumFinishTime(PartialSolution p, Vertex v) {
		int maxHeuristic = Math.max(p.getMinimumFinishTime(), allocatedVertices.get(v).getStartTime() + v.getBottomLevel());
		maxHeuristic = Math.max(maxHeuristic, ((BranchAndBound.getSequentialTime() + totalIdleTime) / numProcessors));
//		maxHeuristic = Math.max(maxHeuristic, calculateEarliestUnallocatedVertexFinishTime());
		minimumFinishTime = maxHeuristic;
	}

	@SuppressWarnings("unchecked")
	protected void getStartingVertices() {
		availableVertices = (HashSet<Vertex>) BranchAndBound.getStartVertices().clone(); //Not very object oriented either
	}
}
