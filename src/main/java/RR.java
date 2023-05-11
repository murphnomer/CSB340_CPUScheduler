import java.util.*;

/**
 * Round Robin Scheduling algorithm.
 *
 * @author Jared Scarr
 */
public class RR {
    private final Queue<Process> queue;
    private List<Process> processedList;
    private int timeQuantum;
    private int algorithmTotalTime = 0;

    public RR() {
        timeQuantum = 10;
        queue = new LinkedList<>();
        processedList = new ArrayList<>();
    }

    public RR(List<Process> toProcessList) {
        timeQuantum = 10;
        queue = new LinkedList<>();
        queue.addAll(toProcessList);
        processedList = new ArrayList<>();
    }

    public RR(int quantumLength) {
        timeQuantum = quantumLength;
        queue = new LinkedList<>();
        processedList = new ArrayList<>();
    }

    public RR(int quantumLength, List<Process> toProcessList) {
        timeQuantum = quantumLength;
        queue = new LinkedList<>();
        queue.addAll(toProcessList);
        processedList = new ArrayList<>();
    }

    public List<Process> process() {
        while (!isEmpty()) {
            Process currProc = queue.poll();
            if (currProc.getCurrentState() == Process.State.WAITING && currProc.getEnterWaitState() != 0) {
                int delta = algorithmTotalTime - currProc.getEnterWaitState();
                // adds wait time to total wait time and total time
                currProc.wait(delta);
                // reset waiting time for this period (not total weight time)
                currProc.setEnterWaitState(0);
            }
            // Run process for entire burst or quantum whichever is shortest
            int runDuration = Math.max(currProc.nextBurstDuration(), timeQuantum);
            currProc.execute(runDuration);
            algorithmTotalTime += runDuration;;
            // if finished processing add to processed list
            if (currProc.isFinished()) {
                currProc.setCurrentState(Process.State.FINISHED);
                processedList.add(currProc);
            }
            // if not finished then return to the priority queue
            if (currProc.getCurrentState() == Process.State.WAITING) {
                currProc.setEnterWaitState(algorithmTotalTime);
                queue.add(currProc);
            }
        }
        return processedList;
    }

    /**
     * Add Process to queue.
     * @param proc - Process.
     */
    public void addProcess(Process proc) {
        queue.add(proc);
    }

    /**
     * Return queue empty state.
     * @return - boolean. True if empty else false.
     */
    public boolean isEmpty() {
        return queue.size() == 0;
    }

    /**
     * Return the size of the queue.
     * @return - int number of items in the queue.
     */
    public int size() {
        return queue.size();
    }
}
