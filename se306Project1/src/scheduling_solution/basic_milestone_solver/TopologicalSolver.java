package scheduling_solution.basic_milestone_solver;

import java.util.List;
import scheduling_solution.graph.Vertex;

/**
 * This class creates a valid solution based on a topological sort of the vertices in the graph.
 * As it allocates all vertices to the first processor, no edge weights need to be taken into account
 * and the startTime of a vertex will only be based on the weights of the vertices allocated before it
 */
public class TopologicalSolver {
	
	/**
	 * Solve adds vertices with their relevant string information for the output file
	 * into the vertexInfoMap.
	 * @param topologicalSort - vertex in topological sort order
	 * @return topological sort solution 
	 */
	public static Solution solve(List<Vertex> topologicalSort) {
		Solution solution = new Solution();
		int startTime = 0;
		
		//Allocate all vertex into one processor
		for (Vertex v : topologicalSort) {
			allocateToProcessor(v, 1, startTime, solution);
			startTime += v.getWeight();
		}		
		return solution;
	}
	
	/**
	 * Uses VertexInfo to format the vertex solution string
	 * and store it with the corresponding vertex
	 * @param v  - Vertex to add
	 * @param processor - Processor to store vertex into
	 * @param startTime - Start time of vertex
	 * @param solution - Solution to add vertex into
	 */
	private static void allocateToProcessor(Vertex v, int processor,int startTime, Solution solution) {
		VertexInfo vinfo = new VertexInfo(startTime, processor);
		solution.addVertexSolution(v, vinfo);
	}
}
