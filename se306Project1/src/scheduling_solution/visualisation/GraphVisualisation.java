package scheduling_solution.visualisation;


import java.awt.BorderLayout;
import java.awt.Component;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.graphstream.graph.Graph;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;

/**
 * 
 * @author Team 8
 */
@SuppressWarnings("serial")
public class GraphVisualisation extends JFrame {
	
	Boolean programEnded = false;
	
	JLabel timeElapsed = new JLabel("00:00");
	JLabel numbNodes = new JLabel("0");
	JLabel numbProcessors = new JLabel("0");
	JLabel openQ = new JLabel("0");
	JLabel closedQ = new JLabel("0");
	JLabel minCost = new JLabel("0");
	JLabel numbThreads = new JLabel("0");
	
	public GraphVisualisation(Graph gsGraph, long startTime, int numbProc) {
		setTitle("Process Visualisation");
		setBounds(50, 50, 900, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel information = new JPanel();
		information.setBorder(new EmptyBorder(20, 20, 20, 20));
		information.setLayout(new BoxLayout(information, BoxLayout.Y_AXIS));
		
		information.add(new JLabel("Time Elapsed:"));
		information.add(timeElapsed);
		//create thread to print timer
        Thread t = new Thread(new Runnable() {
        	
            @Override
            public void run() {
                while (!programEnded) {
                	try {
                		timeElapsed.setText(format(System.currentTimeMillis() - startTime));
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }
            }

			private String format(long t) {
				long seconds = t/1000;
				long milli = t - 1000*seconds;
				return seconds + "s " + milli/10;
			}
        });
        t.start();

		information.add(new JLabel("No. of Nodes:"));
		numbNodes.setText(Integer.toString(gsGraph.getNodeCount()));
		information.add(numbNodes);
		
		information.add(new JLabel("No. of Processors:"));
		numbProcessors.setText(Integer.toString(numbProc));
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
	
	public void stopTimer() { //time in milliseconds
		programEnded = true;
	}
	
}
