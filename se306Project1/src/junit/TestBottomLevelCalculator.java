package junit;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import graph.GraphInterface;
import graph.JGraphTAdapter;
import graph.Vertex;
import junit.framework.TestCase;
import scheduling_solution.input.GraphParser;
import scheduling_solution.solver.BottomLevelCalculator;

public class TestBottomLevelCalculator extends TestCase{
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;
	
	private static final String inputFile1 = "tests/example1.dot";
	private static final String inputFile2 = "tests/example2.dot";
	
	@Before
	public void setUp() {
		graph = new JGraphTAdapter<>(DefaultWeightedEdge.class);
	}
	
	@After
	public void tearDown() {
		graph = null;
	}
	
	@Test
	public void testSmallGraphBottomLevels() {
		graph = GraphParser.parse(inputFile1);
		BottomLevelCalculator.calculate(graph);
		int bottomLevelSum = sumBottomLevels(graph);
		assertEquals("Incorrect sum of bottom levels", 19, bottomLevelSum);
	}
	
	@Test 
	public void testLargeGraphBottomLevels() {
		graph = GraphParser.parse(inputFile2);
		BottomLevelCalculator.calculate(graph);
		int bottomLevelSum = sumBottomLevels(graph);
		assertEquals("Incorrect sum of bottom levels", 50, bottomLevelSum);
	}
	
	private int sumBottomLevels(GraphInterface<Vertex, DefaultWeightedEdge> graph) {
		int sum = 0;
		for(Vertex v : graph.vertexSet()) {
			sum += v.getBottomLevel();
		}
		return sum;
	}
}
