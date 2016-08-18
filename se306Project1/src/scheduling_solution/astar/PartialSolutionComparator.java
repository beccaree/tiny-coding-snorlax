package scheduling_solution.astar;

import java.util.Comparator;

public class PartialSolutionComparator implements Comparator<PartialSolution> {

	@Override
	/**
	 * Compares two PartialSolutions to determine which solution will have a quicker estimated runtime
	 * returns 	1 - Second partialSolution is quicker
	 * 		   -1 - First partialSolution is quicker
	 * 			0 - Both  partialSolution are equal
	 */
	public int compare(PartialSolution p1, PartialSolution p2) {
		int t1 = p1.getMinimumFinishTime();
		int t2 = p2.getMinimumFinishTime();
		
		if (t1 > t2) {
			return 1;
		} else if (t2 > t1) {
			return -1;
		} else {
			return 0;
		}
	}

}
