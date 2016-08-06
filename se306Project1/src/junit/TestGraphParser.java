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

public class TestGraphParser extends TestCase{
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
	public void testSmallGraphParse() {
		graph = GraphParser.parse(inputFile1);
		assertEquals("Incorrect number of nodes", 4, graph.vertexSet().size());
		assertEquals("Incorrect number of edges", 4, graph.edgeSet().size());
	}
	
	@Test 
	public void testLargeGraphParse() {
		graph = GraphParser.parse(inputFile2);
		assertEquals("Incorrect number of nodes", 8, graph.vertexSet().size());
		assertEquals("Incorrect number of edges", 8, graph.edgeSet().size());
	}
}
