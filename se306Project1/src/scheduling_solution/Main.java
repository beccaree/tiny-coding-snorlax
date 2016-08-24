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
//		args = new String[]{"tests/Nodes_11_OutTree.dot", "2"};
		long startTime = System.currentTimeMillis();

		parseArgs(args);
		GraphInterface<Vertex, DefaultWeightedEdge> graph = GraphParser.parse(inputFileName);
		
		PartialSolution p = null;
		
		if (isVisualised) {
			GraphVisualisation visuals = new GraphVisualisation(GraphParser.getDisplayGraph(), startTime, numProcessors, numThreads, inputFileName);
			AStarVisuals astar = new AStarVisuals(graph,  numProcessors, numThreads, visuals, isParallel);
			p = astar.calculateOptimalSolution();
			visuals.stopTimer(p, astar);	
		} else {
			AStar astar;
			if (isParallel) {
				astar = new AStarParallel(graph,  numProcessors, numThreads);
			} else {
				astar = new AStarSequential(graph, numProcessors);
			}
			p = astar.calculateOptimalSolution();
		}
		
		OutputFileCreator.create(outputFileName, inputFileName, p);
	}
	
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
