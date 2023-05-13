import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

class SJFTest {
    private final TestUtil testUtil = new TestUtil();

    @Test
    void testRuns() {
        Process[] testData = testUtil.getDefaultTestData();
        SJF sjf = new SJF(testData);
        List<Process> result = sjf.process();
        String[] expected = {"P1", "P6", "P8", "P7", "P2", "P4", "P5", "P3"};
        ArrayList<String> actual = new ArrayList<>(8);
        for (Process proc : result) {
            System.out.print(proc.getName() + " CPU: " + proc.getCpuTime() + " IO: " + proc.getIoTime() +
                    " TT: " + proc.getTurnaroundTime() + " WT: ");
            System.out.println(proc.getWaitingTime());
            actual.add(proc.getName());
        }
        System.out.print("Total Time: " + sjf.timer + " Idle Time: " + sjf.idleCPUTime + " CPU Util: ");
        System.out.printf("%.1f", sjf.getCPUUtilization() * 100);
        System.out.println("%");
        assertArrayEquals(expected, actual.toArray());
    }
}