import java.util.*;

/**
 * Runs a Multilevel Queue CPU Scheduling algorithm
 *
 * @author Mike Murphy
 */
public class MLQ implements ScheduleInterface {
    // priority queue of processes waiting for CPU time
    Queue<Process> foregroundQueue;
    Queue<Process> backgroundQueue;
    // pointers to track which queue is currently allowed to execute and which is idle
    Queue<Process> activeQueue;
    Queue<Process> idleQueue;
    // keep track of which processes belong in which queue so they go back to the right one when they return from IO
    Map<String, Queue<Process>> processMap;
    // time quantum of the round robin foreground queue
    int foregroundTQ;
    // list of all processes regardless of status
    List<Process> allProcesses;
    // list of processes currently doing IO
    List<Process> outForIO;
    // list of processes that have already finished their work
    List<Process> finishedProcesses;
    // tracking variable for how many CPU ticks have elapsed
    int timer = 0;
    // counter to track current round robin cycle
    int currCycleTimer = 0;
    // total number of processes
    int totalNumberOfProcesses;
    // counter for how long the CPU has been idle
    int idleCPUTime = 0;
    // indicator of whether the CPU is currently idle
    boolean cpuIsIdle = true;
    // pointer to the process currently executing on the CPU
    Process procOnCPU;
    // switch variable indicating whether to display state at every context switch
    private boolean displayMode = false;
    // switch variable indicating whether to display state every tick for debugging
    private boolean debugMode = false;

    public MLQ(List<Process> foregroundProcesses, List<Process> backgroundProcesses, int foregroundTimeQuantum) {
        foregroundQueue = new LinkedList<>(foregroundProcesses);
        backgroundQueue = new LinkedList<>(backgroundProcesses);
        activeQueue = foregroundQueue;
        idleQueue = backgroundQueue;
        allProcesses = new ArrayList<>();
        processMap = new HashMap<>();
        for (Process p : foregroundProcesses) {
            allProcesses.add(p);
            processMap.put(p.getName(), foregroundQueue);
        }
        for (Process p : backgroundProcesses) {
            allProcesses.add(p);
            processMap.put(p.getName(), backgroundQueue);
        }
        outForIO = new ArrayList<>();
        finishedProcesses = new ArrayList<>();
        foregroundTQ = foregroundTimeQuantum;
        totalNumberOfProcesses = allProcesses.size();
    }

    /**
     * Gets the number of process that haven't yet finished their operations.
     *
     * @return is the number of unfinished processes.
     */
    public int getUnfinishedProcessCount() {
        int count = 0;

        for (Process p : allProcesses) {
            if (!p.isFinished()) count++;
        }
        return count;
    }

    private void switchQueues() {
        if (activeQueue == foregroundQueue) {
            idleQueue = foregroundQueue;
            activeQueue = backgroundQueue;
        } else {
            idleQueue = backgroundQueue;
            activeQueue = foregroundQueue;
        }
        currCycleTimer = 0;
    }

    @Override
    public List<Process> process() {
        while (getUnfinishedProcessCount() > 0) {
            if (activeQueue == foregroundQueue) {
                if (currCycleTimer++ >= foregroundTQ) {
                    if (procOnCPU != null && procOnCPU.getCurrentState() == Process.State.RUNNING) {
                        procOnCPU.preempt();
                        processMap.get(procOnCPU.getName()).add(procOnCPU);
                    }
                    currCycleTimer = 0;
                }
            } else {
                if (!foregroundQueue.isEmpty()) {
                    switchQueues();
                    if (procOnCPU != null && procOnCPU.getCurrentState() == Process.State.RUNNING) {
                        procOnCPU.preempt();
                        processMap.get(procOnCPU.getName()).add(procOnCPU);
                    }
                    currCycleTimer = 0;
                }
            }

                // if there's currently a running process on the CPU
            if (procOnCPU != null && procOnCPU.getCurrentState() == Process.State.RUNNING) {
                // no context switch required

                // if no currently running process, the if the ready queue is not empty
            } else if (activeQueue.size() > 0) {
                    // ensure the CPU is not set to idle
                    // if the process currently on the processor is not running, that means it must have finished on the
                    // previous tick, so send it to IO and choose the next process
                    if (cpuIsIdle || procOnCPU.getCurrentState() != Process.State.RUNNING) {
                        cpuIsIdle = false;
                        // pick the next process from the ready queue
                        procOnCPU = activeQueue.remove();
                        // print output for this context switch if desired
                        procOnCPU.setCurrentState(Process.State.RUNNING);
                        currCycleTimer = 1;
                        if (displayMode) displayState(false);
                    }
                } else if (idleQueue.size() > 0) {
                    switchQueues();
                    cpuIsIdle = false;
                    // pick the next process from the ready queue
                    procOnCPU = activeQueue.remove();
                    // print output for this context switch if desired
                    // set the selected process to running
                    procOnCPU.setCurrentState(Process.State.RUNNING);
                    if (displayMode) displayState(false);
                } else {
                    // both queues are empty, so the CPU will be idle
                    procOnCPU = null;
                    if (!cpuIsIdle) {
                        cpuIsIdle = true;
                        // display this context switch if desired
                        if (displayMode) displayState(false);
                    }
                }
            // run a tick on all processes
            tickAll();
            if (debugMode) displayState(false);
    }
        return finishedProcesses;
    }

    /**
     * Runs a single clock tick on every process.
     */
    public void tickAll() {
        timer++;
        if (cpuIsIdle) idleCPUTime++;
        for (Process p : allProcesses) {
            p.tick();

            // if the process is waiting, make sure it's in the ready queue and not in the IO queue
            if (p.getCurrentState() == Process.State.WAITING) {
                if (!processMap.get(p.getName()).contains(p)) processMap.get(p.getName()).add(p);
                if (outForIO.contains(p)) outForIO.remove(p);
            }
            // if the process is in the IO state, make sure it's in the IO queue
            if (p.getCurrentState() == Process.State.IO) {
                if (!outForIO.contains(p)) outForIO.add(p);
            }
            // if the process has finished all of its bursts, make sure it's in the finished queue and not in the IO
            // queue
            if (p.getCurrentState() == Process.State.FINISHED) {
                if (!finishedProcesses.contains(p)) finishedProcesses.add(p);
                if (outForIO.contains(p)) outForIO.remove(p);
            }
        }

    }


    /**
     * Gets the CPU utilization metric for this run
     * @return
     */
    public double getCPUUtilization() {
        return (1.0 * (timer - idleCPUTime)) / timer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalIdleCPUTime() {
        return idleCPUTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalElapsedTime() {
        return timer;
    }
    /**
     * Set boolean variable for the display mode.
     * @param displayMode - boolean default true.
     */
    @Override
    public void setDisplayMode(boolean displayMode) {
        this.displayMode = displayMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getDisplayMode() {
        return displayMode;
    }

    /**
     * Prints out the current state of all queues.
     *
     * @param waitBetweenPages - boolean to wait for command line input or not.
     */
    public void displayState(boolean waitBetweenPages) {
        System.out.println("Current Time: " + timer);
        System.out.println();
        System.out.println("Process on CPU: " + ((cpuIsIdle) ? "<none>" : procOnCPU.getName() + ", remaining burst duration: " +
                (procOnCPU.getCurrentBurstType()==Process.BurstType.CPU ? procOnCPU.getCurrentDuration() : "<finished>")));
        System.out.println(".......................................................");
        System.out.println();
        System.out.println("List of processes in the foreground queue: " + (activeQueue ==foregroundQueue ? "<active> current TQ " + currCycleTimer + "/" + foregroundTQ : "") );
        System.out.println();
        if (!foregroundQueue.isEmpty()) {
            System.out.println("\t\tProcess\t\tBurst");
            for (Process p : foregroundQueue) {
                System.out.println("\t\t\t" + p.getName() + "\t\t" + p.getCurrentDuration());
            }
        } else {
            System.out.println("\t\t<none>");
        }
        System.out.println();
        System.out.println("List of processes in the background queue: " + (activeQueue ==backgroundQueue ? "<active> ": ""));
        System.out.println();
        if (!backgroundQueue.isEmpty()) {
            System.out.println("\t\tProcess\t\tBurst");
            for (Process p : backgroundQueue) {
                System.out.println("\t\t\t" + p.getName() + "\t\t" + p.getCurrentDuration());
            }
        } else {
            System.out.println("\t\t<none>");
        }
        System.out.println();
        System.out.println(".......................................................");
        System.out.println("List of processes in I/O:");
        System.out.println();
        if (outForIO.size() > 0) {
            System.out.println("\t\tProcess\tRemaining I/O time");
            for (Process p : outForIO) {
                System.out.println("\t\t\t" + p.getName() + "\t\t" + p.getCurrentDuration());
            }
        } else {
            System.out.println("\t\t<none>");
        }
        System.out.println();
        System.out.println(".......................................................");
        System.out.println();
        System.out.print("Finished processes: ");
        for (Process p : finishedProcesses) System.out.print(p.getName() + " ");
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
