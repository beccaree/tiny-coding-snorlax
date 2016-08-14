package scheduling_solution.astar;

import java.util.Comparator;

public class PartialSolutionComparator implements Comparator<PartialSolution> {

	@Override
	public int compare(PartialSolution p1, PartialSolution p2) {
		int t1 = p1.getMinimumTime();
		int t2 = p2.getMinimumTime();
		
		if (t1 > t2) {
			return 1;
		} else if (t2 > t1) {
			return -1;
		} else {
			return 0;
		}
	}

}
