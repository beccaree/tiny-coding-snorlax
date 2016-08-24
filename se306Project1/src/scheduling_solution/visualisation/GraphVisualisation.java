package scheduling_solution.visualisation;

import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.graphstream.graph.Graph;
import org.graphstream.graph.Node;
import org.graphstream.ui.swingViewer.ViewPanel;
import org.graphstream.ui.view.Viewer;
import org.graphstream.ui.view.ViewerPipe;
import scheduling_solution.astar.AStarVisuals;
import scheduling_solution.astar.PartialSolution;

/**
 * Visualisation class to display search progress and information of scheduler
 * @author Team 8
 */
@SuppressWarnings("serial")
public class GraphVisualisation extends JFrame {
	
	static Boolean programEnded = false;
	
	Graph gsGraph;
	private long startTime;
	
	JLabel lblTimeElapsed = new JLabel("0.00s");
	JLabel lblNumbNodes = new JLabel("0");
	JLabel lblNumbProc = new JLabel("0");
	JLabel lblNumbThreads = new JLabel("0");
	JLabel lblClosedQ = new JLabel("0");
	
	JLabel[] openQlbls;
	
	ViewPanel view;
	ViewerPipe vp;
	
	byte numProc;

	private ColourArray colours;
	
	public GraphVisualisation(Graph gsGraph, final long startTime, byte numProc, int numThreads, String inputFileName) {
		setTitle("A* Graph Visualisation - " + inputFileName);
		setBounds(0, 0, 900, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		if (numThreads == 0) { numThreads = 1; } // main thread
		
		this.numProc = numProc;
		this.startTime = startTime;
		this.gsGraph = gsGraph;
		this.gsGraph.addAttribute("ui.stylesheet", "node {fill-mode: dyn-plain;}");
		this.openQlbls = new JLabel[numThreads];
		this.colours = new ColourArray();
		
		JPanel information = new JPanel();
		information.setBorder(new EmptyBorder(20, 20, 20, 20));
		information.setLayout(new BoxLayout(information, BoxLayout.Y_AXIS));
		        
		// Table Headers for Information
        String[] columns = new String[] {
            "Running Details", "Thing"
        };
        // Information data
        Object[][] data = new Object[][] {
    		   {"Time Elapsed:", lblTimeElapsed.getText()},
    		   {"No. of Vertices:",Integer.toString(gsGraph.getNodeCount())},
    		   {"No. of Processors:",Byte.toString(numProc)},
    		   {"Threads Used:",Integer.toString(numThreads)},
    		   {"Closed Set Size:", lblClosedQ.getText()},
        };
		
       
        // Create table with Statistics
		final JTable table = new JTable(data, columns);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

		
		//create thread to update timer (time in milliseconds)
        Thread t = new Thread(new Runnable() {
        	
            @Override
            public void run() {
                while (!programEnded) {
                	try {
                		table.setValueAt(format(System.currentTimeMillis() - startTime), 0, 1);
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
        
//      information.add(new JLabel("No. of Vertices:"));
//		lblNumbNodes.setText(Integer.toString(gsGraph.getNodeCount()));
//		information.add(lblNumbNodes);
//		
//		information.add(new JLabel("No. of Processors:"));
//		lblNumbProc.setText(Byte.toString(numProc));
//		information.add(lblNumbProc);
//		
//		information.add(new JLabel("Threads Used:"));
//		lblNumbThreads.setText(Integer.toString(numThreads));
//		information.add(lblNumbThreads);
//		
//		information.add(new JLabel("Closed Set Size:"));
//		information.add(lblClosedQ);
		
        //Add table to a scroll pane
        JScrollPane pane1 = new JScrollPane(table);
        pane1.setPreferredSize(new Dimension(200, 50));
        //Add the table to the frame
        information.add(pane1);
        
		for (int i = 0; i < numThreads; i++) {
			information.add(new JLabel("Open Queue Size - Thread " + (i+1) + ":"));
			openQlbls[i] = new JLabel("0");
			//table.add(new Object[]{("Open Queue Size - Thread " + (i+1) + ":"), openQlbls[i]});
			information.add(openQlbls[i]);
		}
		
		// Add the image of the key for vertex colour
		information.add(new JLabel(new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "/ColourKey.jpg").getImage().getScaledInstance(150, 70, Image.SCALE_DEFAULT))));

		add(information, BorderLayout.WEST);
		
		// Add the input graph stream graph into the JPanel
		Viewer viewer = new Viewer(this.gsGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout();
		vp = viewer.newViewerPipe(); // Allows us to make updates to the displayed graph
		view = viewer.addDefaultView(false);
		add(view, BorderLayout.CENTER);
		
		setVisible(true);
	}


	/**
	 * Firstly stops the timer by setting programEnded to true, them uses the information stored 
	 * in the final solution and scheduler statistics in astar to display a meaningful Gantt chart.
	 * 
	 * @param p - final solution
	 * @param astar
	 */
	public void stopTimer(PartialSolution p, AStarVisuals astar) {
		programEnded = true;
		
		// Display scheduler results 
		new FinalDetails(p, astar, startTime, numProc);
	}

	/**
	 * Updates the labels of closed queue and updates the open queue size label for the
	 * corresponding thread number.
	 * 
	 * @param threadID
	 * @param openSize
	 * @param closeSize
	 */
	public void updateQueueSize(int threadID, int openSize, int closeSize) {
		lblClosedQ.setText(Integer.toString(closeSize));
		openQlbls[threadID].setText(Integer.toString(openSize));
	}
	
	/**
	 * Changes the vertex colour of the vertex with name according to the number of times is has
	 * been allocated. The colours range from green to yellow to red, with green representing
	 * approximately 1-1000 calls, while red represents around 31000+ calls.
	 * 
	 * @param name
	 * @param numUsed
	 */
	public void changeNodeColour(String name, int numUsed) {
		int i = numUsed / 10000;
		
		if (i > 30) { i = 30; }
			
		Node n = gsGraph.getNode(name);
		n.addAttribute("ui.color", colours.getColour(i));
		
		vp.pump();
	}
	
}
