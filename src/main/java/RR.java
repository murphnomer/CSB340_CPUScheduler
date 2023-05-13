import java.util.*;

/**
 * Round Robin Scheduling algorithm.
 *
 * @author Jared Scarr
 */
public class RR implements ScheduleInterface {
    private final Queue<Process> queue = new LinkedList<>();
    private final List<Process> ioQueue = new ArrayList<>();
    private final List<Process> processedList  = new ArrayList<>();;
    private final int timeQuantum;
    private int algorithmTotalTime = 0;
    private Process currentRunningProcess = null;
    private boolean displayMode = true;

    public RR() {
        timeQuantum = 10;
    }

    public RR(List<Process> toProcessList) {
        timeQuantum = 10;
        queue.addAll(toProcessList);
    }

    public RR(int quantumLength) {
        timeQuantum = quantumLength;
    }

    public RR(int quantumLength, List<Process> toProcessList) {
        timeQuantum = quantumLength;
        queue.addAll(toProcessList);
    }

    public List<Process> process() {
        while (!isEmpty()) {
            Process currProc = queue.poll();
            if (displayMode) {
                currentRunningProcess = currProc;
                displayState(false);
            }
            if (currProc.getCurrentState() == Process.State.WAITING) {
                int delta = algorithmTotalTime - currProc.getEnterWaitState();
                // adds wait time to total wait time and total time
                currProc.wait(delta);
            }
            ioQueue.remove(currProc);
            // Run process for entire burst or quantum whichever is shortest
            int runDuration = Math.min(currProc.nextBurstDuration(), timeQuantum);
            currProc.execute(runDuration);
            algorithmTotalTime += runDuration;
            // if finished processing add to processed list
            if (currProc.isFinished()) {
                processedList.add(currProc);
                ioQueue.remove(currProc);
            }

            if (currProc.getCurrentState() == Process.State.IO) {
                ioQueue.add(currProc);
                currProc.execute(runDuration);
            }
            // if not finished then return to the priority queue
            if (currProc.getCurrentState() == Process.State.WAITING) {
                currProc.setEnterWaitState(algorithmTotalTime);
                queue.add(currProc);
            }
        }
        if (displayMode) {
            currentRunningProcess = null;
            displayState(false);
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

    /**
     * Set boolean variable for the display mode.
     * @param display - boolean default true.
     */
    public void setDisplayMode(boolean display) {
        displayMode = display;
    }

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
        for (Process p : queue) {
            System.out.println("\t\t\t" + p.getName() + "\t\t" + p.getCurrentDuration());
        }
        System.out.println();
        System.out.println(".......................................................");
        System.out.println("List of processes in I/O:");
        System.out.println();
        System.out.println("\t\tProcess\tRemaining I/O time");
        for (Process p : ioQueue) {
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
}
