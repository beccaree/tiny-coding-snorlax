package scheduling_solution.tools;

import java.util.Set;

public interface GraphInterface<V,E> {
	
	public E addEdge(V sourceVertex, V targetVertex);
	public void addVertex(V v);
	public V getEdgeSource(E e);
	public V getEdgeTarget(E e);
	public double getEdgeWeight(E e);
	public void setEdgeWeight(E e, double weight);
	public int inDegreeOf(V vertex);
	public int outDegreeOf(V vertex);
	public Set<E> incomingEdgesOf(V vertex);
	public Set<E> outgoingEdgesOf(V vertex);
	public E removeEdge(V sourceVertex, V targetVertex);
	public void removeEdge(E e);
	public void removeVertex(V v);
	public Set<V> vertexSet();
	public String toString();
}
