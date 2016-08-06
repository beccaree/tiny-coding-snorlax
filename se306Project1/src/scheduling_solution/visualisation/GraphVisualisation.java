package scheduling_solution.visualisation;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.jgraph.JGraph;
import org.jgrapht.ext.JGraphModelAdapter;
import org.jgrapht.graph.DefaultDirectedWeightedGraph;
import org.jgrapht.graph.DefaultWeightedEdge;

import graph.Vertex;

@SuppressWarnings("serial")
public class GraphVisualisation extends JFrame {
	
	private JGraphModelAdapter<Vertex, DefaultWeightedEdge> jgAdapter;
	
	public GraphVisualisation(DefaultDirectedWeightedGraph<Vertex, DefaultWeightedEdge> defaultDirectedWeightedGraph) {
		setTitle("Process Visualisation");
		setExtendedState(JFrame.MAXIMIZED_BOTH); 
		
		JPanel graphContainer = new JPanel();
		
		jgAdapter = new JGraphModelAdapter<Vertex, DefaultWeightedEdge>(defaultDirectedWeightedGraph);
		JGraph jgraph = new JGraph(jgAdapter);
		graphContainer.add(jgraph);

		add(graphContainer);
		
		setVisible(true);
	}
	
}
