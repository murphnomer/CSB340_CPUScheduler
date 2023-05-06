import java.util.*;

public class Process implements Comparable<Process> {

    public enum BurstType {IO, CPU;}

    public enum State {RUNNING, WAITING, IO, FINISHED;}

    /**
     * Internal class to track individual CPU or IO bursts.
     */
    private class Burst {
        public BurstType type;
        public Integer duration;

        public Burst(BurstType type, Integer duration) {
            this.type = type;
            this.duration = duration;
        }

    }

    private String name;
    private int priority;
    private Queue<Burst> bursts;
    private int waitingTime;
    private int cpuTime;
    private int ioTime;
    private int totalTime;
    private int finishTime;
    private int currentTick;
    private int arrivalTime;
    private int firstRunTime;
    private State currentState;

    public Process(String name, int priority, int arrivalTime) {
        this.name = name;
        this.priority = priority;
        this.bursts = new LinkedList<>();
        this.arrivalTime = arrivalTime;
        this.waitingTime = 0;
        this.cpuTime = 0;
        this.ioTime = 0;
        this.finishTime = 0;
        this.totalTime = 0;
        this.currentTick = 0;
        this.firstRunTime = 0;
        this.currentState = State.WAITING;
    }

    public void addBurst(BurstType type, Integer duration) {
        bursts.add(new Burst(type, duration));
    }

    public BurstType nextBurstType() {
        return bursts.peek().type;
    }

    /**
     * Runs a clock tick for this process in whatever state the processor is currently in.
     */
    public void tick() {
        currentTick++;
        switch (currentState) {
            case WAITING:
                wait(1);
                break;
            case RUNNING:
                runOnCPU(1);
                break;
            case IO:
                sendToIO(1);
                break;
        }
    }

    /**
     * Simulates the process running on the CPU for some amount of time.  If the next burst in the queue is a CPU
     * burst, decrements the remaining time in the burst and removes the burst if time decrements to zero.
     *
     * @param time is the amount of time to run the process to run the process on the CPU.
     * @return is the amount of time remaining after the process finishes its current CPU burst.
     */
    public int runOnCPU(int time) {
        totalTime += time;
        if (firstRunTime == 0) firstRunTime = currentTick;
        Burst curBurst = bursts.peek();
        cpuTime += Math.max(curBurst.duration, time);
        if (curBurst.type == BurstType.CPU) {
            curBurst.duration = curBurst.duration - time;
            if (curBurst.duration <= 0) {
                bursts.remove();
                setCurrentState(State.WAITING);
                return Math.abs(curBurst.duration);
            }
        }
        return 0;
    }

    /**
     * Simulates sending the process to I/O.  If the next burst in the queue is an IO burst, will also remove that
     * burst and return the duration of the burst.  Also increments the process' IO timer.
     *
     * @return is the amount of time until the process will return from I/O.
     */
    public int sendToIO() {
        currentState = State.IO;
        if (bursts.peek().type == BurstType.IO) {
            Burst curBurst = bursts.remove();
            ioTime += curBurst.duration;
            return curBurst.duration;
        }
        return 0;
    }

    /**
     * Sends the process to I/O for a specified duration.  Return value is the amount of time used if the requested
     * duration is longer than the current I/O burst time.
     *
     * @param time is the I/O time to process.
     * @return is the amount of time actually used if the requested time is longer than the I/O burst duration.
     */
    public int sendToIO(int time) {
        totalTime += time;
        currentState = State.IO;
        Burst curBurst = bursts.peek();
        if (curBurst.type == BurstType.IO) {
            curBurst.duration -= time;
            int timeUsed = Math.min(curBurst.duration, time);
            ioTime += timeUsed;
            if (curBurst.duration <= 0) {
                bursts.remove();
                currentState = State.WAITING;
                return timeUsed;
            }
        }
        return 0;
    }

    /**
     * Simulates requiring the process to wait in the ready queue.  Adds the specified amount of time to the
     * process' wait timer.
     *
     * @param time is the amount of time to wait.
     */
    public void wait(int time) {
        totalTime += time;
        waitingTime += time;
    }

    /**
     * Calculates the turnaround time of this process.  Only valid if the process has finished.
     * @return is the turnaround time.
     */
    public int getTurnaroundTime() {
        if (this.isFinished()) return waitingTime + cpuTime + ioTime;
        return 0;
    }

    public int getResponseTime() {
        return firstRunTime - arrivalTime;
    }

    public boolean isFinished() {
        return bursts.isEmpty() || currentState == State.FINISHED;
    }

    public void finish(int finishTime) {
        this.finishTime = finishTime;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
    }

    public void setPriority(int priority) {
        this.priority = priority;
    }

    public int getWaitingTime() {
        return waitingTime;
    }

    public int getCpuTime() {
        return cpuTime;
    }

    public int getIoTime() {
        return ioTime;
    }

    public int getTotalTime() {
        return totalTime;
    }

    public int getFinishTime() {
        return finishTime;
    }

    public State getCurrentState() {
        return currentState;
    }

    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    /**
     * Bumps the process off of the CPU so a higher priority process can run.
     */
    public void preempt() {
        this.currentState = State.WAITING;
    }

    /**
     * Compares two Process objects on priority first, then on duration if currently in a CPU burst cycle.
     *
     * @param other is the object to be compared.
     * @return is the result of the comparison.
     */
    public int compareTo(Process other) {
        if (this.priority != other.priority) {
            return this.priority - other.priority;
        } else {
            Burst myBurst = bursts.peek();
            Burst otherBurst = other.bursts.peek();

            int myDuration = (myBurst.type == BurstType.CPU) ? myBurst.duration : 100;
            int otherDuration = (otherBurst.type == BurstType.CPU) ? otherBurst.duration : 100;

            return myDuration - otherDuration;
        }
    }
}
