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
    private int size;
    private int currentTime;
    private double cpuTime = 0;
    private double totWaitTime;
    private double totTurnaroundTime;
    private double totResponseTime;
    private Process current;
    boolean displayMode;

    public FCFS(List<Process> readyQueue) {
        inReadyQueue = new LinkedList<>(readyQueue);
        processes = new LinkedList<>(readyQueue);
        size = inReadyQueue.size();
        completed = new ArrayList<>(size);
        currentTime = 0;
        displayMode = false;
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
            if (displayMode) {
                displayState(false);
            }
            tickProcess(burstDuration);
        }
        if (displayMode) {
            displayState(false);
        }
        return completed;
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
                    totWaitTime += current.getWaitingTime();
                    totTurnaroundTime += current.getTurnaroundTime();
                    totResponseTime += current.getResponseTime();
                    cpuTime += current.getCpuTime();
                }
            }
            burstDuration--;
        }

    }


    @Override
    public void displayState(boolean waitBetweenPages) {
        System.out.println("\n\nCurrent Time: " + currentTime);
        System.out.print("Next Process on CPU: ");
        if (current != null && current.getCurrentState() == Process.State.RUNNING) {
            System.out.println(current.getName() + " \nBurst Time: " + current.getCurrentDuration());
        } else {
            System.out.println("IDLE");
        }

        System.out.println("..................................................");
        System.out.println("\nList of processes in the ready queue:\n");
        System.out.println("\t\tProcess\t\tBurst");
        if (inReadyQueue.isEmpty()) {
            System.out.println("\t\t[empty]");
        } else {
            for (Process p : inReadyQueue) {
                System.out.println("\t\t\t" + p.getName() + "\t\t\t" + p.getCurrentDuration());
            }
            System.out.println();
        }
        System.out.println("..................................................");
        System.out.println("\nList of processes in I/O:\n");
        System.out.println("\t\tProcess\t\tRemaining I/O time");
        if (inIO.isEmpty()) {
            System.out.println("\t\t[empty]");
        } else {
            for (Process p : inIO) {
                System.out.println("\t\t\t" + p.getName() + "\t\t\t" + p.getCurrentDuration());
            }
        }
        System.out.println("..................................................");
        if (!completed.isEmpty()) {
            System.out.print("\nCompleted: ");
            for (Process p : completed) {
                System.out.print(p.getName() + " ");
            }
            System.out.println();
            System.out.println("..................................................");
        }
        System.out.println("..................................................");

        if (completed.size() == size) {
            Queue<Integer> waitTimes = new LinkedList<>();
            Queue<Integer> turnAroundTimes = new LinkedList<>();
            Queue<Integer> responseTimes = new LinkedList<>();

            System.out.println("\n\n");
            System.out.println("FINISHED\n");
            System.out.println("Total Time:\t\t\t" + currentTime);
            System.out.printf("CPU Utilization:\t%.4f", (cpuTime / currentTime) * 100 );
            System.out.println("%");
            String[] timeType = {"Waiting Times", "Turnaround Times", "Response Times"};

            for (int i = 0; i < timeType.length; i++) {
                System.out.print("\n" + timeType[i] + "\t");
                if (i != 1) {
                    System.out.print("\t");
                }

                for (Process process : processes) {
                    System.out.print(process.getName() + "\t");
                    if (i == 0) {
                        waitTimes.add(process.getWaitingTime());
                        turnAroundTimes.add(process.getTurnaroundTime());
                        responseTimes.add(process.getResponseTime());
                    }
                }
                System.out.print("\n\t\t\t\t\t");
                if (i == 0) {
                    while (!waitTimes.isEmpty()) {
                        System.out.print(waitTimes.remove() + "\t");
                    }
                    System.out.printf("\nAverage Wait:\t\t%.2f %n", (totWaitTime / size));
                } else if (i == 1) {
                    while (!turnAroundTimes.isEmpty()) {
                        System.out.print(turnAroundTimes.remove() + "\t");
                    }
                    System.out.printf("\nAverage Turnaround: %.3f %n", (totTurnaroundTime / size));
                } else {
                    while (!responseTimes.isEmpty()) {
                        System.out.print(responseTimes.remove() + "\t");
                    }
                    System.out.printf("\nAverage Response:\t%.3f %n%n", (totResponseTime / size));
                }
            }
        }
        if (waitBetweenPages) {
            System.out.println("Press Enter to continue . . .");
            try {
                System.in.read();
            } catch (Exception e) {
                System.out.println(e);
            }
        }
    }

    @Override
    public void setDisplayMode(boolean displayMode) {
        this.displayMode = displayMode;
    }
}
