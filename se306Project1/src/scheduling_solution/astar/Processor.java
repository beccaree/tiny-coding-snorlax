 package scheduling_solution.astar;

import java.util.HashSet;

/**
 * Class is used to represent a processor to store executed nodes and their start times
 * @author Team 8
 *
 */
public class Processor {
	private int finishTime = 0; 
	
	private int idleTime = 0;//TODO
	
	private HashSet<ProcessorTask> processorTasks = new HashSet<ProcessorTask>();//HashSet is faster than ArrayList I think
	
	/**
	 * Add a new task to the processor and calculate the new finish time of the processor
	 * @param p	task to add
	 */
	public void add(ProcessorTask p) {
		processorTasks.add(p);
		
		idleTime += (p.getStartTime() - finishTime);
		
		finishTime = p.getStartTime() + p.getVertex().getWeight();
	}
	
	/**
	 * Returns all the tasks that have been assigned to this processor
	 * @return
	 */
	public HashSet<ProcessorTask> tasks() {
		return processorTasks;
	}
	
	//TODO probably very slow. should increment after every addition instead of doing this
	//Actually after trying an incremental solution, the runtime wasnt significantly affected.
	public int getFinishTime() {
//		int maxFinishTime = 0;
//		for (ProcessorTask p : processorTasks) {
//			int finishTime = p.getStartTime() + p.getVertex().getWeight();
//			if (finishTime > maxFinishTime) {
//				maxFinishTime  = finishTime;
//			}
//		}
//		return maxFinishTime;
		return finishTime;
	}
	
	public int getIdleTime() {
		return idleTime;
	}
	
	public Processor clone() {
		Processor p = new Processor();
		
		for (ProcessorTask pTask : processorTasks ) {
			p.add(pTask.clone());
		}
		
		return p;
	}
	
	@Override
	public boolean equals(Object obj) {
		HashSet<ProcessorTask> processorTasks1  = ((Processor) obj).tasks();
		return processorTasks.equals(processorTasks1);
	}
	
	@Override
	public int hashCode() {
		return processorTasks.hashCode();
	}
	 
}
