package scheduling_solution.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import scheduling_solution.astar.AllocationInfo;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.graph.Vertex;
import scheduling_solution.input.GraphParser;
import scheduling_solution.solver.VertexInfo;

/**
 * Creates and writes the output file in the same format as the input file
 * @author Team 8
 */
public class OutputFileCreator {
	
	/**
	 * This method creates the output file and writes to it by analyzing the solution
	 * as well as the input file.
	 */
	public static void create(String outputFileName, String inputFileName, PartialSolution solution) {
			
		BufferedReader br;
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName)));
			
			//Write to output file with output file name 
			FileWriter fw = new FileWriter(outputFileName);			
			BufferedWriter bw = new BufferedWriter(fw);
			
			String line = br.readLine();
			//Format first line in output
			String outputGraphName = createOutputGraphName(line);
			String title = line.substring(0, line.indexOf("\""))+"\""+outputGraphName+line.substring(line.lastIndexOf("\""));
			bw.write(title);
			bw.newLine();

			while ((line = br.readLine()) != null) {
					//Read input file and write output format for a vertex
					// i.e. doesn't contain "->" characters
					if(line.contains("[") &&(line.contains("]")) && (!line.contains("->"))) {						
						
						//Store line of input as "substring"
						String substring = line.substring(0,line.lastIndexOf("]"));
						//Get vertex name
						String vertexName = line.substring(0, line.indexOf("[")).trim();
						
						//Get corresponding vertex object based on the vertex name
						Vertex vertex = null;						
						for (Vertex v : solution.getAllocatedVertices().keySet()) {
							if(v.getName().equals(vertexName)){
								vertex = v;
								break;
							}
						}
						//Get vertex information of the vertex
						AllocationInfo vertexInfo  = solution.getAllocatedVertices().get(vertex);
						
						//Write to file
						System.out.println(substring+", Start="+vertexInfo.getStartTime()+", Processor="+vertexInfo.getProcessorNumber()+"];");
						bw.write(substring+", Start="+vertexInfo.getStartTime()+", Processor="+vertexInfo.getProcessorNumber()+"];");
						
						
						
//						Vertex v = GraphParser.getVertexMap().get(vertex);
//						//Get solution of the vertex 
//						//i.e. the string that appears after each vertex/edge in the output file
//						VertexInfo vInfo = solution.getVertexInfo(v);
//	
//						//Rewrite vertex output to include solution
//						bw.write(substring+vInfo.toString()+"];");
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
