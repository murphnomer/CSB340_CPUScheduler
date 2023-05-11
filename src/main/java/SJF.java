import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;

public class SJF {
    PriorityQueue<Process> readyQueue;
    List<Process> allProcesses;
    int timer;
    int totalNumberOfProcesses;

    public SJF(Process[] processes) {
        timer = 0;
        readyQueue = new PriorityQueue<>();
        allProcesses = new ArrayList<>();
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
        for (Process p : allProcesses) {
            p.tick();
            if (p.getCurrentState() == Process.State.WAITING) {
                if (!readyQueue.contains(p)) readyQueue.add(p);
            }
        }
    }

    public List<Process> process() {
        Process curProc;

        while (getUnfinishedProcessCount() > 0) {
            curProc = readyQueue.remove();
            curProc.setCurrentState(Process.State.RUNNING);
            tickAll();
        }
        return allProcesses;
    }
}
