package junit;

import static org.junit.Assert.*;

import java.util.PriorityQueue;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import scheduling_solution.astar.AStarSeq;
import scheduling_solution.astar.AllocationInfo;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.branch_and_bound.BranchAndBound;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.JGraphTAdapter;
import scheduling_solution.graph.Vertex;
import scheduling_solution.input.GraphParser;

public class TestVaildOptimalSolution {
	
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;
	private static final String node11ForkJoin = "tests/Nodes_11_ForkJoin.dot";	
	private static final byte numProcessors = 4;
	AStarSeq astar;
	BranchAndBound branchBound;
	
	@Before
	public void setUp() throws Exception {
		graph = new JGraphTAdapter<>(DefaultWeightedEdge.class);
	}

	@After
	public void tearDown() throws Exception {
		graph = null;
	}


	@Test
	public void test11NodeForkJoin() {		
		
		graph = GraphParser.parse(node11ForkJoin);
		
		astar = new AStarSeq(graph, numProcessors);
		PartialSolution aStarSolution = astar.calculateOptimalSolution();
		
		branchBound = new BranchAndBound();
		PartialSolution branchBoundSolution = branchBound.calculateOptimalSolution(graph, numProcessors);

		assertEquals("Same Key Set",branchBoundSolution.getAllocatedVertices().keySet(), aStarSolution.getAllocatedVertices().keySet());
		assertEquals("Check Finish Times",branchBoundSolution.getFinishTime(),aStarSolution.getFinishTime());

		
	

	}

}
