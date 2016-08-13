package junit;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;

import junit.framework.TestCase;
import scheduling_solution.astar.Processor;
import scheduling_solution.astar.ProcessorTask;
import scheduling_solution.graph.Vertex;

public class TestProcessor extends TestCase {
	
	private Processor processor;
	private ProcessorTask pTask;
	private Vertex vertex;
	int tasksInProcessor;
	int NUM_OF_TASKS = 10;
	
	@Before
	protected void setUp() {
		vertex = new Vertex("a", 2);
		pTask = new ProcessorTask(vertex, 10, 1);
		processor = new Processor();		
	}

	@After
	protected void tearDown() {
		vertex = null;
		pTask = null;
		processor = null;
	}
	
	@Test
	public void testProcessorTasksAdded(){

		for(int i =0; i<NUM_OF_TASKS; i++){
			vertex = new Vertex(Integer.toString(i), i);
			pTask = new ProcessorTask(vertex, 10, 1);
			processor.add(pTask); //Add multiple tasks into processor
		}		
		tasksInProcessor = processor.tasks().size();
		assertEquals("Processor tasks not added into processor", 10, tasksInProcessor);
	}
	
	@Test
	public void testDuplicateProcessorTasksNotAdded(){
		for(int i =0; i<NUM_OF_TASKS; i++){
			processor.add(pTask); //Add multiples of 1 task into processor
		}
		tasksInProcessor = processor.tasks().size();
		assertEquals("Processor contains duplicate tasks", 1, tasksInProcessor);
	}
}
