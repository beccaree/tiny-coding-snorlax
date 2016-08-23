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

public class GanttChart {
	
	PartialSolution partialSolution;
	byte numProc;
	TaskSeries series;

	public GanttChart(PartialSolution partialSolution, byte numProc) {
		this.partialSolution = partialSolution;
		this.numProc = numProc;
	}
	
	IntervalCategoryDataset getDataSet() {
		
		series = new TaskSeries("Scheduled");
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
			Task procTask = new Task("Processor "+i, date(processorStartTimes[i]), date(processorFinishTimes[i]));
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
