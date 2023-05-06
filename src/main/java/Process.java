import java.util.*;

public class Process {

    public enum BurstType {IO, CPU;}

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

    public Process(String name, int priority) {
        this.name = name;
        this.priority = priority;
        this.bursts = new LinkedList<>();
        waitingTime = 0;
        cpuTime = 0;
        ioTime = 0;
    }

    public void addBurst(BurstType type, Integer duration) {
        bursts.add(new Burst(type, duration));
    }

    public BurstType nextBurstType() {
        return bursts.peek().type;
    }

    /**
     * Simulates the process running on the CPU for some amount of time.  If the next burst in the queue is a CPU
     * burst, decrements the remaining time in the burst and removes the burst if time decrements to zero.
     *
     * @param time is the amount of time to run the process to run the process on the CPU.
     * @return is the amount of time remaining after the process finishes its current CPU burst.
     */
    public int runOnCPU(int time) {
        Burst curBurst = bursts.peek();
        cpuTime += Math.max(curBurst.duration, time);
        if (curBurst.type == BurstType.CPU) {
            curBurst.duration = curBurst.duration - time;
            if (curBurst.duration <= 0) {
                bursts.remove();
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
        if (bursts.peek().type == BurstType.IO) {
            Burst curBurst = bursts.remove();
            ioTime += curBurst.duration;
            return curBurst.duration;
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
        waitingTime += time;
    }

    public String getName() {
        return name;
    }

    public int getPriority() {
        return priority;
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

}
