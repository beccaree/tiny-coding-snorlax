package junit;

import static org.junit.Assert.*;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scheduling_solution.astar.AStarSeq;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.branch_and_bound.BranchAndBound;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.JGraphTAdapter;
import scheduling_solution.graph.Vertex;
import scheduling_solution.input.GraphParser;

public class TestVaildOptimalSolution {
	
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;
	private static final byte numProcessors = 4;
	
	// Solution 
	AStarSeq astar;
	BranchAndBound branchBound;
	
	// Files
	private static final String node11ForkJoin = "tests/Nodes_11_ForkJoin.dot";	
	private static final String node5ForkJoin = "tests/Nodes_5_ForkJoin.dot";	
	private static final String node7OutTree = "tests/Nodes_7_OutTree.dot";	
	
	
	
	@Before
	public void setUp() throws Exception {
		graph = new JGraphTAdapter<>(DefaultWeightedEdge.class);
	}

	@After
	public void tearDown() throws Exception {
		graph = null;
	}


	@Test
	// Testing file with 11 node with fork joins
	public void test11NodeForkJoin() {	
		
//		compareOptimalSolutions(node11ForkJoin);
		
		graph = GraphParser.parse(node11ForkJoin);
		
		astar = new AStarSeq(graph, numProcessors);
		PartialSolution aStarSolution = astar.calculateOptimalSolution();
		
		branchBound = new BranchAndBound();
		PartialSolution branchBoundSolution = branchBound.calculateOptimalSolution(graph, numProcessors);

		assertEquals("Same Key Set Size",branchBoundSolution.getAllocatedVertices().keySet().size(), aStarSolution.getAllocatedVertices().keySet().size());
		assertEquals("Check Finish Times",branchBoundSolution.getFinishTime(),aStarSolution.getFinishTime());

		
		
	}
	
//	@Test
//	// Testing file with 11 node with fork joins
//	public void test5NodeForkJoin() {	
//		
////		compareOptimalSolutions(node5ForkJoin);
//		
//		graph = GraphParser.parse(node5ForkJoin);
//		
//		astar = new AStarSeq(graph, numProcessors);
//		PartialSolution aStarSolution = astar.calculateOptimalSolution();
//		
//		branchBound = new BranchAndBound();
//		PartialSolution branchBoundSolution = branchBound.calculateOptimalSolution(graph, numProcessors);
//
//		assertEquals("Same Key Set Size",branchBoundSolution.getAllocatedVertices().keySet().size(), aStarSolution.getAllocatedVertices().keySet().size());
//		assertEquals("Check Finish Times",branchBoundSolution.getFinishTime(),aStarSolution.getFinishTime());
//
//		
//		
//	}
//
//	
//	@Test
//	// Testing file with 11 node with fork joins
//	public void test7NodeOutTree() {	
//		
////		compareOptimalSolutions(node7OutTree);
//		graph = GraphParser.parse(node7OutTree);
//		
//		astar = new AStarSeq(graph, numProcessors);
//		PartialSolution aStarSolution = astar.calculateOptimalSolution();
//		
//		branchBound = new BranchAndBound();
//		PartialSolution branchBoundSolution = branchBound.calculateOptimalSolution(graph, numProcessors);
//
//		assertEquals("Same Key Set Size",branchBoundSolution.getAllocatedVertices().keySet().size(), aStarSolution.getAllocatedVertices().keySet().size());
//		assertEquals("Check Finish Times",branchBoundSolution.getFinishTime(),aStarSolution.getFinishTime());
//
//		
//	}
	
	
	
//	
//	private void compareOptimalSolutions(String fileName){
//		
//		graph = GraphParser.parse(fileName);
//		
//		astar = new AStarSeq(graph, numProcessors);
//		PartialSolution aStarSolution = astar.calculateOptimalSolution();
//		
//		branchBound = new BranchAndBound();
//		PartialSolution branchBoundSolution = branchBound.calculateOptimalSolution(graph, numProcessors);
//
//		assertEquals("Same Key Set Size",branchBoundSolution.getAllocatedVertices().keySet().size(), aStarSolution.getAllocatedVertices().keySet().size());
//		assertEquals("Check Finish Times",branchBoundSolution.getFinishTime(),aStarSolution.getFinishTime());
//
//		
//	}

}
