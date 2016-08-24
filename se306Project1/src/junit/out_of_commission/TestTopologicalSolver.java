package junit.out_of_commission;

import java.util.ArrayList;
import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.After;
import org.junit.Before;

import junit.framework.TestCase;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.JGraphTAdapter;
import scheduling_solution.graph.Vertex;
import scheduling_solution.output.Solution;
import scheduling_solution.solver.TopologicalSolver;
import scheduling_solution.solver.VertexInfo;

/**
 * This class tests that the topological solver returns the correct time to finish if every vertex
 * is ran on the same processor: the time taken should be equal to the sum of the vertex weights
 *
 */
public class TestTopologicalSolver extends TestCase {
	
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;
	private List<Vertex> topologicalSort;
	
	private static final int NUM_VERTICES= 10;
	
	@Before
	public void setUp() {
		graph = new JGraphTAdapter<>(DefaultWeightedEdge.class);
		topologicalSort = new ArrayList<>();
	}
	
	@After
	public void tearDown() {
		graph = null;
		topologicalSort = null;
	}
	
	public void testTopologicalSolution() {
		Vertex[] vertexArray = new Vertex[NUM_VERTICES];
		int sumOfWeights = 0, timeTaken = 0;
		
		/*Create a graph with NUM_VERTICES disconnected vertices, and create a topological sort with those vertices 
		 * Additionally, sum the weights of the vertices here. */
		for(int i = 0; i < NUM_VERTICES; i++) {
			vertexArray[i] = new Vertex(String.valueOf(i), i);
			graph.addVertex(vertexArray[i]);
			topologicalSort.add(vertexArray[i]);
			sumOfWeights += i;
		}
		
		Solution solution = TopologicalSolver.solve(topologicalSort);
		
		//Get the maximum *finishing* time of all nodes
		for (int i = 0; i < NUM_VERTICES; i++) {
			Vertex v = vertexArray[i];
			VertexInfo vinfo = solution.getVertexInfo(v);
			int finishTime = vinfo.getStartTime() + v.getWeight();
			if (finishTime > timeTaken) { 
				timeTaken = finishTime;
			}
		}
		
		//The latest finishing time should be = to the sum of vertex weights
		assertEquals("Time taken was not equal to the time to run every node sequentially", sumOfWeights, timeTaken);
	}
}
