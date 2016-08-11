package scheduling_solution.astar;

import java.util.ArrayList;



/**
 * Class is used to represent a processor to store executed nodes and their start times
 * @author Team 8
 *
 */
public class Processor extends ArrayList<ProcessorTask>{
	
	
	public Processor clone() {
		Processor p = new Processor();
		
		for (ProcessorTask pTask : this ) {
			p.add(pTask.clone());
		}
		
		return p;
	}
}
