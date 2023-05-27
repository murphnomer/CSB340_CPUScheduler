import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PriorityTest {
    private final TestUtil testUtils = new TestUtil();

    @Test
    void testProcessedInOrderOfPriorityNonPreemptive() {
        Process[] processArr = testUtils.getDefaultTestData();
        Priority priority = new Priority(Arrays.asList(processArr));
        List<Process> result = priority.process();
        String[] actual = new String[result.size()];
        for (int i = 0; i < result.size(); i++) {
            actual[i] = result.get(i).getName();
        }
        String[] expected = {"P6", "P1", "P5", "P3", "P8", "P4", "P7", "P2"};
        assertArrayEquals(expected, actual);
    }
}