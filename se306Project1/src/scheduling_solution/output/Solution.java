package scheduling_solution.output;

import java.util.HashMap;

import graph.Vertex;
import scheduling_solution.solver.VertexInfo;

/**
 * Contains a HashMap of vertices and their corresponding solutions
 * @author sabflik
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
