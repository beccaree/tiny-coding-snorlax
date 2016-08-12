package scheduling_solution.astar;

import java.util.Comparator;

public class PartialSolutionComparator implements Comparator<PartialSolution> {

	@Override
	public int compare(PartialSolution p1, PartialSolution p2) {
		int t1 = p1.getTimeLength() + p1.getMaxBottomLevel();
		int t2 = p2.getTimeLength() + p2.getMaxBottomLevel();
		/*TODO use f = g + h here, and just compare the fs like normal integers (lower is better)
		 h is the max of borromlevel of all scheduled nodes
		 * I think the perfect load balance is not a heuristic, but is actually just a lower bound
		 */
		if (t1 > t2) {
			return 1;
		} else if (t2 > t1) {
			return -1;
		} else {
			return 0;
		}
	}

}
