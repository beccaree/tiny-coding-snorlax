package scheduling_solution.tools;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import com.sun.javafx.geom.Edge;

/**
 * Class is used to calculate the weight of the bottom levels
 * @author Kristy, Stefan
 *
 */
public class BottomLevelCalculator {

	public void calculate(GraphInterface<Vertex, DefaultWeightedEdge> directedGraph){
		Queue<Vertex> queue = new LinkedList<>();
		
		//Add all leaves to the queue, initialise their value to their bottom level as this is our starting point
		for (Vertex node: directedGraph.vertexSet()){
			if(directedGraph.outDegreeOf(node) == 0){
				queue.add(node);
				node.setBottomLevel(node.getWeight());
			}
		}
		
		//while the queue is not empty calculate the weight
		Vertex node, sourceNode, targetNode;
		ArrayList<DefaultWeightedEdge> removedEdges = new ArrayList<DefaultWeightedEdge>();
		while (!queue.isEmpty()){
			//get the head node from the queue
			node = queue.remove();
			//must use edgesOf() instead of incomingEdgesOf() as the latter throws a concurrentModificationException
			for (DefaultWeightedEdge e: directedGraph.edgesOf(node)){
				//get node from which edge comes from
				sourceNode = directedGraph.getEdgeSource(e);
				sourceNode.setBottomLevel(Math.max(sourceNode.getBottomLevel(),node.getBottomLevel()+sourceNode.getWeight()));
				
				//remove used edge
				removedEdges.add(directedGraph.removeEdge(sourceNode, node));
				
				//if the source node is now a leaf then add it into the queue
				if(directedGraph.outDegreeOf(sourceNode) == 0){
					queue.add(sourceNode);
				}
			}
			
		}
		
		//add the removed edges back in. This will add them back in a different order than the initialised order
		for(DefaultWeightedEdge e : removedEdges) {
			directedGraph.addEdge(directedGraph.getEdgeSource(e), directedGraph.getEdgeTarget(e), e);
		}
		
		
		
	}
	
	
	
}
