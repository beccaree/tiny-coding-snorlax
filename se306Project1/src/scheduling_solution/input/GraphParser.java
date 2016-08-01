package scheduling_solution.input;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.tools.GraphInterface;
import scheduling_solution.tools.JGraphTAdapter;

/**
 * GraphParser Class creates a weighted directed graph with values from an input file
 * @author Team 8
 */
public class GraphParser {

	private String inputFile;
	private GraphInterface<String, DefaultWeightedEdge> directedGraph = new JGraphTAdapter<>(DefaultWeightedEdge.class);

	public GraphParser(String inputFile) {
		this.inputFile = inputFile;
	}

	/**
	 * Reads the input file, parses each line
	 * and adds the edges and nodes into a directed graph
	 */
	public void parse() {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
			String line = br.readLine();
			String outputGraphName = createOutputGraphName(line);
			line = br.readLine().trim();

			while (!line.contains("}")) {
				// maybe think of a better name
				String nodeOrEdge = line.substring(0, line.indexOf("[")).trim();
				String weight = line.substring(line.indexOf("=") + 1, line.lastIndexOf("]"));

				if (nodeOrEdge.contains("->")) { //An edge					
					//Get first node
					String fromNode = nodeOrEdge.substring(0, nodeOrEdge.indexOf("-")).trim();
					//Get second node
					String toNode = nodeOrEdge.substring(nodeOrEdge.indexOf(">") + 1).trim();

					//Add edge with weight to directed graph				
					DefaultWeightedEdge edge = directedGraph.addEdge(fromNode, toNode);
					directedGraph.setEdgeWeight(edge, Integer.parseInt(weight));
					
					//debugging 
					System.out.println("New edge " + fromNode + "->" + toNode + " with weight " + weight + " created.");
					
				} else {
					
					//Add node to directed graph
					directedGraph.addVertex(nodeOrEdge);
					
					//debugging
					System.out.println("New node " + nodeOrEdge + " with weight " + weight + " created.");					
				}

				line = br.readLine();
			}
			System.out.println(directedGraph.toString());

		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * This function takes the line containing the name of the graph and returns the correct
	 * output graph name, which should capitalize the first letter and prepend the word 'output' e.g.
	 * digraph "example" { --> outputExample
	 * @param line - the line containing the graph's name
	 * @return
	 */
	private String createOutputGraphName(String line) {
		String inputGraphName = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
		return "output" + inputGraphName.substring(0, 1).toUpperCase()
				+ inputGraphName.substring(1, inputGraphName.length());
	}
}
