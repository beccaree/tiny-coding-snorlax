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
	 * @param v
	 * @return
	 */
	public VertexInfo getVertexInfo(Vertex v) {
		return vertexInfoMap.get(v);
	}
	
	public void addVertexSolution(Vertex v, VertexInfo vinfo) {
		vertexInfoMap.put(v, vinfo);
	}
}
