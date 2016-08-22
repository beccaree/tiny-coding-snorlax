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

import scheduling_solution.astar.AStarVisuals;
import scheduling_solution.astar.PartialSolution;

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
	JLabel lblOpenQ = new JLabel("0");
	JLabel lblClosedQ = new JLabel("0");
	JLabel lblNumbThreads = new JLabel("0");

	private static long startTime;
	
	public GraphVisualisation(Graph gsGraph, final long startTime, byte numbProc, String numbThreads) {
		setTitle("Process Visualisation");
		setBounds(50, 50, 900, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		this.startTime = startTime;
		
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
	
	public void stopTimer(PartialSolution p, AStarVisuals astar) {
		programEnded = true;
		JFrame frame = new JFrame("A* Search Details");
		frame.setBounds(200, 150, 500, 300);
		
		// Show details of the search after it is complete
		JPanel solutionDetails = new JPanel();
		solutionDetails.setBorder(new EmptyBorder(20, 20, 20, 20));
		BoxLayout b = new BoxLayout(solutionDetails, BoxLayout.Y_AXIS);
		
		solutionDetails.setLayout(b);
		
		// Add gantt chart here to display p
		
		solutionDetails.add(new JLabel("Solutions created: " + astar.solutionsCreated));
		solutionDetails.add(new JLabel("Solutions popped: " + astar.solutionsPopped));
		solutionDetails.add(new JLabel("Solutions pruned: " + astar.solutionsPruned));
		solutionDetails.add(new JLabel("Max memory (MB): " + astar.maxMemory /1024/1024));
		long finishTime = System.currentTimeMillis();
		solutionDetails.add(new JLabel("Time taken: " + (finishTime - startTime)));
		
		frame.add(solutionDetails);
		frame.setVisible(true);
	}

	public void updateQueueSize(int openSize, int closedSize) {
		// updates the labels in the display for open queue and closed queue
		lblOpenQ.setText(Integer.toString(openSize));
		lblClosedQ.setText(Integer.toString(closedSize));		
	}
	
}
