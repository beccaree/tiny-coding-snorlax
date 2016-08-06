package scheduling_solution.solver;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

/**
 * Class to calculate the bottom level of each node in the input graph.
 * @author Kristy, Stefan
 */
public class BottomLevelCalculator {

	public static void calculate(GraphInterface<Vertex, DefaultWeightedEdge> directedGraph){
		Queue<Vertex> queue = new LinkedList<>();
		
		//Add all leaves to the queue, initialise their value to their bottom level as this is our starting point
		for (Vertex vertex: directedGraph.vertexSet()){
			if(directedGraph.outDegreeOf(vertex) == 0){
				queue.add(vertex);
				vertex.setBottomLevel(vertex.getWeight());
			}
		}
		
		//while the queue is not empty calculate the weight
		Vertex vertex, sourceVertex;
		ArrayList<DefaultWeightedEdge> removedEdges = new ArrayList<DefaultWeightedEdge>();
		while (!queue.isEmpty()){
			//get the head node from the queue
			vertex = queue.remove();
			//must use edgesOf() instead of incomingEdgesOf() as the latter throws a concurrentModificationException
			for (DefaultWeightedEdge e: directedGraph.edgesOf(vertex)){
				//get node from which edge comes from
				sourceVertex = directedGraph.getEdgeSource(e);
				sourceVertex.setBottomLevel(Math.max(sourceVertex.getBottomLevel(),vertex.getBottomLevel()+sourceVertex.getWeight()));
				
				//remove used edge
				removedEdges.add(directedGraph.removeEdge(sourceVertex, vertex));
				
				//if the source node is now a leaf then add it into the queue
				if(directedGraph.outDegreeOf(sourceVertex) == 0){
					queue.add(sourceVertex);
				}
			}
			
		}
		
		//add the removed edges back in. This will add them back in a different order than the initialised order
		for(DefaultWeightedEdge e : removedEdges) {
			directedGraph.addEdge(directedGraph.getEdgeSource(e), directedGraph.getEdgeTarget(e), e);
		}
		
		
		
	}
	
	
	
}
