import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Priority scheduling class.
 *
 * @author Jared Scarr
 */
public class Priority {
    private final PriorityQueue<Process> pq;
    private final RR rr;
    private List<Process> processedList;
    private int algorithmTotalTime = 0;

    /**
     * Default constructor.
     */
    public Priority() {
        pq = getPriorityQueue(false);
        processedList = new ArrayList<>();
        rr = new RR();
    }

    /**
     * Constructor with option for ascending or descending priority.
     * @param desc - boolean flag.
     */
    public Priority(boolean desc) {
        pq = getPriorityQueue(desc);
        processedList = new ArrayList<>();
        rr = new RR();
    }

    /**
     * Constructor with list of Processes passed as a List.
     * @param procList - List of Process classes.
     */
    public Priority(List<Process> procList) {
        pq = getPriorityQueue(false);
        pq.addAll(procList);
        processedList = new ArrayList<>();
        rr = new RR();
    }

    /**
     * Constructor with boolean flag and list of processes.
     * @param desc - boolean flag for asc or desc.
     * @param procList - List of Process classes.
     */
    public Priority(boolean desc, List<Process> procList) {
        pq = getPriorityQueue(desc);
        pq.addAll(procList);
        processedList = new ArrayList<>();
        rr = new RR();
    }

    /**
     * Add a process to the PriorityQueue.
     * @param proc - Process class.
     */
    public void addProcess(Process proc) {
        pq.add(proc);
        processedList = new ArrayList<>();
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
            // If multiple processes with the same priority have been found run on
            // round-robin algorithm to prevent starvation
            if (currProc.getCurrentState() == Process.State.WAITING) {
                while (currProc != null && pq.peek() != null && currProc.getPriority() == pq.peek().getPriority()) {
                    rr.addProcess(currProc);
                    currProc = pq.poll();
                    rr.addProcess(currProc);
                }
            }

            if (!rr.isEmpty()) {
                completedProcessList = rr.process();
                processedList.addAll(completedProcessList);
                // End round-robin code execution
            } else {
                // Otherwise run on Priority algorithm
                // update the time this process has been waiting based on the delta of when it entered the queue
                // and how long this algorithm has been running bursts
                int delta = algorithmTotalTime - currProc.getEnterWaitState();
                currProc.wait(delta);
                currProc.setCurrentState(Process.State.RUNNING);
                while (!currProc.isFinished()) {
                    currProc.execute(currProc.nextBurstDuration());
                    algorithmTotalTime += currProc.nextBurstDuration();
                }
                processedList.add(currProc);
            }
        }
        return processedList;
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
