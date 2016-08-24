package scheduling_solution.basic_milestone_solver;

import java.util.HashMap;

import scheduling_solution.graph.Vertex;

/**
 * Used in Topological Sort for the basic milestone solution
 * Contains a HashMap of vertices and their corresponding solutions
 * @author Team 8
 */
public class Solution {
	
	private HashMap<Vertex, VertexInfo> vertexInfoMap;
	
	public Solution() {
		vertexInfoMap = new HashMap<>();
	}
		
	/**
	 * Returns vertex info for given vertex
	 * @param v - vertex to get information for
	 * @return Info of given vertex
	 */
	public VertexInfo getVertexInfo(Vertex v) {
		return vertexInfoMap.get(v);
	}
	
	/**
	 * Add vertex and it's info to be stored
	 * @param v - vertex
	 * @param vinfo - vertex information
	 */
	public void addVertexSolution(Vertex v, VertexInfo vinfo) {
		vertexInfoMap.put(v, vinfo);
	}
}
