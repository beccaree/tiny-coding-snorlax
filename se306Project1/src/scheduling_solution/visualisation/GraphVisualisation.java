package scheduling_solution.visualisation;

import java.awt.BorderLayout;

import javax.swing.JFrame;
import javax.swing.JPanel;

import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

/**
 * 
 * @author Team 8
 */
@SuppressWarnings("serial")
public class GraphVisualisation extends JFrame {
	
	public GraphVisualisation(Graph gsGraph) {
		setTitle("Process Visualisation");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		gsGraph.display();
		
//		Viewer viewer = new Viewer(gsGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
//		ViewPanel view = viewer.addDefaultView(false);
//		add(view, BorderLayout.CENTER);
		
//		setVisible(true);
	}
	
}
