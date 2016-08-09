package scheduling_solution.solver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

/**
 * BottomLevelCalculator calculates the bottom level of each node in the input graph.
 * @author Team 8
 */
public class BottomLevelCalculator {

	public static void calculate(GraphInterface<Vertex, DefaultWeightedEdge> directedGraph){
		Queue<Vertex> queue = new LinkedList<>();
		
		//Add all leaves to the queue, initialize their value to their bottom level as this is our starting point
		for (Vertex vertex: directedGraph.vertexSet()){
			if(directedGraph.outDegreeOf(vertex) == 0){
				queue.add(vertex);
				vertex.setBottomLevel(vertex.getWeight());
			}
		}
		
		//While the queue is not empty calculate the weight
		Vertex vertex, sourceVertex;
		ArrayList<DefaultWeightedEdge> removedEdges = new ArrayList<DefaultWeightedEdge>();
		while (!queue.isEmpty()){
			//Get the head node from the queue
			vertex = queue.remove();
			//Use edgesOf() instead of incomingEdgesOf() or else it throws a concurrentModificationException
			for (DefaultWeightedEdge e: directedGraph.edgesOf(vertex)){
				//Get node from which e(edge) comes from
				sourceVertex = directedGraph.getEdgeSource(e);
				sourceVertex.setBottomLevel(Math.max(sourceVertex.getBottomLevel(),vertex.getBottomLevel()+sourceVertex.getWeight()));
				
				//Remove and store removed edges in array list
				removedEdges.add(directedGraph.removeEdge(sourceVertex, vertex));
				
				//If the source node is now a leaf then add it into the queue
				if(directedGraph.outDegreeOf(sourceVertex) == 0){
					queue.add(sourceVertex);
				}
			}			
		}
		
		//Add the removed edges back in 
		//This will add them back in a different order than the initialized order
		for(DefaultWeightedEdge e : removedEdges) {
			directedGraph.addEdge(directedGraph.getEdgeSource(e), directedGraph.getEdgeTarget(e), e);
		}		
	}
}
