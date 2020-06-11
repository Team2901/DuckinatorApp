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

public class DuckinatorApplication extends Application {

    public ProjectPane clickerPane;

    @Override
    public void start(Stage primaryStage) {

        clickerPane = new ProjectPane();

        MenuBar menuBar = new MenuBar();
        Menu menuFile = new Menu("File");
        MenuItem open = new MenuItem("Open");
        MenuItem save = new MenuItem("Save");

        final FileChooser fileChooser = new FileChooser();

        open.setOnAction(e -> {
            File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                clickerPane.readPointsFromFile(file);
            }
        });

        save.setOnAction(e -> {
            File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                clickerPane.savePointsToFile(file);
            }
        });

        menuFile.getItems().addAll(open, save);

        menuBar.getMenus().add(menuFile);

        VBox vBox = new VBox(menuBar, clickerPane);

        Scene scene = new Scene(vBox, 1050, 543);

        scene.setFill(Color.BLANCHEDALMOND);
        primaryStage.setTitle("Duckinator 3000");
        primaryStage.setScene(scene);
        primaryStage.show();
        primaryStage.setResizable(true);
    }

    public static void main(String[] args) {
        launch(args);
    }
}

