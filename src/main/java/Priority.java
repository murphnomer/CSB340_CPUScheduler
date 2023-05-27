import java.util.*;

/**
 * Priority scheduling class.
 *
 * @author Jared Scarr
 */

public class Priority implements ScheduleInterface {
    public List<Process> allProcesses = new ArrayList<>();
    private PriorityQueue<Process> readyQ = this.getPriorityQueue(false);
    private final RR rr = new RR();
    private final Queue<Process> ioQ = new PriorityQueue<>(new Comparator<Process>() {
        public int compare(Process a, Process b) {
            if (a.getCurrentDuration() >= b.getCurrentDuration()) {
                return 1;
            }
            if (a.getCurrentDuration() < b.getCurrentDuration()) {
                return -1;
            }
            return 0;
        }
    });
    private final Set<Process> processedList  = new LinkedHashSet<>();
    private int algorithmTotalTime = 0;
    private boolean displayMode = false;
    private Process currentRunningProcess = null;

    private int cpuTime = 0;
    private int cpuIdleTime = 0;

    /**
     * Default constructor.
     */
    public Priority() {}

    /**
     * Constructor with option for ascending or descending priority.
     * @param desc - boolean flag.
     */
    public Priority(boolean desc) {
        readyQ = getPriorityQueue(desc);
    }

    /**
     * Constructor with list of Processes passed as a List.
     * @param procList - List of Process classes.
     */
    public Priority(List<Process> procList) {
        allProcesses.addAll(procList);
        readyQ.addAll(procList);
    }
    /**
     * Constructor with boolean flag and list of processes.
     * @param desc - boolean flag for asc or desc.
     * @param procList - List of Process classes.
     */
    public Priority(boolean desc, List<Process> procList) {
        allProcesses.addAll(procList);
        readyQ.addAll(procList);
    }

    /**
     * Add a process to the PriorityQueue.
     * @param proc - Process class.
     */
    public void addProcess(Process proc) {
        allProcesses.add(proc);
        readyQ.add(proc);
    }

    /**
     * Non-preemptive processing of the schedule.
     * @return - List of processes in the order that they were processed.
     */
    public List<Process> process() {
        while (processedList.size() != allProcesses.size()) {
            Process currProc = readyQ.poll();
            // Ready queue contains processes
            if (currProc != null) {
                currProc.setCurrentState(Process.State.RUNNING);
                int runDuration = currProc.getCurrentDuration();
                int tick = 0;
                while (tick < runDuration) {
                    // display logic
                    if (displayMode) {
                        currentRunningProcess = currProc;
                        displayState(false);
                    }
                    // end display logic
                    tick();
                    cpuTime += currProc.getCpuTime();
                    tick++;
                }
            } else {
                cpuIdleTime++;
                tick();
            }
        }
        // last snapshot
        if (displayMode) {
            currentRunningProcess = null;
            displayState(false);
        }
        // end snapshot logic
        return processedList.stream().toList();
    }

    /**
     * Run updates on all processes, check their states after each tick,
     * and move/remove them from the appropriate lists and queues.
     */
    private void tick() {
        algorithmTotalTime++;
        for (Process proc : allProcesses) {
            proc.tick();
            if (proc.isFinished()) {
                proc.setCurrentState(Process.State.FINISHED);
                readyQ.remove(proc);
                ioQ.remove(proc);
                processedList.add(proc);
            }
            // only proc to IO queue if in the correct state AND isn't already there
            // otherwise it will have repeat processes in the ioQ
            if (proc.getCurrentState() == Process.State.IO && !ioQ.contains(proc)) {
                // The CPU burst has completed so send to IO
                readyQ.remove(proc);
                ioQ.add(proc);
            }

            if (proc.getCurrentState() == Process.State.WAITING && !readyQ.contains(proc)) {
                // Add back to ready after IO burst completes
                Process firstIoCompletedProcess = ioQ.poll();
                // if there is a process in IO the first one in the queue will be the first to be ready to move
                if (firstIoCompletedProcess != null) {
                    readyQ.add(firstIoCompletedProcess);
                }
            }
        }
    }

    /**
     * Display the snapshots of each process state.
     *
     * @param waitBetweenPages - boolean to wait for command line input or not.
     */
    @Override
    public void displayState(boolean waitBetweenPages) {
        System.out.println("Process burst reference:");
        for (Process p : allProcesses) {
            System.out.println(p.toString());
        }
        System.out.println(".......................................................");
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
        for (Process p : readyQ) {
            System.out.println("\t\t\t" + p.getName() + "\t\t" + p.getCurrentDuration());
        }
        System.out.println();
        System.out.println(".......................................................");
        System.out.println("List of processes in I/O:");
        System.out.println();
        System.out.println("\t\tProcess\tRemaining I/O time");
        for (Process p : ioQ) {
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
     * {@inheritDoc}
     */
    @Override
    public boolean getDisplayMode() {
        return displayMode;
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
        return cpuIdleTime;
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
