import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

/**
 * Shortest Job First (non-preemptive) scheduling algorithm
 *
 * @author Mike Murphy
 */
public class SJF implements ScheduleInterface {
    // priority queue of processes waiting for CPU time
    PriorityQueue<Process> readyQueue;
    // list of all processes regardless of status
    List<Process> allProcesses;
    // list of processes currently doing IO
    List<Process> outForIO;
    // list of processes that have already finished their work
    List<Process> finishedProcesses;
    // tracking variable for how many CPU ticks have elapsed
    int timer;
    // total number of processes
    int totalNumberOfProcesses;
    // counter for how long the CPU has been idle
    int idleCPUTime;
    // indicator of whether the CPU is currently idle
    boolean cpuIsIdle;
    // pointer to the process currently executing on the CPU
    Process procOnCPU;
    // switch variable indicating whether to display state at every context switch
    private boolean displayMode = false;

    public SJF(Process[] processes) {
        timer = 0;
        idleCPUTime = 0;
        cpuIsIdle = true;
        readyQueue = new PriorityQueue<>(new OrderByCPUDuration());
        allProcesses = new ArrayList<>();
        outForIO = new ArrayList<>();
        finishedProcesses = new ArrayList<>();
        totalNumberOfProcesses = processes.length;

        for (Process p : processes) {
            allProcesses.add(p);
            readyQueue.add(p);
        }
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
                if (!readyQueue.contains(p)) readyQueue.add(p);
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
     * Runs the algorithm on the process list until all processes have finished.
     *
     * @return is the list of finished process in the order of completion.
     */
    public List<Process> process() {

        while (getUnfinishedProcessCount() > 0) {
            // if there's currently a running process on the CPU
            if (procOnCPU != null && procOnCPU.getCurrentState() == Process.State.RUNNING) {
                // no context switch required

              // if no currently running process, the if the ready queue is not empty
            } else if (readyQueue.size() > 0) {
                // ensure the CPU is not set to idle
                cpuIsIdle = false;
                // if the process currently on the processor is not running, that means it must have finished on the
                // previous tick, so send it to IO and choose the next process
                if (cpuIsIdle || procOnCPU.getCurrentState() != Process.State.RUNNING) {
                    // pick the next process from the ready queue
                    procOnCPU = readyQueue.remove();
                    // print output for this context switch if desired
                    if (displayMode) displayState(false);
                }
                // set the selected process to running
                procOnCPU.setCurrentState(Process.State.RUNNING);
            } else {
                // the ready queue is empty, so the CPU will be idle
                procOnCPU = null;
                if (!cpuIsIdle) {
                    cpuIsIdle = true;
                    // display this context switch if desired
                    if (displayMode) displayState(false);
                }
            }
            // run a tick on all processes
            tickAll();
        }
        return finishedProcesses;
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
    public int getTotalIdleCPUTime() {
        return idleCPUTime;
    }

    /**
     * {@inheritDoc}
     */
    public int getTotalElapsedTime() {
        return timer;
    }
    /**
     * Set boolean variable for the display mode.
     * @param displayMode - boolean default true.
     */
    public void setDisplayMode(boolean displayMode) {
        this.displayMode = displayMode;
    }

    /**
     * Prints out the current state of all queues.
     *
     * @param waitBetweenPages - boolean to wait for command line input or not.
     */
    public void displayState(boolean waitBetweenPages) {
        System.out.println("Current Time: " + timer);
        System.out.println();
        System.out.println("Next process on CPU: " + ((cpuIsIdle) ? "<none>" : procOnCPU.getName() + ", duration: " +
                procOnCPU.getCurrentDuration()));
        System.out.println(".......................................................");
        System.out.println();
        System.out.println("List of processes in the ready queue:");
        System.out.println();
        System.out.println("\t\tProcess\t\tBurst");
        for (Process p : readyQueue) {
            System.out.println("\t\t\t" + p.getName() + "\t\t" + p.getCurrentDuration());
        }
        System.out.println();
        System.out.println(".......................................................");
        System.out.println("List of processes in I/O:");
        System.out.println();
        System.out.println("\t\tProcess\tRemaining I/O time");
        for (Process p : outForIO) {
            System.out.println("\t\t\t" + p.getName() + "\t\t" + p.getCurrentDuration());
        }
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
