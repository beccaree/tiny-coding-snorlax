package scheduling_solution.visualisation;


import java.awt.BorderLayout;
<<<<<<< HEAD
import java.awt.Color;
import java.awt.Dimension;
=======
import java.awt.Image;
>>>>>>> 267c9347328398120b289c009c63dda7009b9630

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
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.TaskSeries;

import scheduling_solution.astar.AStarVisuals;
import scheduling_solution.astar.PartialSolution;

/**
 * 
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
	
	byte numbProc;
	CategoryPlot plot;

	private ColourArray colours;
	
	public GraphVisualisation(Graph gsGraph, long startTime, byte numProc, int numThreads, String inputFileName) {
		setTitle("A* Graph Visualisation - " + inputFileName);
		setBounds(0, 0, 900, 600);
		setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		
		if (numThreads == 0) { numThreads = 1; } // main thread
		
		this.numbProc = numProc;
		this.startTime = startTime;
		this.gsGraph = gsGraph;
		this.gsGraph.addAttribute("ui.stylesheet", "node {fill-mode: dyn-plain;}");
		this.openQlbls = new JLabel[numThreads];
		this.colours = new ColourArray();
		
		JPanel information = new JPanel();
		information.setBorder(new EmptyBorder(20, 20, 20, 20));
		information.setLayout(new BoxLayout(information, BoxLayout.Y_AXIS));
		
//		information.add(new JLabel("Time Elapsed:"));
//		information.add(lblTimeElapsed);
		        
		//Table Headers for Information
        String[] columns = new String[] {
            "Running Details", "Thing"
        };

       //Information data
       Object[][] data = new Object[][] {
    		   {"Time Elapsed:", lblTimeElapsed.getText()},
    		   {"No. of Vertices:",Integer.toString(gsGraph.getNodeCount())},
    		   {"No. of Processors:",Byte.toString(numProc)},
    		   {"Threads Used:",Integer.toString(numThreads)},
    		   {"Closed Set Size:", lblClosedQ.getText()},
       };
		
       
		// Create table with Statistics
		JTable table = new JTable(data, columns);
		
		DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
		centerRenderer.setHorizontalAlignment(SwingConstants.CENTER);
		table.getColumnModel().getColumn(0).setCellRenderer(centerRenderer);
		table.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );

		
		//create thread to print timer (time in milliseconds)
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
        
//        information.add(new JLabel("No. of Vertices:"));
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
<<<<<<< HEAD
				
=======
		
		information.add(new JLabel(new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\ColourKey.jpg").getImage().getScaledInstance(150, 70, Image.SCALE_DEFAULT))));
		
>>>>>>> 267c9347328398120b289c009c63dda7009b9630
		add(information, BorderLayout.WEST);
		
		Viewer viewer = new Viewer(this.gsGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout();
		vp = viewer.newViewerPipe();
		view = viewer.addDefaultView(false);
		add(view, BorderLayout.CENTER);
		
		setVisible(true);
	}
	
	private Object format(long l) {
		// TODO Auto-generated method stub
		return null;
	}

	public void stopTimer(PartialSolution p, AStarVisuals astar) {
		programEnded = true;
		JFrame frame = new JFrame("A* Search Details");
		frame.setBounds(900, 100, 700, 500);
		
		// Show details of the search after it is complete
		JPanel solutionDetails = new JPanel();
		solutionDetails.setBorder(new EmptyBorder(20, 20, 20, 20));
		BoxLayout b = new BoxLayout(solutionDetails, BoxLayout.Y_AXIS);
		
		solutionDetails.setLayout(b);
		
		
		//GANTT CHART
		GanttChart gantt = new GanttChart(p, numbProc);
				
		final IntervalCategoryDataset dataset = gantt.getDataSet();
				
		// create the chart...
		final JFreeChart chart = ChartFactory.createGanttChart(
				"Optimal Schedule",  // chart title
		        "Processor",              // domain axis label
		        "Time",              // range axis label
		        dataset,             // data
		        true,                // include legend
		        true,                // tooltips
		        false                // urls
		);
		this.plot = (CategoryPlot) chart.getPlot();
		        
		TaskSeries series = gantt.getTasks();
		GanttChartRenderer renderer = new GanttChartRenderer(series);
		plot.setRenderer(renderer);
		
		TimeAxis axis = new TimeAxis();
		plot.setRangeAxis(axis);
		
		final ChartPanel chartPanel = new ChartPanel(chart);
		chartPanel.setPreferredSize(new java.awt.Dimension(500, 370));
		solutionDetails.add(chartPanel);

		//STATISTICS TABLE
	    //Table Headers
        String[] columns = new String[] {
            "Statistics", "No."
        };
         
        long finishTime = System.currentTimeMillis();
        
        //Statistics
        Object[][] data = new Object[][] {
            {"Solutions created: ", astar.solutionsCreated},
            {"Solutions popped: ", astar.solutionsPopped},
            {"Solutions pruned: ", astar.solutionsPruned},
            {"Max memory (MB): ", astar.maxMemory /1024/1024},
            {"Time taken: ", (finishTime - startTime)},
        };
        
        //Create table with Statistics
        JTable table = new JTable(data, columns);
        
        DefaultTableCellRenderer centerRenderer = new DefaultTableCellRenderer();
        centerRenderer.setHorizontalAlignment( SwingConstants.CENTER );
        table.getColumnModel().getColumn(0).setCellRenderer( centerRenderer );
        table.getColumnModel().getColumn(1).setCellRenderer( centerRenderer );
        
        //Add table to a scroll pane
        JScrollPane pane = new JScrollPane(table);
        pane.setPreferredSize(new Dimension(500, 117));
        //Add the table to the frame
        solutionDetails.add(pane);

		
//		solutionDetails.add(new JLabel("Solutions created: " + astar.solutionsCreated));
//		solutionDetails.add(new JLabel("Solutions popped: " + astar.solutionsPopped));
//		solutionDetails.add(new JLabel("Solutions pruned: " + astar.solutionsPruned));
//		solutionDetails.add(new JLabel("Max memory (MB): " + astar.maxMemory /1024/1024));
//		long finishTime = System.currentTimeMillis();
//		solutionDetails.add(new JLabel("Time taken: " + (finishTime - startTime)));
		
		frame.add(solutionDetails);
		frame.setVisible(true);
	}

	public void updateQueueSize(int threadID, int openSize, int closeSize) {
		lblClosedQ.setText(Integer.toString(closeSize));
		openQlbls[threadID].setText(Integer.toString(openSize));
	}
	
	public void changeNodeColour(String name, int numUsed) {
		int i = numUsed / 1000;
		
		if (i > 30) { i = 30; }
			
		Node n = gsGraph.getNode(name);
		n.addAttribute("ui.color", colours.getColour(i));
		
		vp.pump();
	}
	
}
