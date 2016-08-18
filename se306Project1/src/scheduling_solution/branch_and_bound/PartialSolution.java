package scheduling_solution.branch_and_bound;

import java.util.HashMap;
import java.util.HashSet;

import org.jgrapht.graph.DefaultWeightedEdge;

import scheduling_solution.astar.AllocationInfo;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;

public class PartialSolution {
	
	private Vertex lastVertex;
	
	private GraphInterface<Vertex, DefaultWeightedEdge> graph;
	private byte numProcessors;
	
	private Integer hashcode = null;
	
	private HashMap<Vertex, AllocationInfo> allocatedVertices;
	private HashSet<Vertex> unallocatedVertices;
	private int minimumFinishTime = 0;
	private int finishTime;

	public PartialSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, Vertex v, byte processorNumber) {
		//Brand new solution with a single vertex
		this.graph = graph;
		this.numProcessors = numProcessors;
		this.lastVertex = v;

		allocatedVertices = new HashMap<Vertex, AllocationInfo>();
		allocatedVertices.put(v, new AllocationInfo((byte) processorNumber, 0));
		
		unallocatedVertices = new HashSet<Vertex>();
		unallocatedVertices.addAll(graph.vertexSet());
		unallocatedVertices.remove(v);
		
		minimumFinishTime = v.getBottomLevel();
	}
	

	@SuppressWarnings("unchecked")
	public PartialSolution(GraphInterface<Vertex, DefaultWeightedEdge> graph, PartialSolution partialSolution, Vertex v, byte processorNumber) {
		this.graph = graph;
		this.numProcessors = partialSolution.numProcessors;
		this.lastVertex = v;
		
		int startTime = calculateStartTime(v, processorNumber);
		
		allocatedVertices = (HashMap<Vertex, AllocationInfo>) partialSolution.getAllocatedVertices().clone();
		allocatedVertices.put(v, new AllocationInfo((byte) processorNumber, startTime));
		
		unallocatedVertices = (HashSet<Vertex>) partialSolution.getUnAllocatedVertices().clone();
		unallocatedVertices.remove(v);
		
		calculateMinimumFinishiTime();
	}
	
	public int calculateStartTime(Vertex v, byte processorNumber) {
		//TODO
	}
	
	public int calculateMinimumFinishiTime() {
		//TODO
	}
	
	public Vertex getLastVertex() {
		return this.lastVertex;
	}
	
	public HashMap<Vertex, AllocationInfo> getAllocatedVertices() {
		return this.allocatedVertices;
	}
	
	public HashSet<Vertex> getUnAllocatedVertices() {
		return this.unallocatedVertices;
	}
	
	public int getMinimumFinishTime() {
		return this.minimumFinishTime;
	}
	
	public int getFinishTime() {
		return this.finishTime;
	}
	
	@Override
	public int hashCode() {
		//I'm not sure if caching it actually helps, as this should only get called once.
		//This technically uses more memory
		if (hashcode == null) {
			hashcode = allocatedVertices.hashCode();
		}
		
		return hashcode;
	}
}

