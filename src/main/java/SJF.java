import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class SJF implements ScheduleInterface {
    PriorityQueue<Process> readyQueue;
    List<Process> allProcesses;
    List<Process> outForIO;
    List<Process> finishedProcesses;
    int timer;
    int totalNumberOfProcesses;
    int idleCPUTime;
    boolean cpuIsIdle;
    Process procOnCPU;
    private boolean displayMode = true;

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

    public int getUnfinishedProcessCount() {
        int count = 0;

        for (Process p : allProcesses) {
            if (!p.isFinished()) count++;
        }
        return count;
    }

    public void tickAll() {
        timer++;
        if (cpuIsIdle) idleCPUTime++;
        for (Process p : allProcesses) {
            p.tick();
            if (p.getCurrentState() == Process.State.WAITING) {
                if (!readyQueue.contains(p)) readyQueue.add(p);
                if (outForIO.contains(p)) outForIO.remove(p);
            }
            if (p.getCurrentState() == Process.State.IO) {
                if (!outForIO.contains(p)) outForIO.add(p);
            }
            if (p.getCurrentState() == Process.State.FINISHED) {
                if (!finishedProcesses.contains(p)) finishedProcesses.add(p);
                if (outForIO.contains(p)) outForIO.remove(p);
            }
        }
    }

    public List<Process> process() {

        while (getUnfinishedProcessCount() > 0) {
            if (procOnCPU != null && procOnCPU.getCurrentState() == Process.State.RUNNING) {

            } else if (readyQueue.size() > 0) {
                if (cpuIsIdle || procOnCPU.getCurrentState() != Process.State.RUNNING) {
                    cpuIsIdle = false;
                    procOnCPU = readyQueue.remove();
                    if (displayMode) displayState(false);
                }
                cpuIsIdle = false;
                procOnCPU.setCurrentState(Process.State.RUNNING);
            } else {
                procOnCPU = null;
                if (!cpuIsIdle) {
                    cpuIsIdle = true;
                    if (displayMode) displayState(false);
                }
            }
            tickAll();
        }
        return finishedProcesses;
    }

    public double getCPUUtilization() {
        return (1.0 * (timer - idleCPUTime)) / timer;
    }

    /**
     * Set boolean variable for the display mode.
     * @param displayMode - boolean default true.
     */
    public void setDisplayMode(boolean displayMode) {
        this.displayMode = displayMode;
    }

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
