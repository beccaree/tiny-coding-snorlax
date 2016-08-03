package scheduling_solution.output;

import java.util.HashMap;
import scheduling_solution.tools.Vertex;
import scheduling_solution.tools.VertexInfo;

/**
 * Contains a HashMap of vertices and their corresponding solutions
 * @author sabflik
 */
public class Solution {
	
	private static HashMap<Vertex, VertexInfo> map;
	
	public static void setMap(HashMap<Vertex, VertexInfo> map) {
		Solution.map = map;
	}
	
	public static HashMap<Vertex, VertexInfo> getMap() {
		return map;
	}	
	
	/**
	 * Returns vertex info for given vertex
	 * @param v
	 * @return
	 */
	public static VertexInfo getVertexInfo(Vertex v) {
		return map.get(v);
	}
}
