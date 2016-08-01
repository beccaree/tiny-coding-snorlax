package project1;
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
	
	/**
	 * Method stores all bottom level weight and name
	 */
	private void calculate(){
		
		DirectedGraph<Vertex, DefaultWeightedEdge> directedGraph =
	            new DefaultDirectedGraph<Vertex,  DefaultWeightedEdge>
	            ( DefaultWeightedEdge.class);
	
		Queue<Vertex> queue = new LinkedList<>();
		
		//Add all leafs to the queue
		for ( Vertex node: directedGraph.vertexSet()){
			if(directedGraph.outDegreeOf(node)==0){
				queue.add(node);
			}
		}
		
		//while the queue is not empty calculate the weight
		while (!queue.isEmpty()){
			//get the head node from the queue
			Vertex node = queue.remove();
			for (DefaultWeightedEdge e: directedGraph.incomingEdgesOf(node)){
				
				//get node from which edge comes from
				Vertex sourceNode =directedGraph.getEdgeSource(e);
				sourceNode.SetBottomLevel(Math.max(sourceNode.getBottomLevel(),(node.getBottomLevel()+node.getWeight())));
				
				//remove used edge
				directedGraph.removeEdge(node, sourceNode);
				
				//if the source node is now a leaf then add it into the queue
				if(directedGraph.outDegreeOf(sourceNode)==0){
					queue.add(sourceNode);
				}
			}
			
		}
		
	}
	
	
	
}
