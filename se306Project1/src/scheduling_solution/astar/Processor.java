package scheduling_solution.astar;

import java.util.ArrayList;



/**
 * Class is used to represent a processor to store executed nodes and their start times
 * @author Team 8
 *
 */
public class Processor {
	private int finishTime = 0; 
	
	private ArrayList<ProcessorTask> processorTasks = new ArrayList<ProcessorTask>();
	
	public void add(ProcessorTask p) {
		processorTasks.add(p);
		
		finishTime = p.getStartTime() + p.getVertex().getWeight();
	}
	
	public ArrayList<ProcessorTask> tasks() {
		return processorTasks;
	}
	
	public int getFinishTime() {
		return finishTime;
	}
	
	public Processor clone() {
		Processor p = new Processor();
		
		for (ProcessorTask pTask : processorTasks ) {
			p.add(pTask.clone());
		}
		
		return p;
	}
}
