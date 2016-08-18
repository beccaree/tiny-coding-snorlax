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
	
	static Boolean programEnded = false;
	
	JLabel lblTimeElapsed = new JLabel("0.00s");
	JLabel lblNumbNodes = new JLabel("0");
	JLabel lblNumbProc = new JLabel("0");
	static JLabel lblOpenQ = new JLabel("0");
	static JLabel lblClosedQ = new JLabel("0");
	JLabel lblNumbThreads = new JLabel("0");
	
	public GraphVisualisation(Graph gsGraph, final long startTime, byte numbProc, String numbThreads) {
		setTitle("Process Visualisation");
		setBounds(50, 50, 900, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		JPanel information = new JPanel();
		information.setBorder(new EmptyBorder(20, 20, 20, 20));
		information.setLayout(new BoxLayout(information, BoxLayout.Y_AXIS));
		
		information.add(new JLabel("Time Elapsed:"));
		information.add(lblTimeElapsed);
		//create thread to print timer (time in milliseconds)
        Thread t = new Thread(new Runnable() {
        	
            @Override
            public void run() {
                while (!programEnded) {
                	try {
                		lblTimeElapsed.setText(format(System.currentTimeMillis() - startTime));
						Thread.sleep(10);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
                }
            }

			private String format(long t) {
				long seconds = t/1000;
				long milli = t - 1000*seconds;
				return seconds + "." + milli/10 + " s";
			}
        });
        t.start();

		information.add(new JLabel("No. of Nodes:"));
		lblNumbNodes.setText(Integer.toString(gsGraph.getNodeCount()));
		information.add(lblNumbNodes);
		
		information.add(new JLabel("No. of Processors:"));
		lblNumbProc.setText(Byte.toString(numbProc));
		information.add(lblNumbProc);
		
		information.add(new JLabel("Open queue size:"));
		information.add(lblOpenQ);
		
		information.add(new JLabel("Closed queue size:"));
		information.add(lblClosedQ);
		
		information.add(new JLabel("Threads used:"));
		lblNumbThreads.setText(numbThreads);
		information.add(lblNumbThreads);
		
		add(information, BorderLayout.WEST);
		
		Viewer viewer = new Viewer(gsGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout();
		ViewPanel view = viewer.addDefaultView(false);
		add(view, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	public static void stopTimer() {
		programEnded = true;
	}

	public static void updateQueueSize(int openSize, int closedSize) {
		// updates the labels in the display for open queue and closed queue
		lblOpenQ.setText(Integer.toString(openSize));
		lblClosedQ.setText(Integer.toString(closedSize));		
	}
	
}
