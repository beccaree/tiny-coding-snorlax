package scheduling_solution;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.astar.AStar;
import scheduling_solution.astar.AStarSequential;
import scheduling_solution.astar.AStarVisuals;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.astar.parallel.AStarParallel;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.input.GraphParser;
import scheduling_solution.output.OutputFileCreator;
import scheduling_solution.visualisation.GraphVisualisation;

public class Main {
	
	private static String inputFileName;
	private static byte numProcessors;
	private static boolean isParallel = false;
	private static int numThreads = 1;	
	private static boolean isVisualised = false;
	private static String outputFileName;

	public static void main(String[] args) {

		long startTime = System.currentTimeMillis();
		
		//Parse command line instructions and stores data from input file into a digraph
		parseArgs(args);
		GraphInterface<Vertex, DefaultWeightedEdge> graph = GraphParser.parse(inputFileName);
		
		PartialSolution p = null;
		
		//If user wants visualisation then initalise and start JFrames 
		if (isVisualised) {
			GraphVisualisation visuals = new GraphVisualisation(GraphParser.getDisplayGraph(), startTime, numProcessors, numThreads, inputFileName);
			AStarVisuals astar = new AStarVisuals(graph,  numProcessors, numThreads, visuals, isParallel);
			p = astar.calculateOptimalSolution();
			visuals.stopTimer(p, astar);	
		} else {
			AStar astar;
			
			//Check if calculating in parallel then start the AStar process for calculation
			if (isParallel) {
				astar = new AStarParallel(graph,  numProcessors, numThreads);
			} else {
				astar = new AStarSequential(graph, numProcessors);
			}
			p = astar.calculateOptimalSolution();
		}
		
		//Create the output file
		OutputFileCreator.create(outputFileName, inputFileName, p);
	}
	
	/**
	 * Parse command line argument
	 * @param args - command line arguments
	 */
	private static void parseArgs(String[] args) {		
		try {
			inputFileName = args[0];
			numProcessors = Byte.parseByte(args[1]);
			
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
			
		} catch(ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
			System.out.println("Invalid command line arguments!");
			e.printStackTrace();
		}
		
	}
}
