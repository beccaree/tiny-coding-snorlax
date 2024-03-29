package junit;

import java.util.HashSet;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import scheduling_solution.astar.AStarSequential;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.JGraphTAdapter;
import scheduling_solution.graph.Vertex;

public class TestPartialSolution extends TestCase {
	
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;
	private static final int NUM_VERTICES= 10;

	@Before
	protected void setUp() {
		graph = new JGraphTAdapter<>(DefaultWeightedEdge.class);
	}

	@After
	protected void tearDown() {
		graph = null;
	}
	
	@Test
	public void testSingleVertexPartialSolution() {
		
		Vertex v = new Vertex("1", 10);
		/*PARTIALSOLUTION FAILS CONSTRUCTION BECAUSE IT HAS NO INCOMING/OUTGOING EDGES*/
		graph.addVertex(v);
		AStarSequential astar = new AStarSequential(graph, (byte) 2);
		astar.initialise();
		PartialSolution pSolution = new PartialSolution(graph, (byte)2, v, (byte)1);
		
		//The finish time of the current partial solution must be equal to the weight of the vertex
		assertEquals("Finish time of current partial solution is incorrect", 
				10, pSolution.getMinimumFinishTime());
		
		//The number of available vertices must be 0 as there aren't any more vertices left
		HashSet<Vertex> vertices = pSolution.getAvailableVertices();
		assertEquals("Incorrect number of available vertices", 0, vertices.size());
	}
	
	@Test
	public void testIndependentVertexPartialSolution() {
		
		Vertex[] vertexArray = new Vertex[NUM_VERTICES];
		//Create a graph with 10 independent vertices
		for(int i = 0; i < NUM_VERTICES; i++) {
			vertexArray[i] = new Vertex(String.valueOf(i), i);
			graph.addVertex(vertexArray[i]);
		}
		
		/*Test the starting vertex*/
		AStarSequential astar = new AStarSequential(graph, (byte) 3);
		astar.initialise();
		PartialSolution pSolution = new PartialSolution(graph, (byte) 3, vertexArray[0], (byte) 0);
		//Finishing time of the starting partial solution should be equal to its weight
		assertEquals("Finish time of current partial solution is incorrect", 
				vertexArray[0].getBottomLevel(), pSolution.getMinimumFinishTime());
		
		//The number of available vertices must be 9
		HashSet<Vertex> vertices = pSolution.getAvailableVertices();
		assertEquals("Incorrect number of available vertices", 9, vertices.size());
		
		/*Test the rest of the vertices*/
		int startTime = vertexArray[0].getWeight();
		int num = 8;
		
		for(int i = 1; i < NUM_VERTICES; i++) {
			pSolution = new PartialSolution(graph, pSolution, vertexArray[i], (byte) 0);
			//Finishing time of the current partial solution should be equal to its startTime + weight
			//System.out.println("Expected "+(startTime + vertexArray[i].getBottomLevel())+ " but Received "+pSolution.getMinimumFinishTime());
			assertEquals("Finish time of current partial solution is incorrect", 
					(startTime + vertexArray[i].getBottomLevel()), pSolution.getMinimumFinishTime());
			startTime += vertexArray[i].getWeight();
			
			//The number of available vertices must reduce by 1 each time, as they are being allocated
			vertices = pSolution.getAvailableVertices();
			
			assertEquals("Incorrect number of available vertices", num, vertices.size());
			num--;
		}
	}
	
	@Test
	public void testSmallVertexPartialSolution() {
		
		/*Create a graph with 4 vertices and start vertex a*/
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
		
		/*Vertex A*/
		AStarSequential astar = new AStarSequential(graph, (byte) 2);
		astar.initialise();
		PartialSolution pSolution = new PartialSolution(graph, (byte) 2, vertexa, (byte) 0);
		//Check the finish time of the current partial solution
		System.out.println("Bottom Level of a "+vertexa.getBottomLevel());
		assertEquals("Finish time of current partial solution is incorrect", 
				vertexa.getBottomLevel(), pSolution.getMinimumFinishTime());
		
		HashSet<Vertex> vertices = pSolution.getAvailableVertices();
		//There should be 2 available vertices which are b and c
		assertEquals("Incorrect number of available vertices", 2, vertices.size());
		assertEquals("GetAvailableVertices doesn't contain expected vertex", true, vertices.contains(vertexb));
		assertEquals("GetAvailableVertices doesn't contain expected vertex", true, vertices.contains(vertexc));
		
		
		/*Vertex B*/
		pSolution = new PartialSolution(graph, pSolution, vertexb, (byte) 0);
		//Check the finish time of the current partial solution
		assertEquals("Finish time of current partial solution is incorrect", 2, pSolution.getMinimumFinishTime());
		
		//There should be 1 available vertex, which is c
		vertices = pSolution.getAvailableVertices();
		assertEquals("Incorrect number of available vertices", 1, vertices.size());
		assertEquals("GetAvailableVertices doesn't contain expected vertex", true, vertices.contains(vertexc));
		
		/*Vertex C*/
		pSolution = new PartialSolution(graph, pSolution, vertexc, (byte) 1);
		//Check the finish time of the current partial solution
		assertEquals("Finish time of current partial solution is incorrect", 4, pSolution.getMinimumFinishTime());
		
		vertices = pSolution.getAvailableVertices();
		//There should be 1 available vertex, which is d
		assertEquals("Incorrect number of available vertices", 1, vertices.size());
		assertEquals("GetAvailableVertices doesn't contain expected vertex", true, vertices.contains(vertexd));
		
		/*Vertex D*/
		pSolution = new PartialSolution(graph, pSolution, vertexd, (byte) 1);
		//Check the finish time of the current partial solution
		
		vertices = pSolution.getAvailableVertices();
		//There should be no available vertices
		assertEquals("Incorrect number of available vertices", 0, vertices.size());
		
	}

}
