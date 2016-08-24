package scheduling_solution.visualisation;


import java.awt.BorderLayout;
import java.awt.Image;

import javax.swing.BoxLayout;
import javax.swing.ImageIcon;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

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
	
	public GraphVisualisation(Graph gsGraph, long startTime, byte numProc, int numThreads) {
		setTitle("Process Visualisation");
		setBounds(50, 50, 900, 600);
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
        
        information.add(new JLabel("No. of Vertices:"));
		lblNumbNodes.setText(Integer.toString(gsGraph.getNodeCount()));
		information.add(lblNumbNodes);
		
		information.add(new JLabel("No. of Processors:"));
		lblNumbProc.setText(Byte.toString(numProc));
		information.add(lblNumbProc);
		
		information.add(new JLabel("Threads used:"));
		lblNumbThreads.setText(Integer.toString(numThreads));
		information.add(lblNumbThreads);
		
		information.add(new JLabel("Closed set size:"));
		information.add(lblClosedQ);
		
		for (int i = 0; i < numThreads; i++) {
			information.add(new JLabel("Open queue size thread " + (i+1) + ":"));
			openQlbls[i] = new JLabel("0");
			information.add(openQlbls[i]);
		}
		
		information.add(new JLabel(new ImageIcon(new ImageIcon(System.getProperty("user.dir") + "\\ColourKey.jpg").getImage().getScaledInstance(150, 70, Image.SCALE_DEFAULT))));
		
		add(information, BorderLayout.WEST);
		
		Viewer viewer = new Viewer(this.gsGraph, Viewer.ThreadingModel.GRAPH_IN_ANOTHER_THREAD);
		viewer.enableAutoLayout();
		vp = viewer.newViewerPipe();
		view = viewer.addDefaultView(false);
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
        
		
		solutionDetails.add(new JLabel("Solutions created: " + astar.solutionsCreated));
		solutionDetails.add(new JLabel("Solutions popped: " + astar.solutionsPopped));
		solutionDetails.add(new JLabel("Solutions pruned: " + astar.solutionsPruned));
		solutionDetails.add(new JLabel("Max memory (MB): " + astar.maxMemory /1024/1024));
		long finishTime = System.currentTimeMillis();
		solutionDetails.add(new JLabel("Time taken: " + (finishTime - startTime)));
		
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
