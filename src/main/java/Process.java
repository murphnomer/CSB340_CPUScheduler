import java.util.*;

/**
 * Process Class.
 *
 * @author Mike Murphy, Jared Scarr
 */
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
    private BurstType lastAddedBurstType;
    private int waitingTime;
    private int cpuTime;
    private int ioTime;
    private int totalTime;
    private int finishTime;
    private int currentTick;
    private int arrivalTime;
    private int firstRunTime;
    private State currentState;
    private int enterWait;

    /**
     * Constructor for Process.
     * @param name - process name.
     * @param priority - priority of process.
     * @param arrivalTime - the time the process arrived.
     */
    public Process(String name, int priority, int arrivalTime) {
        this.name = name;
        this.priority = priority;
        this.bursts = new LinkedList<>();
        this.lastAddedBurstType = null;
        this.arrivalTime = arrivalTime;
        this.waitingTime = 0;
        this.cpuTime = 0;
        this.ioTime = 0;
        this.finishTime = 0;
        this.totalTime = 0;
        this.currentTick = 0;
        this.firstRunTime = 0;
        this.currentState = State.WAITING;
        this.enterWait = 0;
    }

    /**
     * Get the time that this process entered the most recent waiting state.
     * When compared to a total running time this may be used to craft
     * a time delta to see how long this process has been waiting.
     * @return - int.
     */
    public int getEnterWaitState() {
        return enterWait;
    }

    /**
     * Set the time the process entered a waiting state.
     * Allows for wait time to be tracked outside of this process when compared
     * to an outside total running time.
     * @param waitStart - int
     */
    public void setEnterWaitState(int waitStart) {
        this.enterWait = waitStart;
    }

    /**
     * Add a burst to this process.
     * Burst types in this class are tracked in a queue that must alternate
     * from CPU/IO burst types beginning with a CPU burst type.
     * Throws InvalidBurstTypeException if burst type added out of order.
     * @param type - BurstType
     * @param duration - Integer
     */
    public void addBurst(BurstType type, Integer duration) throws InvalidBurstTypeException {
        if (lastAddedBurstType == null) {
            bursts.add(new Burst(type, duration));
        } else if (!lastAddedBurstType.equals(type)) {
            bursts.add(new Burst(type, duration));
        } else {
            throw new InvalidBurstTypeException("Invalid BurstType for queue. Attempted to add: " + type
                                                + " but last added type is: " + lastAddedBurstType);
        }

    }

    /**
     * Return the next burst type.
     * If there are no more bursts return null.
     * @return - BurstType || null.
     */
    public BurstType nextBurstType() {
        return bursts.peek() != null ? bursts.peek().type : null;
    }

    /**
     * Return the next burst duration.
     * If there is no burst return 0.
     * @return - int
     */
    public Integer nextBurstDuration() { return bursts.peek() != null ? bursts.peek().duration : 0; }

    /**
     * Runs a clock tick for this process in whatever state the processor is currently in.
     */
    public void tick() {
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
        currentTick++;
    }

    /**
     * Execute the current process. Check the next burst and send to the appropriate method.
     * @param time - int
     */
    public void execute(int time) {
        BurstType nextBurstType = nextBurstType();
        if (nextBurstType == BurstType.CPU) {
            runOnCPU(time);
        }

        if (nextBurstType == BurstType.IO){
            sendToIO();
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
        if (curBurst != null) {
            if (curBurst.type == BurstType.CPU) {
                cpuTime += Math.min(curBurst.duration, time);
                curBurst.duration = curBurst.duration - time;
                if (curBurst.duration <= 0) {
                    bursts.remove();
                    if (isFinished()) {
                        setCurrentState(State.FINISHED);
                    } else {
                        if (getCurrentBurstType() == BurstType.IO) {
                            setCurrentState(State.IO);
                        } else {
                            setCurrentState(State.WAITING);
                        }
                    }
                    return Math.abs(curBurst.duration);
                }
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
            setCurrentState(State.WAITING);
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
        if (curBurst != null) {
            if (curBurst.type == BurstType.IO) {
                int timeUsed = Math.min(curBurst.duration, time);
                ioTime += timeUsed;
                curBurst.duration -= time;
                if (curBurst.duration <= 0) {
                    bursts.remove();
                    currentState = State.WAITING;
                    return timeUsed;
                }
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

    /**
     * Return the response time of this process.
     * @return - int
     */
    public int getResponseTime() {
        return firstRunTime - arrivalTime;
    }

    /**
     * Return boolean value if the process is finished or not.
     * Process is finished if either the State is FINISHED or
     * there are no more bursts to process.
     *
     * @return - boolean
     */
    public boolean isFinished() {
        return bursts.isEmpty() || currentState == State.FINISHED;
    }

    /**
     * Set the time that the process completed all bursts.
     * @param finishTime - int.
     */
    public void setFinishTime(int finishTime) {
        this.finishTime = finishTime;
    }

    /**
     * Return the name of the process.
     * @return - String.
     */
    public String getName() {
        return name;
    }

    /**
     * Return this process's priority.
     * @return - int.
     */
    public int getPriority() {
        return priority;
    }

    /**
     * Set the priority on the process.
     * @param priority - int
     */
    public void setPriority(int priority) {
        this.priority = priority;
    }

    /**
     * Return total time spent waiting.
     * @return - int.
     */
    public int getWaitingTime() {
        return waitingTime;
    }

    /**
     * Return total time spent processing cpu bursts.
     * @return - int.
     */
    public int getCpuTime() {
        return cpuTime;
    }

    /**
     * Return total time spend processing IO bursts.
     * @return 0 int
     */
    public int getIoTime() {
        return ioTime;
    }

    /**
     * Return total time process did things.
     * @return - int.
     */
    public int getTotalTime() {
        return totalTime;
    }

    /**
     * Return time process finished.
     * @return - int
     */
    public int getFinishTime() {
        return finishTime;
    }

    /**
     * Get the current state.
     * @return - State
     */
    public State getCurrentState() {
        return currentState;
    }

    /**
     * Set the current state.
     * @param currentState - new State.
     */
    public void setCurrentState(State currentState) {
        this.currentState = currentState;
    }

    public int getCurrentDuration() {
        return bursts.peek().duration;
    }

    public BurstType getCurrentBurstType() {
        return bursts.peek().type;
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

    public String toString() {
        StringBuilder sb = new StringBuilder("");

        sb.append(this.name + " Pri: " + this.priority + ": {");
        for (Burst b : bursts) {
            sb.append(b.type.toString() + ":" + b.duration + " ");
        }
        sb.append("}");
        return sb.toString();
    }

    /**
     * InvalidBurstTypeException.
     */
    static class InvalidBurstTypeException extends Exception {
        public InvalidBurstTypeException(String message) {
            super(message);
        }
    }
}

class OrderByPriority implements Comparator<Process> {
    public int compare(Process a, Process b) {
        return a.getPriority() - b.getPriority();
    }
}

class OrderByCPUDuration implements Comparator<Process> {
    public int compare(Process a, Process b) {
        if (a.getCurrentBurstType() == Process.BurstType.CPU) {
            if (b.getCurrentBurstType() == Process.BurstType.CPU)
                return a.getCurrentDuration() - b.getCurrentDuration();
            else return 0 - a.getCurrentDuration();
        } else {
            if (b.getCurrentBurstType() == Process.BurstType.IO)
                return 0;
            else return b.getCurrentDuration();

        }
    }
}