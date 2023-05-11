import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class RRTest {
    private final TestUtil testUtil = new TestUtil();

    @Test
    void testRuns() {
        Process[] testData = testUtil.getDefaultTestData();
        RR rr = new RR(Arrays.stream(testData).toList());
        List<Process> result = rr.process();
        String[] expected = {"P8", "P1", "P7", "P2", "P3", "P4", "P6", "P5"};
        ArrayList<String> actual = new ArrayList<>(8);
        for (Process proc : result) {
            System.out.print(proc.getName() + " " + proc.getCpuTime() + " " + proc.getIoTime() +
                    " " + proc.getTurnaroundTime() + " ");
            System.out.println(proc.getWaitingTime());
            actual.add(proc.getName());
        }
        assertArrayEquals(expected, actual.toArray());
    }
}