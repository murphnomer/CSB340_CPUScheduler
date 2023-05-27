import java.security.PrivateKey;
import java.util.*;

/**
 * First Come, First Served scheduling class.
 *
 * @author Derrek Do
 */
public class FCFS implements ScheduleInterface{
    //Ready queue for each process
    private Queue<Process> inReadyQueue;
    //List of processes in IO
    private List<Process> inIO = new LinkedList<>();
    //list process that have completed all bursts
    private List<Process> completed;
    //List of all processes
    private List<Process> processes;
    //number of processes
    private int size;
    //Data and results regarding process runtimes
    private int currentTime;
    private double cpuTime = 0;
    private double totWaitTime;
    private double totTurnaroundTime;
    private double totResponseTime;
    //current process running
    private Process processOnCpu;
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
        //Runs each processes until they complete all bursts
        while (completed.size() != size) {
            //removes the first process from queue
            processOnCpu = inReadyQueue.poll();
            //checks if a process was removed
            if (processOnCpu != null) {
                //record its burst duration and set state to RUNNING
                burstDuration = processOnCpu.nextBurstDuration();
                processOnCpu.setCurrentState(Process.State.RUNNING);
            } else {
                //If there were no process to run, wait for process in I/O
                if (processOnCpu == null) {
                    burstDuration = inIO.get(0).nextBurstDuration();
                }
                //Finds the process with the lowest current I/O time
                for (Process process : inIO) {
                    burstDuration = Math.min(burstDuration, process.getCurrentDuration());
                }
            }
            //determines if the data will be shown
            if (displayMode) {
                displayState(false);
            }
            //Ticks each process base on the current CPU or I/O burst
            //updates run time,wait time, and I/O time based on each processes current state
            tickProcess(burstDuration);
        }
        if (displayMode) {
            displayState(false);
        }
        return completed;
    }

    public void tickProcess(int burstDuration) {
        //adds current burst duration to total time
        currentTime += burstDuration;
        //loop runs until the burst finishes
        while (burstDuration > 0) {
            //iterates through each process
            for (Process current : processes) {
                //runs the tick method on the current process
                current.tick();
                //if the current running process completes is CPU burst, send to I/O
                if (current.getCurrentState() == Process.State.IO && !inIO.contains(current)) {
                    inIO.add(current);
                    inReadyQueue.remove(current);
                    //If any process I/O burst finishes, send to ready queue
                } else if (current.getCurrentState() == Process.State.WAITING && !inReadyQueue.contains(current)) {
                    inReadyQueue.add(current);
                    inIO.remove(current);
                    //if the process finishes all bursts, add to list of completed processes, and record all data
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
        if (processOnCpu != null && processOnCpu.getCurrentState() == Process.State.RUNNING) {
            System.out.println(processOnCpu.getName() + " \nBurst Time: " + processOnCpu.getCurrentDuration());
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

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean getDisplayMode() {
        return displayMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDisplayMode(boolean displayMode) {
        this.displayMode = displayMode;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalElapsedTime() {
        return currentTime;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getTotalIdleCPUTime() {
        return currentTime - (int)cpuTime;
    }
}
