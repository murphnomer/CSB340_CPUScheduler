import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;

public class MLFQTest {

    @Test
    void mlfqTest() {
        TestUtil testUtil = new TestUtil();
        String[] expected = {"P1", "P6", "P8", "P7", "P4", "P2", "P5", "P3"};
        Process[] data = testUtil.getDefaultTestData();
        MLFQ mlfq = new MLFQ(Arrays.asList(data), 5, 10);
        mlfq.setDisplayMode(true);
        List<Process> result = mlfq.process();
        String[] actual = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            actual[i] = result.get(i).getName();
        }
        assertArrayEquals(expected, actual);
    }
}
