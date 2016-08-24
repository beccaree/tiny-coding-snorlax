package scheduling_solution.visualisation;

import java.awt.Dimension;

import javax.swing.BoxLayout;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.SwingConstants;
import javax.swing.border.EmptyBorder;
import javax.swing.table.DefaultTableCellRenderer;

import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.TaskSeries;

import scheduling_solution.astar.AStarVisuals;
import scheduling_solution.astar.PartialSolution;

/**
 * Displays final pop-up that is visible upon search completion
 * 
 * @author team 8
 *
 */
@SuppressWarnings("serial")
public class FinalDetails extends JFrame {
	
	CategoryPlot plot;
	
	public FinalDetails(PartialSolution p, AStarVisuals astar, long startTime, byte numProc) {
		setTitle("A* Search Details");
		setBounds(900, 100, 700, 500);
		
		// Show details of the search after it is complete
		JPanel solutionDetails = new JPanel();
		solutionDetails.setBorder(new EmptyBorder(20, 20, 20, 20));
		BoxLayout b = new BoxLayout(solutionDetails, BoxLayout.Y_AXIS);
		
		solutionDetails.setLayout(b);
		
		//GANTT CHART
		GanttChart gantt = new GanttChart(p, numProc);
				
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
		
		add(solutionDetails);
		setVisible(true);
	}

}
