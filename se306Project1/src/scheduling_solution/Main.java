package scheduling_solution;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.PrintStream;

import org.jgrapht.graph.DefaultWeightedEdge;
import org.jgrapht.traverse.TopologicalOrderIterator;

import com.sun.jndi.toolkit.ctx.PartialCompositeContext;

import pj.Pyjama;
import scheduling_solution.astar.AStarSeq;
//import scheduling_solution.astar.AStarSeq;
import scheduling_solution.astar.AStarVisuals;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.astar.fullyparallel.AStarParallelThreadsSlow;
import scheduling_solution.astar.threads.AStarParallelThreads;
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
	private static byte numProcessors;
	private static boolean isParallel = false;
	private static int numThreads;
	private static boolean isVisualised = false;
	private static String outputFileName;

	public static void main(String[] args) {
		args = new String[]{"tests/Nodes_11_OutTree.dot", "2"};
		
		long startTime = System.currentTimeMillis();

		parseArgs(args);
		GraphInterface<Vertex, DefaultWeightedEdge> graph = GraphParser.parse(inputFileName);
		
		
		//if (isVisualised) {
		//	GraphVisualisation visuals = new GraphVisualisation(GraphParser.getDisplayGraph(), startTime, numProcessors, Integer.toString(numThreads));
		//	AStarVisuals astar = new AStarVisuals(graph,  numProcessors, visuals);
		//	PartialSolution p = astar.calculateOptimalSolution();
		//}
//		AStarSeq astar = new AStarSeq(graph, numProcessors);
		AStarParallelThreads astar = new AStarParallelThreads(graph,  numProcessors, 4);
		PartialSolution p = astar.calculateOptimalSolution();
		
		//GraphVisualisation.stopTimer();
		p.printDetails();
		System.out.println("Solutions created: " + astar.solutionsCreated);
		System.out.println("Solutions popped: " + astar.solutionsPopped);
		System.out.println("Solutions pruned: " + astar.solutionsPruned);
		System.out.println("Max memory (MB): " + astar.maxMemory /1024/1024);
		long finishTime = System.currentTimeMillis();
		System.out.println("Time taken: " + (finishTime - startTime));
		
		
		
		
		p.verify();//TODO remove
		
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
