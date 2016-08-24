package junit;

import static org.junit.Assert.*;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scheduling_solution.astar.AStarSequential;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.branch_and_bound.BranchAndBound;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.JGraphTAdapter;
import scheduling_solution.graph.Vertex;
import scheduling_solution.input.GraphParser;

public class Test11NodeVaildOptimalSolution {
	
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;
	private static final byte numProcessors = 4;
	
	// Solution 
	AStarSequential astar;
	BranchAndBound branchBound;
	
	// Files
	private static final String node11ForkJoin = "tests/Nodes_11_ForkJoin.dot";	
	
	
	
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
		
		graph = GraphParser.parse(node11ForkJoin);
		
		astar = new AStarSequential(graph, numProcessors);
		PartialSolution aStarSolution = astar.calculateOptimalSolution();
		
		branchBound = new BranchAndBound();
		PartialSolution branchBoundSolution = branchBound.calculateOptimalSolution(graph, numProcessors);

		assertEquals("Same Key Set Size",branchBoundSolution.getAllocatedVertices().keySet().size(), aStarSolution.getAllocatedVertices().keySet().size());
		assertEquals("Check Finish Times",branchBoundSolution.getFinishTime(),aStarSolution.getFinishTime());

		
		
	}


}
