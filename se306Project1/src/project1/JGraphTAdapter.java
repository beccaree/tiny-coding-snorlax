package project1;

import java.util.Set;
import org.jgrapht.graph.*;

public class JGraphTAdapter<V,E> implements GraphInterface<V,E>{
	private DefaultDirectedWeightedGraph<V,E> directedGraph;

	public JGraphTAdapter(Class<? extends E> edgeClass) {
		this.directedGraph = 
				new DefaultDirectedWeightedGraph<V,E>(edgeClass);
	}

	@Override
	public E addEdge(V sourceVertex, V targetVertex) {
		return directedGraph.addEdge(sourceVertex, targetVertex);
	}

	@Override
	public void addVertex(V v) {
		directedGraph.addVertex(v);		
	}

	@Override
	public V getEdgeSource(E e) {
		return directedGraph.getEdgeSource(e);
	}

	@Override
	public V getEdgeTarget(E e) {
		return directedGraph.getEdgeTarget(e);
	}

	@Override
	public double getEdgeWeight(E e) {
		return directedGraph.getEdgeWeight(e);
	}

	@Override
	public void setEdgeWeight(E e, double weight) {
		directedGraph.setEdgeWeight(e, weight);		
	}

	@Override
	public int inDegreeOf(V vertex) {
		return directedGraph.inDegreeOf(vertex);
	}

	@Override
	public int outDegreeOf(V vertex) {
		return directedGraph.outDegreeOf(vertex);
	}

	@Override
	public Set<E> incomingEdgesOf(V vertex) {
		return directedGraph.incomingEdgesOf(vertex);
	}

	@Override
	public Set<E> outgoingEdgesOf(V vertex) {
		return directedGraph.outgoingEdgesOf(vertex);
	}

	@Override
	public E removeEdge(V sourceVertex, V targetVertex) {
		return directedGraph.removeEdge(sourceVertex, targetVertex);
	}

	@Override
	public void removeEdge(E e) {
		directedGraph.removeEdge(e);		
	}

	@Override
	public void removeVertex(V v) {
		directedGraph.removeVertex(v);
	}
	
}
