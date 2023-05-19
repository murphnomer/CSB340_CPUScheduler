import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class Main {

    // To turn printing of data off set DISPLAY_MODE = false
    private static final boolean DISPLAY_MODE = false;

    public static void main(String[] args) {
        TestUtil util = new TestUtil();
        ScheduleInterface[] algorithms = {
                new RR(Arrays.stream(util.getDefaultTestData()).toList()),
                new SJF(util.getDefaultTestData()),
                new FCFS(Arrays.stream(util.getDefaultTestData()).toList()),
                new Priority(Arrays.stream(util.getDefaultTestData()).toList())
        };
        for (ScheduleInterface algo : algorithms) {
            algo.setDisplayMode(DISPLAY_MODE);
            algo.process();
            System.out.println(":::::::::::::::::::::::::::::::::::::::::::::::::::::::");
            System.out.println();
            System.out.println(algo.getClass().getName());
            System.out.println();
            displayResults(algo);
        }
    }

    public static void displayResults(ScheduleInterface algo) {
        int totalRT = 0, totalWT = 0, totalTT = 0, idleTime = 0, totalTime = 0;
        double size;

        List<Process> result = algo.process();
        ArrayList<String> actual = new ArrayList<>(result.size());
        size = Double.valueOf(result.size());
        for (Process proc : result) {
            totalRT += proc.getResponseTime();
            totalWT += proc.getWaitingTime();
            totalTT += proc.getTurnaroundTime();
            System.out.print(proc.getName() + " CPU: " + proc.getCpuTime() + " IO: " + proc.getIoTime() +
                    " TTr: " + proc.getTurnaroundTime() + " Tr: " + proc.getResponseTime() + " Tw: ");
            System.out.println(proc.getWaitingTime());
            actual.add(proc.getName());
        }
        idleTime = algo.getTotalIdleCPUTime();
        totalTime = algo.getTotalElapsedTime();
        System.out.println();
        System.out.printf("Tw (avg): %.1f TTr (avg): %.1f Tr (avg): %.1f%n", totalWT / size, totalTT / size, totalRT / size);
        System.out.print("Total Time: " + totalTime + " Idle Time: " + idleTime + " CPU Util: ");
        System.out.printf("%.1f", ((1.0 * totalTime - idleTime) / totalTime) * 100);
        System.out.println("%");

    }
}
