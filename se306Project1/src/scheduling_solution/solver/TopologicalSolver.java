package scheduling_solution.solver;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.output.Solution;

/**
 * This class creates a valid solution based on a topological sort of the vertices in the graph.
 * As it allocates all vertices to the first processor, no edge weights need to be taken into account
 * and the startTime of a vertex will only be based on the weights of the vertices allocated before it
 * @author Team 8
 */
public class TopologicalSolver {
	
	public static Solution solve(List<Vertex> topologicalSort, GraphInterface<Vertex, DefaultWeightedEdge> graph) {
		Solution solution = new Solution();
		int startTime = 0;
		
		for (Vertex v : topologicalSort) {
			allocateToProcessor(v, 1, startTime, solution);
			startTime += v.getWeight();
		}
		
		return solution;
	}
	
	private static void allocateToProcessor(Vertex v, int processor,int startTime, Solution solution) {
		VertexInfo vinfo = new VertexInfo(startTime, processor);
		solution.addVertexSolution(v, vinfo);
	}
}
