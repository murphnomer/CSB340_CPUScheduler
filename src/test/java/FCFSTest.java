import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

public class FCFSTest {

    @Test
    void fcfsTest() {
        TestUtil testUtil = new TestUtil();
        Process[] data = testUtil.getDefaultTestData();
        FCFS fcfs = new FCFS(Arrays.asList(data));
        List<Process> result = fcfs.process();
        String[] actual = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            actual[i] = result.get(i).getName();
        }
        String[] expected = {"P1", "P2", "P3", "P4", "P5", "P6", "P7", "P8"};
    }

}
