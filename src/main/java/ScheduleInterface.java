import java.util.List;

/**
 * Interface for scheduling algorithms.
 *
 * @author Jared Scarr
 */
public interface ScheduleInterface {
    /**
     * Execute the schedule.
     * @return - list of completed processes.
     */
    public List<Process> process();

    /**
     * Display the snapshots of each process state.
     * @param waitBetweenPages - boolean to wait for command line input or not.
     */
    public void displayState(boolean waitBetweenPages);

    /**
     * Set display mode.
     * @param displayMode - true to show data else false.
     */
    public void setDisplayMode(boolean displayMode);
}
