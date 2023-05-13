import java.util.Arrays;

public class Main {

    // To turn printing of data off set DISPLAY_MODE = false
    private static final boolean DISPLAY_MODE = true;

    public static void main(String[] args) {
        // TODO: add toggle for each algorithm to not have the display printed
        TestUtil util = new TestUtil();
        Process[] defaultData = util.getDefaultTestData();
        ScheduleInterface[] algorithms = {new RR(Arrays.stream(defaultData).toList()), new SJF(defaultData)};
        for (ScheduleInterface algo : algorithms) {
            algo.setDisplayMode(DISPLAY_MODE);
            algo.process();
        }
    }
}
