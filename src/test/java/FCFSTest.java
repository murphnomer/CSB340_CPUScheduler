import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class FCFSTest {

    @Test
    void fcfsTest() {
        TestUtil testUtil = new TestUtil();
        String[] expected = {"P1", "P6", "P8", "P7", "P5", "P3", "P2", "P4"};
        Process[] data = testUtil.getDefaultTestData();
        FCFS fcfs = new FCFS(Arrays.asList(data));
        fcfs.setDisplayMode(true);
        List<Process> result = fcfs.process();
        String[] actual = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            actual[i] = result.get(i).getName();
        }
        assertArrayEquals(expected, actual);
    }

}
