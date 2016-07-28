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
			br = new BufferedReader(new InputStreamReader(new FileInputStream(
					inputFile)));
			
			  String line = br.readLine();			  
			  String outputGraphName = createOutputGraphName(line);
			  line = br.readLine().trim();			  
			  String[] splitLine;
			  int weight;
			  
			  while(!line.contains("}"))
			  {
				  line = line.replaceAll("\\s+"," ");
				  splitLine = line.split(" ");
				  if(splitLine.length == 2){
					  //TODO Assign node name stored in index 0
					  weight = parseWeight(splitLine[1]);
				  }else{
					  //TODO Assign node names in index 0 and 2
					  weight = parseWeight(splitLine[3]);
				  }
				  line = br.readLine().trim();
			  }
			  
		} catch (IOException e) {
			e.printStackTrace();
		}
	}
	
	private int parseWeight(String weightString){		
		return Integer.parseInt(weightString.substring(weightString.indexOf("=")+1, weightString.indexOf("]")));
	}
	
	private String createOutputGraphName(String line) {
		String inputGraphName = line.substring(line.indexOf("\"")+1, line.lastIndexOf("\""));		 
		return "output" + inputGraphName.substring(0,1).toUpperCase() + inputGraphName.substring(1,inputGraphName.length()); 
	}
}
