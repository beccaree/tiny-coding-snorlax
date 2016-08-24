package scheduling_solution.visualisation;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.Map;

import org.jfree.data.category.IntervalCategoryDataset;
import org.jfree.data.gantt.Task;
import org.jfree.data.gantt.TaskSeries;
import org.jfree.data.gantt.TaskSeriesCollection;

import scheduling_solution.astar.AllocationInfo;
import scheduling_solution.astar.PartialSolution;
import scheduling_solution.graph.Vertex;

/**
 * Class that creates the details for the gantt chart. This class creates all the Task objects for each
 * vertex and assigns them starting and finishing times. This class utilises GanttChartRenderer to 
 * draw its tasks and TimeAxis to draw its time unit axis.
 */
public class GanttChart {
	
	PartialSolution partialSolution;
	byte numProc;
	TaskSeries series;

	/**
	 * @param partialSolution - optimal partial schedule
	 * @param numProc - number of processors
	 */
	public GanttChart(PartialSolution partialSolution, byte numProc) {
		this.partialSolution = partialSolution;
		this.numProc = numProc;
	}
	
	/**
	 * This method creates the dataset to be used in the gantt chart.
	 * @return
	 */
	IntervalCategoryDataset getDataSet() {
		
		series = new TaskSeries("Scheduled Task");
		int[] processorStartTimes = new int[numProc];
		int[] processorFinishTimes = new int[numProc];
		ArrayList<Task> procTasks = new ArrayList<Task>();
		
		//Initialise start times of processors as the start time of a random vertex in that processor
		for (int i = 0; i < numProc; i++) {
			for (Map.Entry<Vertex, AllocationInfo> entry : partialSolution.getAllocatedVertices().entrySet()) {
				AllocationInfo a = entry.getValue();
				
				if (a.getProcessorNumber() == i) {
					processorStartTimes[i] = a.getStartTime();
					break;
				}
			}
		}
		
		// Find start and finish times of each processor
		for (Map.Entry<Vertex, AllocationInfo> entry : partialSolution.getAllocatedVertices().entrySet()) {
			Vertex v = entry.getKey();
			AllocationInfo a = entry.getValue();
			
			processorFinishTimes[a.getProcessorNumber()] = 
					Math.max(processorFinishTimes[a.getProcessorNumber()], a.getStartTime()+v.getWeight());
			
			processorStartTimes[a.getProcessorNumber()] = 
					Math.min(processorStartTimes[a.getProcessorNumber()], a.getStartTime());
		}
		
		// Create and store the Overall Task for each processor
		for (int i = 0; i < numProc; i++) {
			Task procTask = new Task("Processor "+(i+1), date(processorStartTimes[i]), date(processorFinishTimes[i]));
			procTasks.add(procTask);
		}
		
		// Create tasks for each vertex and add them as subtasks to the overall processor task
		for (Map.Entry<Vertex, AllocationInfo> entry : partialSolution.getAllocatedVertices().entrySet()) {
			Vertex v = entry.getKey();
			AllocationInfo a = entry.getValue();
			
			Task task = new Task(v.getName(), date(a.getStartTime()), date(a.getStartTime()+v.getWeight()));
			
			procTasks.get(a.getProcessorNumber()).addSubtask(task);
		}
		
		// Finally, add the overall processor tasks to the Gantt chart
		for (Task procTask : procTasks) {
			series.add(procTask);
		}
        
        final TaskSeriesCollection collection = new TaskSeriesCollection();
        collection.add(series);

        return collection;
	}
	
	/**
	 * This method creates a Date object out of the time unit supplied
	 * @param second - time units
	 * @return
	 */
	private static Date date(final int second) {

        final Calendar calendar = Calendar.getInstance();
        calendar.set(2016, Calendar.AUGUST, 22, 0, 0, second);
        final Date result = calendar.getTime();
        return result;
    }
	
	public TaskSeries getTasks() {
		return series;
	}
}
