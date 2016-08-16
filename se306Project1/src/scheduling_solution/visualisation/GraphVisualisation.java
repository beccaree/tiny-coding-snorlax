package scheduling_solution.visualisation;


import java.awt.BorderLayout;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
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
	
	JLabel timeElapsed = new JLabel("00:00");
	JLabel numbNodes = new JLabel("0");
	JLabel numbProcessors = new JLabel("0");
	JLabel openQ = new JLabel("0");
	JLabel closedQ = new JLabel("0");
	JLabel minCost = new JLabel("0");
	JLabel numbThreads = new JLabel("0");
	
	public GraphVisualisation(Graph gsGraph) {
		setTitle("Process Visualisation");
		setExtendedState(JFrame.MAXIMIZED_BOTH);
		
		JPanel information = new JPanel();
		information.setLayout(new BoxLayout(information, BoxLayout.Y_AXIS));
		
		information.add(new JLabel("Time Elapsed:"));
		information.add(timeElapsed);
		information.add(new JLabel("No. of Nodes:"));
		information.add(numbNodes);
		information.add(new JLabel("No. of Processors:"));
		information.add(numbProcessors);
		information.add(new JLabel("Open queue size:"));
		information.add(openQ);
		information.add(new JLabel("Closed queue size:"));
		information.add(closedQ);
		information.add(new JLabel("Min cost function:"));
		information.add(minCost);
		information.add(new JLabel("Threads used:"));
		information.add(numbThreads);
		
		add(information, BorderLayout.WEST);
		
		Viewer viewer = new Viewer(gsGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout();
		ViewPanel view = viewer.addDefaultView(false);
		add(view, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public void updateTime(int currentTime) { //time in milliseconds
		timeElapsed.setText(Integer.toString(currentTime));
	}
	
}
