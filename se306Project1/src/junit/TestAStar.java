package junit;

import java.util.HashSet;
import java.util.List;
import java.util.PriorityQueue;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import scheduling_solution.astar.AStar;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.JGraphTAdapter;
import scheduling_solution.graph.Vertex;

public class TestAStar extends TestCase {
	
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;
	private PriorityQueue<PartialSolution> unexploredSolutions;
	private static final int NUM_VERTICES= 10;


	@Before
	public void setUp() {
		graph = new JGraphTAdapter<>(DefaultWeightedEdge.class);
	}
	
	@After
	public void tearDown() {
		graph = null;
		unexploredSolutions = null;
	}
	
	@Test//Need to test calculateStartTime, getStartState and isComplete
	public void testSmallGraphAStar() {
		
		/*Create a graph with 4 vertices. */
		Vertex vertexa = new Vertex("a", 2);
		graph.addVertex(vertexa);
		Vertex vertexb = new Vertex("b", 3);
		graph.addVertex(vertexb);
		Vertex vertexc = new Vertex("c", 3);
		graph.addVertex(vertexc);
		Vertex vertexd = new Vertex("d", 2);
		graph.addVertex(vertexd);
		
		/*Adds edges between vertices*/
		DefaultWeightedEdge edgeab = graph.addEdge(vertexa, vertexb);
		graph.setEdgeWeight(edgeab, 1);
		DefaultWeightedEdge edgeac = graph.addEdge(vertexa, vertexc);
		graph.setEdgeWeight(edgeac, 2);
		DefaultWeightedEdge edgebd = graph.addEdge(vertexb, vertexd);
		graph.setEdgeWeight(edgebd, 2);
		DefaultWeightedEdge edgecd = graph.addEdge(vertexc, vertexd);
		graph.setEdgeWeight(edgecd, 1);
		
		AStar astar = new AStar(graph, 2);
		
		//Checks if the number of start states is what is expected
		astar.getStartStates();
		unexploredSolutions = astar.getUnexploredSolutions();
		assertEquals("Number of start states is different than what was expected", 1, unexploredSolutions.size());
		
		//Checks that the start state is vertex "a"
		HashSet<Vertex> vertices = unexploredSolutions.poll().getAllocatedVertices();
		assertEquals("GetStartStates returns wrong vertex", true, vertices.contains(vertexa));
	}
	
	@Test
	public void test() {
		
		Vertex[] vertexArray = new Vertex[NUM_VERTICES];
		
		for(int i = 0; i < NUM_VERTICES; i++) {
			vertexArray[i] = new Vertex(String.valueOf(i), i);
			graph.addVertex(vertexArray[i]);
		}
		
		AStar astar = new AStar(graph, 2);
		
		//Gets start states and checks if they are accurate
		astar.getStartStates();
		unexploredSolutions = astar.getUnexploredSolutions();
		assertEquals("Number of start states is different than what was expected", 10, unexploredSolutions.size());
	}
}
