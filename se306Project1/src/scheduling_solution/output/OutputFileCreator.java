package scheduling_solution.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import scheduling_solution.graph.Vertex;
import scheduling_solution.input.GraphParser;
import scheduling_solution.solver.VertexInfo;

/**
 * Creates and writes the output file in the same format as the input file
 * @author Team 8
 */
public class OutputFileCreator {
	
	private String outputFileName;
	private String inputFileName;
	
	//Assigns name to output file
	public OutputFileCreator(String outputFileName, String inputFileName) {
		this.outputFileName = outputFileName;
		this.inputFileName = inputFileName;
	}
	
	/**
	 * This method creates the output file and writes to it by analyzing the solution
	 * as well as the input file.
	 */
	public void create(Solution solution) {
			
		BufferedReader br;
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName)));
			
			//Write to output file with output file name 
			FileWriter fw = new FileWriter(outputFileName);			
			BufferedWriter bw = new BufferedWriter(fw);
			
			String line;

			while ((line = br.readLine()) != null) {
					//Read input file and write output format for a vertex
					// i.e. doesn't contain "->" characters
					if(line.contains("[") &&(line.contains("]")) && (!line.contains("->"))) {
						
						
						//Store line of input as "substring"
						String substring = line.substring(0,line.lastIndexOf("]"));
						//Get vertex name
						String vertex = line.substring(0, line.indexOf("[")).trim();
						
						Vertex v = GraphParser.getVertexMap().get(vertex);
						//Get solution of the vertex 
						//i.e. the string that appears after each vertex/edge in the output file
						VertexInfo vInfo = solution.getVertexInfo(v);
	
						//Rewrite vertex output to include solution
						bw.write(substring+vInfo.toString()+"];");
						bw.newLine();
						
					} else if(line.contains("digraph")){// If it is the initial line //TODO this shouldn't be in the while loop: inefficient. Instead, just do it before the while loop
						String outputGraphName = createOutputGraphName(line);
						String title = line.substring(0, line.indexOf("\""))+"\""+outputGraphName+line.substring(line.lastIndexOf("\""));
						bw.write(title);
						bw.newLine();
					} else if(line.contains("}")) { //Rewrites last line without newline
						bw.write(line);
					} else {				
						bw.write(line); //Rewrites edges
						bw.newLine();
					}
				
					
			}
			bw.close();

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
	private static String createOutputGraphName(String line) {
		String inputGraphName = line.substring(line.indexOf("\"") + 1, line.lastIndexOf("\""));
		return "output" + inputGraphName.substring(0, 1).toUpperCase()
				+ inputGraphName.substring(1, inputGraphName.length());
	}
}
