package scheduling_solution.astar;

/**
 * Stores the allocated processor number and start time of the vertex
 */
public class AllocationInfo {
	private final byte processor;
	private final int startTime;
	
	public AllocationInfo(byte processor, int startTime) {
		this.processor = processor;
		this.startTime = startTime;
	}
	
	public int getStartTime() {
		return startTime;
	}

	public byte getProcessorNumber() {
		return processor;
	}
	
	@Override
	public int hashCode() {
		return  Byte.valueOf(processor).hashCode() + Integer.valueOf(startTime).hashCode();
	}
	
	@Override
	public boolean equals(Object obj) {
		AllocationInfo a = (AllocationInfo) obj;
		return processor == a.getProcessorNumber() && startTime == a.getStartTime();
	}
}
