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
			String fromVertexString, toVertexString;
			Vertex fromVertex, toVertex;

			while (!line.contains("}")) {
				try {
					//Get vertex/edge and their weights
					String vertexOrEdge = line.substring(0, line.indexOf("[")).trim();
					String weight = line.substring(line.indexOf("=") + 1, line.lastIndexOf("]"));
	
					// "->" indicates an edge
					if (vertexOrEdge.contains("->")) { 		
						
						//Get first vertex (value before "-" char)
						fromVertexString = vertexOrEdge.substring(0, vertexOrEdge.indexOf("-")).trim();
						fromVertex = vertexMap.get(fromVertexString);
						
						//Get second vertex (value after ">" char)
						toVertexString = vertexOrEdge.substring(vertexOrEdge.indexOf(">") + 1).trim();
						toVertex = vertexMap.get(toVertexString);
						
						//Check if the vertices have not been read in yet, and create them with weight 0 if they haven't.
						//When reading the vertex line later, the weight will be set
						if (fromVertex == null) {
							fromVertex = addVertex(fromVertexString, 0);
						}
						
						if (toVertex == null) {
							toVertex = addVertex(toVertexString, 0);
						}
						
						//Add the edge with weight to the directed graph				
						DefaultWeightedEdge edge = directedGraph.addEdge(fromVertex, toVertex);
						directedGraph.setEdgeWeight(edge, Integer.parseInt(weight));
						
						//TODO: remove this debugging statement at end
						//System.out.println("New edge " + fromVertex + "->" + toVertex + " with weight " + weight + " created.");
						
					} else {
						
						//Add the node with weight to the directed graph.
						//If the node does not exist, create and add it. If it does, set the nodes weight
						Vertex v = vertexMap.get(vertexOrEdge);
						if (v == null) {
							addVertex(vertexOrEdge, Integer.parseInt(weight));
						} else {
							v.setWeight(Integer.parseInt(weight));
						}
					
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
	
	/**
	 * /**
	 * Creates a new vertex with the given name and weight, and adds it to the graph and vertexMap.
	 * @param vertexName
	 * @param vertexWeight
	 * @return v : the created vertex
	 */
	private static Vertex addVertex(String vertexName, int vertexWeight) {
		Vertex v = new Vertex(vertexName, vertexWeight);
		directedGraph.addVertex(v);
		vertexMap.put(vertexName, v);
		return v;
	}
	
	public static HashMap<String, Vertex> getVertexMap() {
		return vertexMap;
	}
}
