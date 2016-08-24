package scheduling_solution.graph;

import java.util.Set;

import org.jgrapht.graph.DefaultDirectedWeightedGraph;
/**
 * Used so that if we were to change graph libraries,
 * The change would not have a large effect to the
 * code using the libraries
 * @param <V> Vertices
 * @param <E> Edges
 */
public interface GraphInterface<V,E> {
	
	public E addEdge(V sourceVertex, V targetVertex);
	public boolean addEdge(V sourceVertex,V targetVertex, E e);
	public Set<E> edgeSet();
	public void addVertex(V v);
	public V getEdgeSource(E e);
	public V getEdgeTarget(E e);
	public double getEdgeWeight(E e);
	public void setEdgeWeight(E e, double weight);
	public int inDegreeOf(V vertex);
	public int outDegreeOf(V vertex);
	public Set<E> edgesOf(V vertex);
	public Set<E> incomingEdgesOf(V vertex);
	public Set<E> outgoingEdgesOf(V vertex);
	public E removeEdge(V sourceVertex, V targetVertex);
	public void removeEdge(E e);
	public void removeVertex(V v);
	public Set<V> vertexSet();
	public String toString();
	public DefaultDirectedWeightedGraph<V,E> getGraph();
	
}
