package scheduling_solution.tools;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Basic milestone:
 * DFS creates a topological sort of a directed graph 
 * @author Team 8
 */
public class DFS {
	
	private static List<Vertex> topologicalSort = new ArrayList<Vertex>();	
	private static Deque<Vertex> vertexStack = new ArrayDeque<Vertex>();
	//For final milestone
	public GraphInterface<Vertex, DefaultWeightedEdge> outputDirectedGraph = new JGraphTAdapter<>(DefaultWeightedEdge.class);
	
	/**
	 * calculate creates a topological sort of all the vertices in a directed graph
	 * @param directedGraph
	 * @return
	 */
	public static List<Vertex> calculate(GraphInterface<Vertex, DefaultWeightedEdge> directedGraph){
		
		//Iterate through all the vertices to find "start nodes"
		// i.e. vertices with no incoming edges 
		for (Vertex vertex: directedGraph.vertexSet()){
			if(directedGraph.inDegreeOf(vertex) == 0){
				vertexStack.add(vertex);
			}
		}
		//Iterate through "start nodes"
		while(!vertexStack.isEmpty()){
			Vertex currentVertex = vertexStack.pop();
			topologicalSort.add(currentVertex);
			
			//Remove edges (i.e outgoing edges) to child vertices
			for (DefaultWeightedEdge e: directedGraph.edgesOf(currentVertex)){
				directedGraph.removeEdge(e);
				Vertex targetVertex = directedGraph.getEdgeTarget(e);
				if(directedGraph.inDegreeOf(targetVertex)==0){
					vertexStack.add(targetVertex);
				}				
			}	
		}
		return topologicalSort;
	}
}
