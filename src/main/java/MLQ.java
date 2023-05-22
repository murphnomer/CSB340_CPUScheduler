import java.util.ArrayList;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

public class MLQ implements ScheduleInterface {
    // priority queue of processes waiting for CPU time
    RR foregroundQueue;
    FCFS backgroundQueue;
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

    public MLQ(List<Process> foregroundProcesses, List<Process> backgroundProcesses, int foregroundTimeQuantum) {
        timer = 0;
        idleCPUTime = 0;
        cpuIsIdle = true;
        foregroundQueue = new RR(foregroundTimeQuantum, foregroundProcesses);
        backgroundQueue = new FCFS(backgroundProcesses);
        allProcesses = new ArrayList<>();
        finishedProcesses = new ArrayList<>();
        totalNumberOfProcesses = foregroundProcesses.size() + backgroundProcesses.size();

    }

    @Override
    public List<Process> process() {
        return null;
    }

    @Override
    public void displayState(boolean waitBetweenPages) {

    }

    @Override
    public void setDisplayMode(boolean displayMode) {

    }

    @Override
    public int getTotalElapsedTime() {
        return 0;
    }

    @Override
    public int getTotalIdleCPUTime() {
        return 0;
    }
}
