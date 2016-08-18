package scheduling_solution.branch_and_bound;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.PriorityQueue;
import java.util.Queue;
import java.util.Set;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

public class BranchAndBound {
	
	public static GraphInterface<Vertex, DefaultWeightedEdge> graph;
	private static int numProcessors = 1;
	private static int bestBound = 1000;//TODO infinity
	private static PartialSolution currentBest = null;
	private static Queue<PartialSolution> activeStates;
	
	public static PartialSolution calculateOptimalSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph, int numProcessors) {
		BranchAndBound.graph = graph;
		BranchAndBound.numProcessors = numProcessors;
		
		getStartStates();
		
		while (activeStates.size() != 0) {
			PartialSolution startState = activeStates.poll();
			depthFirstBranchAndBound(startState);
		}
		return BranchAndBound.currentBest;
	}
	
	// Depth-First-Branch-And-Bound
	public static void depthFirstBranchAndBound(PartialSolution p) {
		
		int minimumFinishTime = getMinimumFinishTime(p);
		
		if (minimumFinishTime > bestBound) {
			return;
		}
		
		if(isComplete(p)) {
			int finishTime = getFinishTime(p);
			bestBound = finishTime;
			currentBest = p;
			return;
		}
		
		ArrayList<PartialSolution> children = getChildren(p);
		for (PartialSolution nextPartialSolution : children) {
			depthFirstBranchAndBound(nextPartialSolution);
		}
	}
	
	//Gets starting partial solutions
	public static void getStartStates() {
		
		ArrayList<Vertex> startVertices = new ArrayList<Vertex>();
		Set<Vertex> vertices = graph.vertexSet();
		
		//Add vertices in graph with no parents to startVertices
		for (Vertex v : vertices) {
			if(graph.inDegreeOf(v) == 0) {
				startVertices.add(v);
			}
		}
		
		//Create starting partial solutions with a start vertex in processor 1
		for(Vertex startVertex : startVertices) {
			for(int p = 0; p < numProcessors; p++) {
				PartialSolution startPartialSolution = new PartialSolution(graph, (byte) numProcessors, startVertex, (byte) 0);
				activeStates.add(startPartialSolution);
			}
		}
	}
	
	public static int getFinishTime(PartialSolution p) {
		return p.getFinishTime();
	}
	
	public static int getMinimumFinishTime(PartialSolution p) {
		return p.getMinimumFinishTime();
	}

	public static ArrayList<PartialSolution> getChildren(PartialSolution p) {
		ArrayList<PartialSolution> children = new ArrayList<PartialSolution>();
		ArrayList<Vertex> nextVertices = new ArrayList<Vertex>();
		
		Vertex lastVertex = p.getLastVertex();
		
		if (graph.outDegreeOf(lastVertex) != 0) {
			outerloop:
			for (DefaultWeightedEdge e1 : graph.outgoingEdgesOf(lastVertex)) {
				Vertex childVertex = graph.getEdgeTarget(e1);
				for (DefaultWeightedEdge e2 : graph.incomingEdgesOf(childVertex)) {
					Vertex parentVertex = graph.getEdgeSource(e2);
					if (p.getUnAllocatedVertices().contains(parentVertex)) {
						continue outerloop;
					}
				}
				nextVertices.add(childVertex);
			}
		}
		
		for (Vertex v : nextVertices) {
			for (int i = 0; i < numProcessors; i++) {
				PartialSolution child = new PartialSolution(graph, p, v, (byte) i);
				children.add(child);
			}
		}
		return children;
	}
	
	

	public static boolean isComplete(PartialSolution p) {
		return p.getUnAllocatedVertices().size() == 0;
	}

}
