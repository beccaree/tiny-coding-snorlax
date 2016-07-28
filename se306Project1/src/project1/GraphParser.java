package project1;

import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

public class GraphParser {

	private String inputFile;

	public GraphParser(String inputFile) {
		this.inputFile = inputFile;
	}

	public void parse() {
		BufferedReader br;
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFile)));

			String line = br.readLine();
			String outputGraphName = createOutputGraphName(line);
			line = br.readLine().trim();

			while (!line.contains("}")) {
				// maybe think of a better name
				String nodeOrEdgeString = line.substring(0, line.indexOf("[")).trim();
				String weightString = line.substring(line.indexOf("=") + 1, line.lastIndexOf("]"));

				if (nodeOrEdgeString.contains("->")) {// edge
					String fromNodeString = nodeOrEdgeString.substring(0, nodeOrEdgeString.indexOf("-")).trim();
					String toNodeString = nodeOrEdgeString.substring(nodeOrEdgeString.indexOf(">") + 1).trim();
					
					//debugging 
					System.out.println("New edge " + fromNodeString + "->" + toNodeString + " with weight " + weightString + " created.");
					
					// add new edge here
				} else {// node
					
					//debugging
					System.out.println("New node " + nodeOrEdgeString + " with weight " + weightString + " created.");
					// new Node(nodeOrEdgeString)
				}

				line = br.readLine();
			}

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
