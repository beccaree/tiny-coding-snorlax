package scheduling_solution.astar;

import java.util.Comparator;

public class PartialSolutionComparator implements Comparator<PartialSolution> {

	@Override
	public int compare(PartialSolution p1, PartialSolution p2) {
		//TODO this is a BFS currently: not using the heuristic cause its returning the wrong answer
		int t1 = p1.getTimeLength();
		int t2 = p2.getTimeLength();
		/*TODO use f = g + h here, and just compare the fs like normal integers (lower is better)
		 h is the max of borromlevel of all scheduled nodes
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
