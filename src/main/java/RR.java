import java.util.*;

/**
 * Round Robin Scheduling algorithm.
 *
 * @author Jared Scarr
 */
public class RR implements ScheduleInterface {
    public List<Process> allProcesses = new ArrayList<>();
    private final Queue<Process> readyQ = new LinkedList<>();
    // priority queue smallest duration is the highest priority
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
    private final int timeQuantum;
    private int algorithmTotalTime = 0;
    private Process currentRunningProcess = null;
    private boolean displayMode = true;

    private int cpuTime = 0;
    private int cpuIdleTime = 0;

    /**
     * Default constructor.
     */
    public RR() {
        timeQuantum = 5;
    }

    /**
     * Constructor with List as param.
     * @param toProcessList - List of Processes.
     */
    public RR(List<Process> toProcessList) {
        allProcesses = toProcessList;
        readyQ.addAll(toProcessList);
        timeQuantum = 5;
    }

    /**
     * Constructor with quantum as parameter.
     * @param quantumLength - integer representing the quantum duration.
     */
    public RR(int quantumLength) {
        timeQuantum = quantumLength;
    }

    /**
     * Constructor with both time quantum and list passed.
     * @param quantumLength - int time quantum duration.
     * @param toProcessList - List of Processes.
     */
    public RR(int quantumLength, List<Process> toProcessList) {
        allProcesses = toProcessList;
        readyQ.addAll(toProcessList);
        timeQuantum = quantumLength;
    }

    /**
     * Process the schedule given the current state of the ready queue.
     * @return - list of completed processes in order of completion.
     */
    public List<Process> process() {
        while (processedList.size() != allProcesses.size()) {
            Process currProc = readyQ.poll();
            // Display logic
            if (displayMode) {
                currentRunningProcess = currProc;
                displayState(false);
            }
            // end display logic

            // Ready queue contains processes
            if (currProc != null) {
                currProc.setCurrentState(Process.State.RUNNING);
                // Run process for entire burst or quantum whichever is shortest
                int runDuration = Math.min(currProc.nextBurstDuration(), timeQuantum);
                int tick = 0;
                while (tick < runDuration) {
                    tick();
                    cpuTime += currProc.getCpuTime();
                    tick++;
                }
            } else { // Nothing in readyQ everything out for IO in ioQ or complete
                // nothing on readyQ so track cpu idle time
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

            if (proc.getCurrentState() == Process.State.WAITING) {
                // The CPU burst did not run its full duration so send back to readyQ
                Process firstIoCompletedProcess = ioQ.poll();
                // if there is a process in IO the first one in the queue will be the first to be ready to move
                if (firstIoCompletedProcess != null) {
                    readyQ.add(firstIoCompletedProcess);
                }
            }
        }
    }

    /**
     * Add Process to queue.
     * @param proc - Process.
     */
    public void addProcess(Process proc) {
        readyQ.add(proc);
    }

    /**
     * Set boolean variable for the display mode.
     * @param display - boolean default true.
     */
    public void setDisplayMode(boolean display) {
        displayMode = display;
    }
    /**
     * {@inheritDoc}
     */
    public int getTotalIdleCPUTime() {
        return cpuIdleTime;
    }

    /**
     * {@inheritDoc}
     */
    public int getTotalElapsedTime() {
        return algorithmTotalTime;
    }


    /**
     * Display consecutive snapshots of each iteration of the algorithm.
     * @param waitBetweenPages - boolean to wait for command line input or not.
     */
    public void displayState(boolean waitBetweenPages) {
        System.out.println("Process burst reference:");
        for (Process p : allProcesses) {
            System.out.println(p.toString());
        }
        System.out.println(".......................................................");
        System.out.println("Current Time: " + algorithmTotalTime);
        System.out.println();
        System.out.println("Next process on CPU: " +
                (currentRunningProcess == null ? "<none>" : currentRunningProcess.getName()) + ", duration: " +
                (currentRunningProcess == null ? "<none>" : currentRunningProcess.getCurrentDuration()));
        System.out.println(".......................................................");
        System.out.println();
        System.out.println("List of processes in the ready queue:");
        System.out.println();
        System.out.println("\t\tProcess\t\tBurst");
        for (Process p : readyQ) {
            System.out.println("\t\t\t" + p.getName() + "\t\t" +
                    ((Integer)p.getCurrentDuration() == null ? "<none>" : p.getCurrentDuration()));
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
}
