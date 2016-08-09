package scheduling_solution;

import java.util.List;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.input.GraphParser;
import scheduling_solution.output.OutputFileCreator;
import scheduling_solution.output.Solution;
import scheduling_solution.solver.BottomLevelCalculator;
import scheduling_solution.solver.TopologicalSortGenerator;
import scheduling_solution.solver.TopologicalSolver;
import scheduling_solution.visualisation.GraphVisualisation;

public class Main {
	
	private static String inputFileName;
	private static int numProcessors;
	private static boolean isParallel = false;
	private static int numThreads;
	private static boolean isVisualised = false;
	private static String outputFileName;

	public static void main(String[] args) {
		args = new String[]{"tests/example1.dot", "1"};
		parseArgs(args);
		GraphInterface<Vertex, DefaultWeightedEdge> graph = GraphParser.parse(inputFileName);
		BottomLevelCalculator.calculate(graph);
//		for(Vertex v : graph.vertexSet()) System.out.println(v.getName() + "  " + v.getBottomLevel());
//		System.out.println(graph.toString());
		//new GraphVisualisation(graph.getGraph());
		//Basic milestone: Produce any schedule
		List<Vertex> topologicalSort = TopologicalSortGenerator.calculate(graph);
		
		Solution topologicalSolution = TopologicalSolver.solve(topologicalSort, graph);
		
		new OutputFileCreator(outputFileName, inputFileName).create(topologicalSolution);
//		System.out.println("topologicalSort");
		for(Vertex vertex : topologicalSort){			
//			System.out.print(vertex+",");
		}
		
		
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
