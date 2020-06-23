import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HistoryTest {

    List<String> valueHistory = new ArrayList<>();
    Integer currentIndex = null;

    public static void main(String[] args) {
        new HistoryTest();
    }

    public HistoryTest() {
        run();
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.next();
            System.out.println(input);

            if ("undo".equals(input)) {
                undo();
            } else if ("redo".equals(input)) {
                redo();
            } else if ("print".equals(input)) {
                printHistory();
            } else {
                updateValue(input);
            }

            printCurrentValue();
        }
    }

    public void updateValue(String newValue) {
        /*
         * Adds the new value to the history
         */
    }

    public void undo() {
        /*
         * Moves the current value to the previous value in the history
         */
    }

    public void redo() {
        /*
         * Moves the current value to the next value in the history
         */
    }

    private void printCurrentValue() {
        /*
         * Prints the current value
         */
        String currentValue = null;
        System.out.println(String.format("current value: %s", currentValue));
    }

    private void printHistory() {
        /*
         * Prints a list of all the values in the history with a special marker next to the current value
         */
    }
}
