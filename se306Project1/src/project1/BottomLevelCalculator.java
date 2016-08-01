package project1;
import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Queue;

import org.jgrapht.*;
import org.jgrapht.graph.DefaultDirectedGraph;
import org.jgrapht.graph.DefaultEdge;
import org.jgrapht.graph.DefaultWeightedEdge;

/**
 * Class is used to calculate the weight of the bottom levels
 * @author Kristy, Stefan
 *
 */
public class BottomLevelCalculator {
	
	//hashmap to store all the weights as graph can not store weights

	private void calculate (){
		
		DirectedGraph<String, DefaultWeightedEdge> directedGraph =
	            new DefaultDirectedGraph<String,  DefaultWeightedEdge>
	            ( DefaultWeightedEdge.class);
	
		Queue queue = new LinkedList<>();
		
		for ( String node: directedGraph.vertexSet()){
			if(directedGraph.outDegreeOf(node)==0){
				queue.add(node);
				//TODO create something that can store the bottom level weight
			}
		}
		
	}
	
	
	
}
