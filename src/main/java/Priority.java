import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Priority scheduling class.
 *
 * @author Jared Scarr
 */
public class Priority implements ScheduleInterface {
    private PriorityQueue<Process> pq = this.getPriorityQueue(false);
    private final RR rr = new RR();
    private final List<Process> ioQueue = new ArrayList<>();
    private List<Process> processedList = new ArrayList<>();;
    private int algorithmTotalTime = 0;
    private boolean displayMode = true;
    private Process currentRunningProcess = null;

    /**
     * Default constructor.
     */
    public Priority() {}

    /**
     * Constructor with option for ascending or descending priority.
     * @param desc - boolean flag.
     */
    public Priority(boolean desc) {
        pq = getPriorityQueue(desc);
    }

    /**
     * Constructor with list of Processes passed as a List.
     * @param procList - List of Process classes.
     */
    public Priority(List<Process> procList) {
        pq.addAll(procList);
    }
    /**
     * Constructor with boolean flag and list of processes.
     * @param desc - boolean flag for asc or desc.
     * @param procList - List of Process classes.
     */
    public Priority(boolean desc, List<Process> procList) {
        pq.addAll(procList);
    }

    /**
     * Add a process to the PriorityQueue.
     * @param proc - Process class.
     */
    public void addProcess(Process proc) {
        pq.add(proc);
    }

    /**
     * Non-preemptive processing of the schedule.
     * @return - List of processes in the order that they were processed.
     */
    public List<Process> process() {
        List<Process> completedProcessList = null;
        // Get the first process
        Process currProc = null;

        while (!pq.isEmpty()) {
            currProc = pq.poll();

            if (displayMode) {
                currentRunningProcess = currProc;
                displayState(false);
            }

            if (currProc.getFirstRuntTime() == -1) {
                currProc.setFirstRunTime(algorithmTotalTime);
            }

            if (currProc.getCurrentState() == Process.State.WAITING) {
                // adds wait time to total wait time and total time
                currProc.wait(algorithmTotalTime - currProc.getEnterWaitState());
            }
            // Check for priorities of equal value and deal with
            // round-robin algorithm to prevent starvation
            while (currProc != null && pq.peek() != null && currProc.getPriority() == pq.peek().getPriority()) {
                if (currProc.getFirstRuntTime() == -1) {
                    currProc.setFirstRunTime(algorithmTotalTime);
                }
                rr.addProcess(currProc);
                currProc = pq.poll();
                if (currProc.getFirstRuntTime() == -1) {
                    currProc.setFirstRunTime(algorithmTotalTime);
                }
                currProc.wait(algorithmTotalTime - currProc.getEnterWaitState());
                rr.addProcess(currProc);
            }
            // run round-robin for equal priorities if they exists
            if (!rr.isEmpty()) {
                completedProcessList = rr.process();
                ioQueue.removeAll(completedProcessList);
                processedList.addAll(completedProcessList);
            } else {
                if (currProc.getCurrentState() == Process.State.IO) {
                    ioQueue.add(currProc);
                } else {
                    ioQueue.remove(currProc);
                }
                currProc.execute(currProc.nextBurstDuration());
                algorithmTotalTime += currProc.nextBurstDuration();
                // if finished remove from io processes
                // add to processedList else add back to priority queue
                if (currProc.isFinished()) {
                    ioQueue.remove(currProc);
                    processedList.add(currProc);
                } else {
                    pq.add(currProc);
                }
            }
        }
        return processedList;
    }

    /**
     * Display the snapshots of each process state.
     *
     * @param waitBetweenPages - boolean to wait for command line input or not.
     */
    @Override
    public void displayState(boolean waitBetweenPages) {
        System.out.println("Current Time: " + algorithmTotalTime);
        System.out.println();
        System.out.println("Next process on CPU: " +
                ((currentRunningProcess == null) ? "<none>" : currentRunningProcess.getName()) + ", duration: " +
                ((currentRunningProcess == null) ? "<none>" : currentRunningProcess.getCurrentDuration()));
        System.out.println(".......................................................");
        System.out.println();
        System.out.println("List of processes in the ready queue:");
        System.out.println();
        System.out.println("\t\tProcess\t\tBurst");
        for (Process p : pq) {
            System.out.println("\t\t\t" + p.getName() + "\t\t" + p.getCurrentDuration());
        }
        System.out.println();
        System.out.println(".......................................................");
        System.out.println("List of processes in I/O:");
        System.out.println();
        System.out.println("\t\tProcess\tRemaining I/O time");
        for (Process p : ioQueue) {
            System.out.println(p.getName());
            System.out.println("\t\t\t" + p.getName() + "\t\t" + p.getCurrentDuration());
        }
        System.out.println(".......................................................");
        System.out.println();
        System.out.print("Finished processes: ");
        for (Process p : processedList) System.out.print(p.getName() + " ");
        System.out.println();
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println();
        System.out.println();
        if (waitBetweenPages) {
            System.out.println("Press ENTER to continue...");
            try {
                System.in.read();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    /**
     * Set display mode.
     *
     * @param displayMode - true to show data else false.
     */
    @Override
    public void setDisplayMode(boolean displayMode) {
        this.displayMode = displayMode;
    }

    /**
     * Get the total number of clock ticks elapsed during the algorithm's run.
     *
     * @return
     */
    @Override
    public int getTotalElapsedTime() {
        return algorithmTotalTime;
    }

    /**
     * Get the total number of clock ticks when the CPU was idle during the algorithm's run.
     *
     * @return
     */
    @Override
    public int getTotalIdleCPUTime() {
        return 0;
    }

    /**
     * Return a PriorityQueue with ascending priority (default) or if desc flag
     * is true return a PriorityQueue with descending priority.
     * @param desc - boolean flag true for descending.
     * @return - PriorityQueue.
     */
    private PriorityQueue<Process> getPriorityQueue(boolean desc) {
        if (desc) {
            return new PriorityQueue<>(new Comparator<Process>() {
                public int compare(Process a, Process b) {
                    if (a.compareTo(b) > 0) {
                        return 1;
                    }
                    if (a.compareTo(b) < 0) {
                        return -1;
                    }
                    return 0;
                }
            });
        }
        return new PriorityQueue<>();
    }
}
