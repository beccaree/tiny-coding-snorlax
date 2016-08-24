package junit;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scheduling_solution.astar.AStarSequential;
import scheduling_solution.astar.AllocationInfo;
import scheduling_solution.branch_and_bound.PartialSolution;
import scheduling_solution.branch_and_bound.BranchAndBound;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.JGraphTAdapter;
import scheduling_solution.graph.Vertex;

public class TestBranchAndBound {
	
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;
	private HashSet<Vertex> startStates;
	private static final int NUM_Processors=4 ;
	BranchAndBound branchBound;


	@Before
	public void setUp() {
		graph = new JGraphTAdapter<>(DefaultWeightedEdge.class);
	}
	
	@After
	public void tearDown() {
		graph = null;
		startStates = null;
	}
	
	
	
	@SuppressWarnings("static-access")
	@Test//Need to test getStartState and isComplete
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
		
//		branchBound = new BranchAndBound();
//		branchBound.graph = this.graph;
//		branchBound.numProcessors = this.NUM_Processors;
//		
//		//PartialSolution branchBoundSolution = branchBound.calculateOptimalSolution(graph, NUM_VERTICES);
//		
//		//Checks if the number of start states is what is expected
//		branchBound.activeStates = new LinkedList<PartialSolution>();
//		branchBound.getStartStates();
//		startStates = branchBound.getStartVertices();		
//		assertEquals("Number of start states is different than what was expected", 1, startStates.size());
//		
//		//Checks that the start state is vertex "a"
//		PartialSolution pSolution = branchBound.activeStates.poll();
//		HashMap<Vertex, AllocationInfo> vertices = pSolution.getAllocatedVertices();
//		assertEquals("Starting State partial solutions don't contain expected vertex", 
//				true, vertices.get(vertexa));
//		
//		assertEquals("isComplete returns wrong boolean outcome", false, branchBound.isComplete(pSolution));
//		

	}

}
