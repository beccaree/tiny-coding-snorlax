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
	
	final JTable infoTable;
	
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
		this.colours = new ColourArray();
		
		JPanel information = new JPanel();
		information.setBorder(new EmptyBorder(20, 20, 20, 20));
		information.setLayout(new BoxLayout(information, BoxLayout.Y_AXIS));
		        
		// Table Headers for Information
        String[] columns = new String[] {
            "Running Details", "Value"
        };
        // Initialise size and information data
        Object[][] data = new Object[numThreads+6][2];

        data[0][0]= "Time Elapsed:";
		data[0][1] = "0";
		data[1][0] = "No. of Vertices:";
		data[1][1] = Integer.toString(gsGraph.getNodeCount());
		data[2][0] = "No. of Processors:";
		data[2][1] = Byte.toString(numProc);
		data[3][0] = "Threads Used:" ;
		data[3][1] = Integer.toString(numThreads);
		data[4][0] = "Closed Set Size:";
		data[4][1] = "0";
		data[5][0] = "Open Queue (Size):";

		// For each thread, add row data
		for(int i = 0; i<numThreads; i++){
			data[i+6][0] = "- Thread " + (i+1);
			data[i+6][1] = "0";
		}
       
        // Create table with Statistics
		infoTable = new JTable(data, columns);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		infoTable.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		infoTable.getColumnModel().getColumn(1).setCellRenderer(centerRenderer);

		
		//create thread to update timer (time in milliseconds)
        Thread t = new Thread(new Runnable() {
        	
            @Override
            public void run() {
                while (!programEnded) {
                	try {
                		infoTable.setValueAt(format(System.currentTimeMillis() - startTime), 0, 1);
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
		
        //Add table to a scroll pane
        JScrollPane pane1 = new JScrollPane(infoTable);
        JPanel p = new JPanel();
        p.add(pane1);
        pane1.setPreferredSize(new Dimension(200, 0));
        
        //Add the table to the frame
        information.add(pane1);
        JTable thing = new JTable();
        information.add(thing);
		
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
		infoTable.setValueAt(Integer.toString(closeSize), (4), 1);
		infoTable.setValueAt(Integer.toString(openSize), (threadID + 6), 1);
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
		int i = numUsed / 1000;
		
		if (i > 30) { i = 30; }
			
		Node n = gsGraph.getNode(name);
		n.addAttribute("ui.color", colours.getColour(i));
		
		vp.pump();
	}
	
}
