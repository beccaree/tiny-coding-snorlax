package scheduling_solution;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.input.GraphParser;
import scheduling_solution.tools.BottomLevelCalculator;
import scheduling_solution.tools.GraphInterface;
import scheduling_solution.tools.Vertex;
import scheduling_solution.visualisation.GraphVisualisation;

public class Main {
	
	private static String inputFileName;
	private static int numProcessors;
	private static boolean isParallel = false;
	private static int numThreads;
	private static boolean isVisualised = false;
	private static String outputFileName;

	public static void main(String[] args) {
		args = new String[]{"tests/example2.dot", "1"};
		parseArgs(args);
		GraphParser graphParser = new GraphParser(inputFileName);
		graphParser.parse();
		GraphInterface<Vertex, DefaultWeightedEdge> graph = graphParser.directedGraph;
		new BottomLevelCalculator().calculate(graph);
		for(Vertex v : graph.vertexSet()) System.out.println(v.getName() + "  " + v.getBottomLevel());
		System.out.println(graph.toString());
		new GraphVisualisation();
	}
	
	private static void parseArgs(String[] args) {		
		try {
			inputFileName = args[0];
			numProcessors = Integer.parseInt(args[1]);
			
			String output = inputFileName.substring(0, inputFileName.indexOf(".dot"));
			outputFileName = output+"-output.dot";
		
			//Read in the Options
			for(int i=2; i < args.length; i++) {
				if(args[i].equals("-p")) {
					isParallel = true;
					numThreads = Integer.parseInt(args[++i]);
				} else if (args[i].equals("-v")) {
					isVisualised = true;
				} else if (args[i].equals("-o")) {
					outputFileName = args[++i];
				}
			}
			
		} catch(ArrayIndexOutOfBoundsException e) {
			System.out.println("Invalid command line arguments!");
		}
	}
}
