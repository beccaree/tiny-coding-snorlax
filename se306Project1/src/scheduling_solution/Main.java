package scheduling_solution;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.sun.jndi.toolkit.ctx.PartialCompositeContext;

import scheduling_solution.astar.AStar;
import scheduling_solution.astar.PartialSolution;
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
		args = new String[]{"tests/example2.dot", "2"};

//		args = new String[]{"tests/EdgesBeforeNodes.dot", "1"};
		
		long startTime = System.currentTimeMillis();

		parseArgs(args);
		GraphInterface<Vertex, DefaultWeightedEdge> graph = GraphParser.parse(inputFileName);
		BottomLevelCalculator.calculate(graph);
		
		new GraphVisualisation(GraphParser.getDisplayGraph());
		
		AStar astar = new AStar(graph,  numProcessors);
		PartialSolution p = astar.calculateOptimalSolution();
		
		p.printDetails();
		System.out.println("Solutions created: " + astar.solutionsCreated);
		System.out.println("Solutions popped: " + astar.solutionsPopped);
		System.out.println("Solutions pruned: " + astar.solutionsPruned);
		long finishTime = System.currentTimeMillis();
		System.out.println("Time taken: " + (finishTime - startTime));
		//Basic milestone: Produce any schedule
//		List<Vertex> topologicalSort = TopologicalSortGenerator.calculate(graph);
//		
//		Solution topologicalSolution = TopologicalSolver.solve(topologicalSort);
//		
//		//Should decide on whether to do static or non-static
//		OutputFileCreator.create(outputFileName, inputFileName, topologicalSolution);
		
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
			
		} catch(ArrayIndexOutOfBoundsException | StringIndexOutOfBoundsException e) {
			System.out.println("Invalid command line arguments!");
			e.printStackTrace();
		}
		
	}
}
