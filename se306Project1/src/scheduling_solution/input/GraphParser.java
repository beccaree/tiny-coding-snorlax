package scheduling_solution.input;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.HashMap;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.JGraphTAdapter;
import scheduling_solution.graph.Vertex;

/**
 * GraphParser Class creates a weighted directed graph with values from an input file
 * @author Team 8
 */
public class GraphParser {

	private static GraphInterface<Vertex, DefaultWeightedEdge> directedGraph;
	private static HashMap<String, Vertex> vertexMap = new HashMap<>();
	
	/**
	 * Reads the input file, parses each line
	 * and adds the edges and nodes into a directed graph
	 */
	public static GraphInterface<Vertex, DefaultWeightedEdge> parse(String inputFile) {
		
		//Directed graph created from the input file 
		directedGraph = new JGraphTAdapter<>(DefaultWeightedEdge.class);
		vertexMap = new HashMap<>();
		BufferedReader br = null;
		
		try {
			//Read input file
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));
			String line = br.readLine();
			line = br.readLine().trim();
			
			//Initialize vertices
			Vertex fromVertex, toVertex;

			while (!line.contains("}")) {
				try {
					//Get node/edge and their weights
					String vertexOrEdge = line.substring(0, line.indexOf("[")).trim();
					String weight = line.substring(line.indexOf("=") + 1, line.lastIndexOf("]"));
	
					// "->" indicates an edge
					if (vertexOrEdge.contains("->")) { 					
						//Get first node (value before "-" char)
						fromVertex = vertexMap.get(vertexOrEdge.substring(0, vertexOrEdge.indexOf("-")).trim());
						//Get second node (value after ">" char)
						toVertex = vertexMap.get(vertexOrEdge.substring(vertexOrEdge.indexOf(">") + 1).trim());
						
						//Add the edge with weight to the directed graph				
						DefaultWeightedEdge edge = directedGraph.addEdge(fromVertex, toVertex);
						directedGraph.setEdgeWeight(edge, Integer.parseInt(weight));
						
						//TODO: remove this debugging statement at end
						//System.out.println("New edge " + fromVertex + "->" + toVertex + " with weight " + weight + " created.");
						
					} else {
						
						//Add the node with weight to the directed graph
						Vertex v = new Vertex(vertexOrEdge, Integer.parseInt(weight));
						directedGraph.addVertex(v);
						vertexMap.put(vertexOrEdge, v);
						
						//TODO: remove this debugging statement at end
						//System.out.println("New node " + vertexOrEdge + " with weight " + weight + " created.");					
					}
				
				} catch (StringIndexOutOfBoundsException e) {
					line = br.readLine();
					continue;
				}
				//Read next line
				line = br.readLine();
			}
			//TODO: remove this debugging statement at end
			//System.out.println(directedGraph.toString());

		} catch (IOException e) {
			e.printStackTrace();
		} finally{
			try {
				//Close the file input stream
				br.close();
			} catch (IOException e) {
				e.printStackTrace();
			}
		}		
		return directedGraph;
	}
	
	public static HashMap<String, Vertex> getVertexMap() {
		return vertexMap;
	}
}
