import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;

/**
 *
 * @author akkir
 */
public class Code6183 extends Application {

    @Override
    public void start(Stage primaryStage) {

        final FileChooser fileChooser = new FileChooser();

        final ProjectPane clickerPane = new ProjectPane();

        final MenuItem open = new MenuItem("Open");
        open.setOnAction(e -> {
            final File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                clickerPane.readPointsFromFile(file);
            }
        });

        final MenuItem save = new MenuItem("Save");
        save.setOnAction(e -> {
            fileChooser.setInitialFileName(String.format("%s.csv",  clickerPane.classNameTextArea.getText()));
            final File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                clickerPane.savePointsToFile(file);
            }
        });

        final MenuItem undo = new MenuItem("Undd");
        undo.setOnAction(e -> {
            clickerPane.undo();
        });

        final MenuItem redo = new MenuItem("Redo");
        redo.setOnAction(e -> {
            clickerPane.redo();
        });

        final Menu menuFile = new Menu("File", null, open, save);
        final Menu menuEdit = new Menu("Edit", null, undo, redo);
        final MenuBar menuBar = new MenuBar(menuFile, menuEdit);

        final VBox vBox = new VBox(menuBar, clickerPane);

        final Scene scene = new Scene(vBox, 1050, 543, Color.BLANCHEDALMOND);

        primaryStage.setTitle("Duckinator 3000");
        primaryStage.setScene(scene);
        primaryStage.setX(150);
        primaryStage.setY(150);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}