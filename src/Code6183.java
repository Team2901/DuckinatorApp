import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.awt.*;
import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

/**
 *
 * @author akkir
 */
public class Code6183 extends Application {

    final static String GITHUB_README = "https://github.com/Team2901/DuckinatorApp/blob/master/README.md";

    @Override
    public void start(Stage primaryStage) {

        final ProjectPane clickerPane = new ProjectPane();

        final FileChooser fileChooser = new FileChooser();

        // Set the default directory the file chooser opens
        final File outputFile = new File("pathways");
        if(!outputFile.exists()){
            outputFile.mkdir();
        }
        fileChooser.setInitialDirectory(outputFile);

        // Only allow opening csv files
        FileChooser.ExtensionFilter extFilter =
                new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extFilter);

        final FileChooser javaFileChooser = new FileChooser();

        // Set the default directory the file chooser opens
        final File javaOutputFile = new File("classes");
        if(!javaOutputFile.exists()){
            javaOutputFile.mkdir();
        }
        javaFileChooser.setInitialDirectory(javaOutputFile);

        // Only allow opening csv files
        FileChooser.ExtensionFilter javaExtFilter =
                new FileChooser.ExtensionFilter("Java files (*.java)", "*.java");
        javaFileChooser.getExtensionFilters().add(javaExtFilter);

        final MenuItem open = new MenuItem("Open");
        open.setOnAction(e -> {

            final File file = fileChooser.showOpenDialog(primaryStage);
            if (file != null) {
                clickerPane.readPointsFromFile(file);
            }
        });

        final MenuItem save = new MenuItem("Save");
        save.setOnAction(e -> {
            fileChooser.setInitialFileName(String.format("%s",  clickerPane.getClassName()));
            final File file = fileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                clickerPane.savePointsToFile(file);
            }
        });

        final MenuItem generateCode = new MenuItem("Generate");
        generateCode.setOnAction(e -> {
            javaFileChooser.setInitialFileName(String.format("%s",  clickerPane.getClassName()));
            final File file = javaFileChooser.showSaveDialog(primaryStage);
            if (file != null) {
                clickerPane.generateCode(file);
            }
        });

        final MenuItem undo = new MenuItem("Undo");
        undo.setOnAction(e -> {
            clickerPane.undo();
        });

        final MenuItem redo = new MenuItem("Redo");
        redo.setOnAction(e -> {
            clickerPane.redo();
        });


        final MenuItem clear = new MenuItem("Redo");
        clear.setOnAction(e -> {
            clickerPane.processCleanButtonOnPressed();
        });

        final MenuItem about = new MenuItem(("About"));
        about.setOnAction(e -> {
            if (Desktop.isDesktopSupported()){
                try {
                    Desktop.getDesktop().browse(new URI(GITHUB_README));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });

        final Menu menuFile = new Menu("File", null, open, save, generateCode);
        final Menu menuEdit = new Menu("Edit", null, undo, redo, clear);
        final Menu menuHelp = new Menu("Help", null, about);
        final MenuBar menuBar = new MenuBar(menuFile, menuEdit, menuHelp);

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