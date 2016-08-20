//Pyjama compiler version:v1.5.4
package scheduling_solution.astar;

import java.util.HashSet;
import java.util.PriorityQueue;
import java.util.Set;
import org.jgrapht.graph.DefaultWeightedEdge;
import scheduling_solution.graph.GraphInterface;
import scheduling_solution.graph.Vertex;
import scheduling_solution.visualisation.GraphVisualisation;

import pj.pr.*;
import pj.PjRuntime;
import pj.Pyjama;
import pi.*;
import java.util.concurrent.*;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.atomic.AtomicReference;
import java.util.concurrent.locks.ReentrantLock;
import javax.swing.SwingUtilities;
import java.lang.reflect.InvocationTargetException;
import pj.pr.exceptions.OmpParallelRegionLocalCancellationException;

public class AStarPyjama {

    public GraphInterface<Vertex, DefaultWeightedEdge> graph = null;

    public static HashSet<Vertex> startingVertices = null;

    PriorityQueue<PartialSolution> unexploredSolutions = null;

    Set<PartialSolution> exploredSolutions = null;

    byte numProcessors = 0;

    public static Integer sequentialTime = null;

    public int solutionsPopped = 0;

    public int solutionsCreated = 0;

    public int solutionsPruned = 0;

    public long maxMemory = 0;

    public AStarPyjama(GraphInterface<Vertex, DefaultWeightedEdge> graph, byte numProcessors, int nThreads) {
        this.graph = graph;
        unexploredSolutions = new PriorityQueue(1000, new PartialSolutionComparator());
        exploredSolutions = new HashSet();
        startingVertices = new HashSet();
        this.numProcessors = numProcessors;
        Pyjama.omp_set_num_threads(nThreads);
    }

    /**
	 * Calculates the optimal solution
	 * @param graph - weighted digraph
	 * @return optimal PartialSolution object
	 */
    public PartialSolution calculateOptimalSolution() {{
        sequentialTime = new Integer(0);
        for (Vertex v : graph.vertexSet()) {
            sequentialTime += v.getWeight();
        }
        initialiseStartingVertices();
        initialiseStartStates();
        while (true) {
            solutionsPopped++;
            maxMemory = Math.max(maxMemory, Runtime.getRuntime().totalMemory());
            PartialSolution currentSolution = unexploredSolutions.poll();
            if (isComplete(currentSolution)) {
                return currentSolution;
            } else {
                Vertex[] availableVerticesArray = currentSolution.getAvailableVertices().toArray(new Vertex[currentSolution.getAvailableVertices().size()]);
                int numAvailableVertices = availableVerticesArray.length;
                solutionsCreated += (numAvailableVertices * numProcessors);
                /*OpenMP Parallel region (#0) -- START */
                InternalControlVariables icv_previous__OMP_ParallelRegion_0 = PjRuntime.getCurrentThreadICV();
                InternalControlVariables icv__OMP_ParallelRegion_0 = PjRuntime.inheritICV(icv_previous__OMP_ParallelRegion_0);
                int _threadNum__OMP_ParallelRegion_0 = icv__OMP_ParallelRegion_0.nthreads_var.get(icv__OMP_ParallelRegion_0.levels_var);
                ConcurrentHashMap<String, Object> inputlist__OMP_ParallelRegion_0 = new ConcurrentHashMap<String,Object>();
                ConcurrentHashMap<String, Object> outputlist__OMP_ParallelRegion_0 = new ConcurrentHashMap<String,Object>();
                inputlist__OMP_ParallelRegion_0.put("numAvailableVertices",numAvailableVertices);
                inputlist__OMP_ParallelRegion_0.put("availableVerticesArray",availableVerticesArray);
                inputlist__OMP_ParallelRegion_0.put("exploredSolutions",exploredSolutions);
                inputlist__OMP_ParallelRegion_0.put("currentSolution",currentSolution);
                inputlist__OMP_ParallelRegion_0.put("unexploredSolutions",unexploredSolutions);
                _OMP_ParallelRegion_0 _OMP_ParallelRegion_0_in = new _OMP_ParallelRegion_0(_threadNum__OMP_ParallelRegion_0,icv__OMP_ParallelRegion_0,inputlist__OMP_ParallelRegion_0,outputlist__OMP_ParallelRegion_0);
                _OMP_ParallelRegion_0_in.runParallelCode();
                numAvailableVertices = (Integer)outputlist__OMP_ParallelRegion_0.get("numAvailableVertices");
                availableVerticesArray = (Vertex[])outputlist__OMP_ParallelRegion_0.get("availableVerticesArray");
                exploredSolutions = (Set<PartialSolution>)outputlist__OMP_ParallelRegion_0.get("exploredSolutions");
                currentSolution = (PartialSolution)outputlist__OMP_ParallelRegion_0.get("currentSolution");
                unexploredSolutions = (PriorityQueue<PartialSolution>)outputlist__OMP_ParallelRegion_0.get("unexploredSolutions");
                PjRuntime.recoverParentICV(icv_previous__OMP_ParallelRegion_0);
                RuntimeException OMP_ee_0 = (RuntimeException) _OMP_ParallelRegion_0_in.OMP_CurrentParallelRegionExceptionSlot.get();
                if (OMP_ee_0 != null) {throw OMP_ee_0;}
                /*OpenMP Parallel region (#0) -- END */

                exploredSolutions.add(currentSolution);
            }
        }
    }
    }
class _OMP_ParallelRegion_0{
        private int OMP_threadNumber = 1;
        private InternalControlVariables icv;
        private ConcurrentHashMap<String, Object> OMP_inputList = new ConcurrentHashMap<String, Object>();
        private ConcurrentHashMap<String, Object> OMP_outputList = new ConcurrentHashMap<String, Object>();
        private ReentrantLock OMP_lock;
        private ParIterator<?> OMP__ParIteratorCreator;
        public AtomicReference<Throwable> OMP_CurrentParallelRegionExceptionSlot = new AtomicReference<Throwable>(null);

        //#BEGIN shared variables defined here
        int numAvailableVertices = 0;
        Set<PartialSolution> exploredSolutions = null;
        PriorityQueue<PartialSolution> unexploredSolutions = null;
        Vertex[] availableVerticesArray = null;
        PartialSolution currentSolution = null;
        //#END shared variables defined here
        public _OMP_ParallelRegion_0(int thread_num, InternalControlVariables icv, ConcurrentHashMap<String, Object> inputlist, ConcurrentHashMap<String, Object> outputlist) {
            this.icv = icv;
            if ((false == Pyjama.omp_get_nested()) && (Pyjama.omp_get_level() > 0)) {
                this.OMP_threadNumber = 1;
            }else {
                this.OMP_threadNumber = thread_num;
            }
            this.OMP_inputList = inputlist;
            this.OMP_outputList = outputlist;
            icv.currentParallelRegionThreadNumber = this.OMP_threadNumber;
            icv.OMP_CurrentParallelRegionBarrier = new PjCyclicBarrier(this.OMP_threadNumber);
            //#BEGIN shared variables initialised here
            numAvailableVertices = (Integer)OMP_inputList.get("numAvailableVertices");
            exploredSolutions = (Set<PartialSolution>)OMP_inputList.get("exploredSolutions");
            unexploredSolutions = (PriorityQueue<PartialSolution>)OMP_inputList.get("unexploredSolutions");
            availableVerticesArray = (Vertex[])OMP_inputList.get("availableVerticesArray");
            currentSolution = (PartialSolution)OMP_inputList.get("currentSolution");
            //#END shared variables initialised here
        }

        private void updateOutputListForSharedVars() {
            //BEGIN update outputlist
            OMP_outputList.put("numAvailableVertices",numAvailableVertices);
            OMP_outputList.put("availableVerticesArray",availableVerticesArray);
            OMP_outputList.put("exploredSolutions",exploredSolutions);
            OMP_outputList.put("currentSolution",currentSolution);
            OMP_outputList.put("unexploredSolutions",unexploredSolutions);
            //END update outputlist
        }
        class MyCallable implements Callable<Void> {
            private int alias_id;
            private ConcurrentHashMap<String, Object> OMP_inputList;
            private ConcurrentHashMap<String, Object> OMP_outputList;
            //#BEGIN private/firstprivate reduction variables defined here
            //#END private/firstprivate reduction variables  defined here
            MyCallable(int id, ConcurrentHashMap<String,Object> inputlist, ConcurrentHashMap<String,Object> outputlist){
                this.alias_id = id;
                this.OMP_inputList = inputlist;
                this.OMP_outputList = outputlist;
                //#BEGIN firstprivate reduction variables initialised here
                //#END firstprivate reduction variables initialised here
            }

            @Override
            public Void call() {
                try {
                    /****User Code BEGIN***/
                    /*OpenMP Work Share region (#1) -- START */
                    
                {//#BEGIN firstprivate lastprivate reduction variables defined and initialized here
                    int OMP_WoRkShArInG_PRIVATE_1numAvailableVertices = numAvailableVertices;
                    //#set implicit barrier here, otherwise unexpected initial value happens
                    PjRuntime.setBarrier();
                    //#END firstprivate lastprivate reduction variables defined and initialized here
                    try{
                        int i_proc=0;
                        int OMP_iterator = 0;
                        int OMP_end = (int)((numAvailableVertices * numProcessors)-(0))/(1);
                        if (((numAvailableVertices * numProcessors)-(0))%(1) == 0) {
                            OMP_end = OMP_end - 1;
                        }
                        int OMP_local_iterator = 0;
                        int OMP_Chunk_Starting_point = 0;
                        int OMP_Default_chunkSize_autoGenerated = (OMP_end+1)/Pyjama.omp_get_num_threads();
                        if (Pyjama.omp_get_thread_num() < (OMP_end+1) % Pyjama.omp_get_num_threads()) {
                            ++OMP_Default_chunkSize_autoGenerated;
                            OMP_Chunk_Starting_point = Pyjama.omp_get_thread_num() * OMP_Default_chunkSize_autoGenerated;
                        } else {
                            OMP_Chunk_Starting_point = Pyjama.omp_get_thread_num() * OMP_Default_chunkSize_autoGenerated + (OMP_end+1) % Pyjama.omp_get_num_threads();
                        }
                        for (OMP_local_iterator=OMP_Chunk_Starting_point; OMP_local_iterator<OMP_Chunk_Starting_point+OMP_Default_chunkSize_autoGenerated && OMP_Default_chunkSize_autoGenerated>0; ++OMP_local_iterator) {
                            i_proc = 0 + OMP_local_iterator * (1);
                            {
                                int i = i_proc % OMP_WoRkShArInG_PRIVATE_1numAvailableVertices;
                                byte processor = (byte) (i_proc / OMP_WoRkShArInG_PRIVATE_1numAvailableVertices);
                                PartialSolution newSolution = new PartialSolution(graph, currentSolution, availableVerticesArray[i], processor);
                                if (isViable(newSolution)) {
                                    unexploredSolutions.add(newSolution);
                                }
                            }if (OMP_end == OMP_local_iterator) {
                                //BEGIN lastprivate variables value set
                                //END lastprivate variables value set
                            }
                        }
                    } catch (pj.pr.exceptions.OmpWorksharingLocalCancellationException wse){
                    } catch (Exception e){throw e;}
                    //BEGIN  reduction
                    PjRuntime.reductionLockForWorksharing.lock();
                    PjRuntime.reductionLockForWorksharing.unlock();//END reduction
                    PjRuntime.setBarrier();
                }

                    PjRuntime.setBarrier();
                    PjRuntime.reset_OMP_orderCursor();
                    /*OpenMP Work Share region (#1) -- END */

                    /****User Code END***/
                    //BEGIN reduction procedure
                    //END reduction procedure
                    PjRuntime.setBarrier();
                } catch (OmpParallelRegionLocalCancellationException e) {
                 	PjRuntime.decreaseBarrierCount();
                } catch (Exception e) {
                    PjRuntime.decreaseBarrierCount();
                	PjExecutor.cancelCurrentThreadGroup();
                OMP_CurrentParallelRegionExceptionSlot.compareAndSet(null, e);
            }
            if (0 == this.alias_id) {
                updateOutputListForSharedVars();
            }
            return null;
        }
    }
    public void runParallelCode() {
        for (int i = 1; i <= this.OMP_threadNumber-1; i++) {
            Callable<Void> slaveThread = new MyCallable(i, OMP_inputList, OMP_outputList);
            PjRuntime.submit(i, slaveThread, icv);
        }
        Callable<Void> masterThread = new MyCallable(0, OMP_inputList, OMP_outputList);
        PjRuntime.getCurrentThreadICV().currentThreadAliasID = 0;
        try {
            masterThread.call();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}




    /**
	 * Initialises the PriorityQueue with the possible starting states
	 */
    public void initialiseStartStates() {{
        for (Vertex v : startingVertices) {
            unexploredSolutions.add(new PartialSolution(graph, numProcessors, v, (byte) 0));
        }
    }
    }


    public void initialiseStartingVertices() {{
        for (Vertex v : graph.vertexSet()) {
            if (graph.inDegreeOf(v) == 0) {
                startingVertices.add(v);
            }
        }
    }
    }


    /**
	 * Checks if partial solution has allocated all vertices
	 * @param Partical solution to check
	 * @return	True - all vertices have been allocated
	 */
    public boolean isComplete(PartialSolution p) {{
        return p.getUnallocatedVertices().size() == 0;
    }
    }


    public PriorityQueue<PartialSolution> getUnexploredSolutions() {{
        return unexploredSolutions;
    }
    }


    public static int getSequentialTime() {{
        return sequentialTime;
    }
    }


    /**
	 * Checks to see if a solution has no chance of being an optimal solution, using all pruning/bound checks
	 * Can get a simple upper bound by adding all vertices together (== running them all sequentially on one processor)
	 * Should check if it exists in the exploredSolutions Set
	 * @param partialSolution
	 * @return True - if the given ParticalSolution has a chance of being an optimal solution
	 */
    public boolean isViable(PartialSolution partialSolution) {{
        if (exploredSolutions.contains(partialSolution) || partialSolution.getMinimumFinishTime() > sequentialTime) {
            solutionsPruned++;
            return false;
        }
        return true;
    }
    }

}
