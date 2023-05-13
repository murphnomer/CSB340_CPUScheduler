import java.util.HashMap;

/**
 * Utility class to create and manage Processes for tests.
 *
 * @author Jared Scarr
 */
public class TestUtil {
    private final String[] names = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8"};
    private final int[] priorities = {3, 6, 5, 4, 1, 2, 8, 7};
    private final int[] zeroPriorities = {0, 0, 0, 0, 0, 0, 0, 0};
    private final int[] p1Bursts = {5, 27, 3, 31, 5, 43, 4, 18, 6, 22, 4, 26, 3, 24, 4};
    private final int[] p2Bursts = {4, 48, 5, 44, 7, 42, 12, 37, 9, 76, 4, 41, 9, 31, 7, 43, 8};
    private final int[] p3Bursts = {8, 33, 12, 41, 18, 65, 14, 21, 4, 61, 15, 18, 14, 26, 5, 31, 6};
    private final int[] p4Bursts = {3, 35, 4, 41, 5, 45, 3, 51, 4, 61, 5, 54, 6, 82, 5, 77, 3};
    private final int[] p5Bursts = {16, 24, 17, 21, 5, 36, 16, 26, 7, 31, 13, 28, 11, 21, 6, 13, 3, 11, 4};
    private final int[] p6Bursts = {11, 22, 4, 8, 5, 10, 6, 12, 7, 14, 9, 18, 12, 24, 15, 30, 8};
    private final int[] p7Bursts = {14, 46, 17, 41, 11, 42, 15, 21, 4, 32, 7, 19, 16, 33, 10};
    private final int[] p8Bursts = {4, 14, 5, 33, 6, 51, 14, 73, 16, 87, 6};
    private final int[][] allBurstData = {p1Bursts, p2Bursts, p3Bursts, p4Bursts, p5Bursts, p6Bursts, p7Bursts, p8Bursts};
    private final HashMap<String, int[]> burstMap = new HashMap<>();
    public Process[] getDefaultTestData() {
        for(int i = 0; i < names.length; i++) {
            burstMap.put(names[i], allBurstData[i]);
        }
        return createMultipleProcess(names, priorities, burstMap);
    }

    public Process[] getDefaultTestDataNoPriority() {
        for(int i = 0; i < names.length; i++) {
            burstMap.put(names[i], allBurstData[i]);
        }
        return createMultipleProcess(names, zeroPriorities, burstMap);
    }
    /**
     * Return an array of Processes.
     * @param names - an array of each Process name.
     * @param priorities - an array of each priority corresponding to the name.
     * @param burstByNameMap - a hashMap containing the key: name, vale: int[] bursts
     * @return - Process[]
     */
    public Process[] createMultipleProcess(String[] names, int[] priorities, HashMap<String, int[]> burstByNameMap) {
        Process[] procArr = new Process[priorities.length];
        Process currProc = null;
        for (int i = 0; i < priorities.length; i++) {
            for (int bursts : burstByNameMap.get(names[i])) {
                currProc = this.createProcess(names[i], priorities[i], 0, burstByNameMap.get(names[i]));
            }
            procArr[i] = currProc;
        }
        return procArr;
    }

    /**
     * Return an array of Processes that do not require bursts.
     * @param names - array of each process name.
     * @param priorities - int[] of each priority corresponding to name.
     * @return - Process[]
     */
    public Process[] createMultipleProcess(String[] names, int[] priorities) {
        Process[] procArr = new Process[priorities.length];
        for (int i = 0; i < priorities.length; i++) {
            procArr[i] = this.createProcess(names[i], priorities[i], 0);
        }
        return procArr;
    }

    /**
     * Create a process with a priority.
     * @param name - String priority name.
     * @param priority - Priority level.
     * @param arrivalTime - Arrival Time.
     * @return new Process based on params.
     */
    public Process createProcess(String name, int priority, int arrivalTime) {
        return new Process(name, priority, arrivalTime);
    }

    /**
     * Create a new process that has bursts.
     * @param name - String priority name.
     * @param priority - Priority level.
     * @param arrivalTime - Arrival Time.
     * @param bursts - int array of ints.
     * @return new Process from params.
     */
    public Process createProcess(String name, int priority, int arrivalTime, int[] bursts) {
        Process newProc = new Process(name, priority, arrivalTime);
        Process.BurstType burstType = Process.BurstType.CPU;
        for (int burst : bursts) {
            try {
                newProc.addBurst(burstType, burst);
                burstType = burstType == Process.BurstType.CPU ? Process.BurstType.IO : Process.BurstType.CPU;
            } catch (Process.InvalidBurstTypeException e) {
                System.out.println(e);
            }
        }
        return newProc;
    }
}
