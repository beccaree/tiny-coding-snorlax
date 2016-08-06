package scheduling_solution.output;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStreamReader;

import graph.Vertex;
import scheduling_solution.input.GraphParser;
import scheduling_solution.solver.VertexInfo;

/**
 * Creates and writes to output file
 * @author sabflik
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
	 * This method creates the output file and writes to it by analysing the solution
	 * as well as the input file.
	 */
	public void create(Solution solution) {
		
		File outFile = new File(outputFileName);
		
		try {//Creates output file with correct name
			outFile.createNewFile();
		} catch (IOException e) {
			System.out.println("Output File couldn't be created");
		}
		
		BufferedReader br;
		
		try {
			br = new BufferedReader(new InputStreamReader(new FileInputStream(inputFileName)));
			FileWriter fw = new FileWriter(outputFileName);
			BufferedWriter bw = new BufferedWriter(fw);
			
			String line;

			while ((line = br.readLine()) != null) {
				
				if((line.contains("]")) && (!line.contains("->"))) {//If it's a vertex do..
					String substring = line.substring(0,line.lastIndexOf("]"));
					String node = line.substring(0, line.indexOf("[")).trim();//Get vertex name
					
					Vertex v = GraphParser.getHashMap().get(node);
					VertexInfo vInfo = solution.getVertexInfo(v);//Get solution of the vertex

					bw.write(substring+vInfo.toString()+"];");//Rewrite vertex output to include solution
					bw.newLine();
					
				} else if(line.contains("}")) {//All lines that aren't vertices are recreated without modification
					bw.write(line);
				} else {
					bw.write(line);
					bw.newLine();
				}
			}
			bw.close();

		} catch (IOException e) {
			e.printStackTrace();
		}		
	}
}
