package scheduling_solution.astar.parallel;

import java.util.PriorityQueue;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.astar.AStarVisuals;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.visualisation.GraphVisualisation;

public class AStarRunnableVisuals extends AStarVisuals implements Runnable{
	
	private GraphVisualisation visualisation;	
	final int threadNumber;
	
	private PartialSolution optimalSolution = null;

	public AStarRunnableVisuals(int i, GraphInterface<Vertex, DefaultWeightedEdge> graph, 
			PriorityQueue<PartialSolution> unexploredSolutions, Set<PartialSolution> exploredSolutions, 
			byte numProcessors, GraphVisualisation visualisation) {
		
		super(graph, numProcessors, i, visualisation, true);
		this.threadNumber = i;
		this.unexploredSolutions = unexploredSolutions;
		this.exploredSolutions = exploredSolutions;
		this.visualisation = visualisation;
	}
	
	@Override
	public void run() {
		while (true) {
			PartialSolution currentSolution = unexploredSolutions.poll();
			
			// check partial solution has all vertices allocated
			if (isComplete(currentSolution)) {
				this.optimalSolution = currentSolution;
				return;
			} else {
				expandPartialSolution(currentSolution);
				visualisation.updateQueueSize(threadNumber, unexploredSolutions.size(), exploredSolutions.size());
			}
		}
	}
	
	@Override
	public PartialSolution calculateOptimalSolution() {
		return optimalSolution;
	}

	
}
