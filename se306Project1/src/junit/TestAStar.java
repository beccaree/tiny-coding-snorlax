package junit;

import java.util.PriorityQueue;

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
		
		//Runs Astar on the graph with 2 processors
		AStarSequential astar = new AStarSequential(graph, (byte) 2);
		
		//Checks if the number of start states is what is expected
		astar.initialise();
		unexploredSolutions = (PriorityQueue<PartialSolution>) astar.getUnexploredSolutions();
		assertEquals("Number of start states is different than what was expected", 1, unexploredSolutions.size());
		
		//Checks that the start state is vertex "a"
		PartialSolution pSolution = unexploredSolutions.poll();
//		HashSet<Vertex> vertices = pSolution.getAllocatedVertices();
//		assertEquals("Starting State partial solutions don't contain expected vertex", 
//				true, vertices.contains(vertexa));
		
		assertEquals("isComplete returns wrong boolean outcome", false, astar.isComplete(pSolution));
		
		//Add Vertices into partial solution and check start time after adding each vertex, then
		//check if partial solution is complete.
//		int startTime = astar.calculateStartTime(pSolution, vertexb, 0);
//		pSolution = new PartialSolution(graph, pSolution, vertexb, 0, startTime);
//		assertEquals("Starting time of vertex in partial solution is wrong", 2, startTime);
//		assertEquals("isComplete returns wrong boolean outcome", false, astar.isComplete(pSolution));
//		
//		startTime = astar.calculateStartTime(pSolution, vertexc, 1);
//		pSolution = new PartialSolution(graph, pSolution, vertexc, 1, startTime);
//		assertEquals("Starting time of vertex in partial solution is wrong", 4, startTime);
//		assertEquals("isComplete returns wrong boolean outcome", false, astar.isComplete(pSolution));
//		
//		startTime = astar.calculateStartTime(pSolution, vertexd, 1);
//		pSolution = new PartialSolution(graph, pSolution, vertexd, 1, startTime);
//		assertEquals("Starting time of vertex in partial solution is wrong", 7, startTime);
//		assertEquals("isComplete returns wrong boolean outcome", true, astar.isComplete(pSolution));
	}
	
	@Test
	public void testIndependentGraphAStar() {
		
		Vertex[] vertexArray = new Vertex[NUM_VERTICES];
		//Create a graph with 10 independent vertices
		for(int i = 0; i < NUM_VERTICES; i++) {
			vertexArray[i] = new Vertex(String.valueOf(i), i);
			graph.addVertex(vertexArray[i]);
		}
		
		//Runs Astar on the graph with 2 processors
		AStarSequential astar = new AStarSequential(graph, (byte) 2);
		
		//Checks if the number of start states is what is expected
		astar.initialise();
		unexploredSolutions = (PriorityQueue<PartialSolution>) astar.getUnexploredSolutions();
		assertEquals("Number of start states is different than what was expected", 10, unexploredSolutions.size());
		
//		for (int i = 0; i < NUM_VERTICES; i++) {
//			PartialSolution pSolution = unexploredSolutions.poll();
//			HashSet<Vertex> vertices = pSolution.getAllocatedVertices();
//			
//			//Checks that each of the starting states has each of the independent vertices
//			assertEquals("Starting State partial solutions don't contain expected vertex", 
//					true, vertices.contains(vertexArray[i]));
//
//			//Adding a vertex to each of the starting state partial solutions should give a
//			//start time of 0 when it's on a different processor
//			assertEquals("Starting time of new independent vertex in an empty processor is not 0", 
//					0, astar.calculateStartTime(pSolution, vertexArray[0], 1));
//
//			//Checks that the start time of new vertex is the same as the weight of the existing vertex 
//			//when they are on the same processor
//			assertEquals("Starting time of new vertex in same processor equal to weight of previous vertex", 
//					vertexArray[i].getWeight(), astar.calculateStartTime(pSolution, vertexArray[0], 0));
//		}
	}
	
	@Test
	public void testLargeGraphAStar() {
		
		Vertex[] vertexArray = new Vertex[NUM_VERTICES];
		//Create a graph with 10 vertices where 0,6,8 and 9 are start vertices
		for(int i = 0; i < NUM_VERTICES; i++) {
			vertexArray[i] = new Vertex(String.valueOf(i), i);
			graph.addVertex(vertexArray[i]);
		}
		
		//Adds edges between vertices
		DefaultWeightedEdge edge02 = graph.addEdge(vertexArray[0], vertexArray[2]);
		graph.setEdgeWeight(edge02, 1);
		DefaultWeightedEdge edge03 = graph.addEdge(vertexArray[0], vertexArray[3]);
		graph.setEdgeWeight(edge03, 1);
		DefaultWeightedEdge edge31 = graph.addEdge(vertexArray[3], vertexArray[1]);
		graph.setEdgeWeight(edge31, 1);
		DefaultWeightedEdge edge63 = graph.addEdge(vertexArray[6], vertexArray[3]);
		graph.setEdgeWeight(edge63, 1);
		DefaultWeightedEdge edge65 = graph.addEdge(vertexArray[6], vertexArray[5]);
		graph.setEdgeWeight(edge65, 1);
		DefaultWeightedEdge edge57 = graph.addEdge(vertexArray[5], vertexArray[7]);
		graph.setEdgeWeight(edge57, 1);
		DefaultWeightedEdge edge84 = graph.addEdge(vertexArray[8], vertexArray[4]);
		graph.setEdgeWeight(edge84, 1);
		DefaultWeightedEdge edge45 = graph.addEdge(vertexArray[4], vertexArray[5]);
		graph.setEdgeWeight(edge45, 1);
		DefaultWeightedEdge edge94 = graph.addEdge(vertexArray[9], vertexArray[4]);
		graph.setEdgeWeight(edge94, 1);
		
		//Runs Astar on the graph with 3 processors
		AStarSequential astar = new AStarSequential(graph, (byte) 3);
		
		//Checks if the number of start states is what is expected
		astar.initialise();
		unexploredSolutions = (PriorityQueue<PartialSolution>) astar.getUnexploredSolutions();
		assertEquals("Number of start states is different than what was expected", 4, unexploredSolutions.size());
		
//		//Checks that the start state vertices are 0, 6, 8 and 9
//		HashSet<Vertex> vertices = unexploredSolutions.poll().getAllocatedVertices();
//		assertEquals("Starting State partial solutions don't contain expected vertex", 
//				true, vertices.contains(vertexArray[0]));
//		vertices = unexploredSolutions.poll().getAllocatedVertices();
//		assertEquals("Starting State partial solutions don't contain expected vertex", 
//				true, vertices.contains(vertexArray[6]));
//		vertices = unexploredSolutions.poll().getAllocatedVertices();
//		assertEquals("Starting State partial solutions don't contain expected vertex", 
//				true, vertices.contains(vertexArray[8]));
//		vertices = unexploredSolutions.poll().getAllocatedVertices();
//		assertEquals("Starting State partial solutions don't contain expected vertex", 
//				true, vertices.contains(vertexArray[9]));
	}
	
}
