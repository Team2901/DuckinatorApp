package main.java;

import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class HistoryTest {

    List<String> valueHistory = new ArrayList<>();
    Integer currentIndex = null;

    public HistoryTest() {
        run();
    }

    public static void main(String[] args) {
        new HistoryTest();
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
        String currentValue = valueHistory.isEmpty() ? null : valueHistory.get(currentIndex);
        if (newValue.equals(currentValue)) {
            // Don't do add anything
        } else {
            if (currentIndex != null) {
                valueHistory = valueHistory.subList(0, currentIndex + 1);
            }
            valueHistory.add(newValue);
            currentIndex = valueHistory.size() - 1;
        }
        /*
         * Adds the new value to the history
         */
    }

    public void undo() {
        /*
         * Moves the current value to the previous value in the history
         */
        if (currentIndex != 0) {
            currentIndex--;
        }
    }

    public void redo() {
        /*
         * Moves the current value to the next value in the history
         */
        if (currentIndex != valueHistory.size() - 1) {
            currentIndex++;
        }
    }

    private void printCurrentValue() {
        /*
         * Prints the current value
         */
        String currentValue = valueHistory.get(currentIndex);
        System.out.println(String.format("current value: %s", currentValue));
    }

    private void printHistory() {
        for (int i = 0; i < valueHistory.size(); i++) {
            System.out.println(i + " " + valueHistory.get(i) + (i == currentIndex ? "(Current Index)" : ""));
        }
        /*
         * Prints a list of all the values in the history with a special marker next to the current value
         */
    }
}
