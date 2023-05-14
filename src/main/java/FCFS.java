import java.security.PrivateKey;
import java.util.*;

/**
 * Priority scheduling class.
 *
 * @author Derrek Do
 */
public class FCFS implements ScheduleInterface{

    private Queue<Process> inReadyQueue;
    private List<Process> inIO = new LinkedList<>();
    private List<Process> completed;
    private List<Process> processes;
    int size;
    private int currentTime;
    private int totWaitTime;
    private int totTurnaroundTime;
    private int totResponseTime;
    private Process current;


    public FCFS(List<Process> readyQueue) {
        inReadyQueue = new LinkedList<>(readyQueue);
        processes = new LinkedList<>(readyQueue);
        size = inReadyQueue.size();
        completed = new ArrayList<>(size);

        currentTime = 0;
    }

    public List<Process> process() {
        int burstDuration = 0;
        while (completed.size() != size) {
            current = inReadyQueue.poll();
            if (current != null) {
                burstDuration = current.nextBurstDuration();
                current.setCurrentState(Process.State.RUNNING);
            } else {
                if (current == null) {
                    burstDuration = inIO.get(0).nextBurstDuration();
                }
                for (Process process : inIO) {
                    burstDuration = Math.min(burstDuration, process.getCurrentDuration());

                }
                //TODO: make IO time decrease while no running process
            }

            display();
            runProcess(burstDuration);
//            System.out.println("test");
        }
        display();


        return completed;
    }



    public void runProcess (int burstDuration) {
//        current.setCurrentState(Process.State.RUNNING);
        tickProcess(burstDuration);
    }

    public void tickProcess(int burstDuration) {
        currentTime += burstDuration;
        while (burstDuration > 0) {
            for (Process current : processes) {
                current.tick();
                if (current.getCurrentState() == Process.State.IO && !inIO.contains(current)) {
                    inIO.add(current);
                    inReadyQueue.remove(current);
                } else if (current.getCurrentState() == Process.State.WAITING && !inReadyQueue.contains(current)) {
                    inReadyQueue.add(current);
                    inIO.remove(current);
                } else if (current.getCurrentState() == Process.State.FINISHED && !completed.contains(current)) {
                    completed.add(current);
                }
            }
            burstDuration--;
        }

    }

    public void display() {
        System.out.println("Current Time: " + currentTime);
        System.out.print("Next Process on CPU: ");
        if (current != null && current.getCurrentState() == Process.State.RUNNING) {
            System.out.println(current.getName() + " Burst: " + current.getCurrentDuration());
        } else {
            System.out.println("IDLE");
        }

        System.out.println("---------------------------");
        System.out.println("In Ready Queue: ");
        if (inReadyQueue.isEmpty()) {
            System.out.println("empty");
        } else {
            for (Process p : inReadyQueue) {
                System.out.println(p.getName() + " " + p.getCurrentDuration());
            }
            System.out.println();
        }
        System.out.println("---------------------------");
        System.out.println("In IO");
        if (inIO.isEmpty()) {
            System.out.println("empty");
        } else {
            for (Process p : inIO) {
                System.out.println(p.getName() + " " + p.getCurrentDuration());
            }
        }
        System.out.println("--------------------------");
        if (!completed.isEmpty()) {
            System.out.print("Completed: ");
            for (Process p : completed) {
                System.out.print(p.getName() + " ");
            }
            System.out.println("\n----------------------------");
        }

        if (completed.size() == size) {
            System.out.println("\n\n\n");
            for (Process p : completed) {
                System.out.print(p.getName() + " ");
                System.out.print(p.getTotalTime() + " ");
                System.out.print(p.getWaitingTime() + " ");
                System.out.print(p.getIoTime() + " ");
                System.out.println(p.getCpuTime());


            }
//            System.out.println("FINISHED\n");
//            System.out.println("Total Time: " + currentTime);
//            System.out.println("CPU Utilization: " + current.getCpuTime());
        }
    }

    @Override
    public void displayState(boolean waitBetweenPages) {

    }

    @Override
    public void setDisplayMode(boolean displayMode) {

    }
}
