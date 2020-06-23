import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HistoryTest {

    List<String> valueHistory = new ArrayList<>();
    Integer currentIndex = 0;

    public static void main(String[] args) {
        new HistoryTest();
    }

    public HistoryTest() {
        run();
    }

    public void run() {

        Scanner scanner = new Scanner(System.in);

        while (true) {
            String input = scanner.nextLine();
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
        valueHistory.add(newValue);
        currentIndex = valueHistory.size() - 1;
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
        String currentValue = valueHistory.get(currentIndex);
        System.out.println(String.format("current value: %s", currentValue));
    }

    private void printHistory() {
        for(int i = 0; i < valueHistory.size(); i++){
            System.out.println(i + " " + valueHistory.get(i));
        }
        /*
         * Prints a list of all the values in the history with a special marker next to the current value
         */
    }
}
