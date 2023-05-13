import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class SJF {
    PriorityQueue<Process> readyQueue;
    List<Process> allProcesses;
    List<Process> outForIO;
    int timer;
    int totalNumberOfProcesses;
    int idleCPUTime;
    boolean cpuIsIdle;
    Process procOnCPU;

    public SJF(Process[] processes) {
        timer = 0;
        idleCPUTime = 0;
        cpuIsIdle = true;
        readyQueue = new PriorityQueue<>(new OrderByCPUDuration());
        allProcesses = new ArrayList<>();
        outForIO = new ArrayList<>();
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
            }
            if (p.getCurrentState() == Process.State.IO) {
                if (!outForIO.contains(p)) outForIO.add(p);
            }
        }
    }

    public List<Process> process() {

        while (getUnfinishedProcessCount() > 0) {
            if (readyQueue.size() > 0) {
                if (cpuIsIdle || procOnCPU.getCurrentState() == Process.State.IO) {
                    cpuIsIdle = false;
                    procOnCPU = readyQueue.remove();
                    //displayState();
                }
                cpuIsIdle = false;
                procOnCPU.setCurrentState(Process.State.RUNNING);
            } else {
                procOnCPU = null;
                cpuIsIdle = true;
            }
            tickAll();
        }
        return allProcesses;
    }

    public void displayState() {
        System.out.println("Current Time: " + timer);
        System.out.println();
        System.out.println("Next process on CPU: " + ((cpuIsIdle) ? "<none>" : procOnCPU.getName() + ", duration: " +
                procOnCPU.getCurrentDuration()));
        System.out.println(".......................................................");
        System.out.println();
        System.out.println("List of processes in the ready queue:");
        System.out.println();
        System.out.println("\t\tProcess\tBurst");
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
        System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::");
        System.out.println();
        System.out.println();
        try {System.in.read();} catch (Exception e) {
            System.out.println(e);
        }
    }
}
