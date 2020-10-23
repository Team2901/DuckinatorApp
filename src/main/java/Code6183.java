package main.java;

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

public class Code6183 extends Application {

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

        /*
         * Links to javafx tutorials
         * Creating a menu bar: http://tutorials.jenkov.com/javafx/menubar.html
         * Creating a file chooser (to navigate your local files): http://tutorials.jenkov.com/javafx/filechooser.html
         * Setting a default file chooser directory: https://stackoverflow.com/questions/14256588/opening-a-javafx-filechooser-in-the-user-directory
         * Opening a web browser: https://blog.ngopal.com.np/2011/02/09/open-default-browser-in-javafx/
         */

        ProjectPane clickerPane = new ProjectPane();

        FileChooser fileChooser = new FileChooser();
        File file = new File("DuckinatorPathways");
        if (!file.exists()) {
            file.mkdir();
        }
        fileChooser.setInitialDirectory(file);

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extensionFilter);

        Menu loadMenu = new Menu("File", null);

        MenuItem open = new MenuItem("Open");
        open.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            try {
                if (selectedFile != null) {
                    clickerPane.loadPoints(selectedFile);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        loadMenu.getItems().add(open);

        MenuItem save = new MenuItem("Save");
        save.setOnAction(e -> {
            fileChooser.setInitialFileName(clickerPane.getRootName());
            File selectedFile = fileChooser.showSaveDialog(primaryStage);
            clickerPane.savePoints(selectedFile);
        });
        loadMenu.getItems().add(save);

        FileChooser fileChooserForCode = new FileChooser();
        File fileForCode = new File("DuckinatorCode");
        if (!fileForCode.exists()) {
            fileForCode.mkdir();
        }
        fileChooserForCode.setInitialDirectory(fileForCode);

        FileChooser.ExtensionFilter extensionFilterForCode = new FileChooser.ExtensionFilter("Java files (*.java)", "*.java");
        fileChooserForCode.getExtensionFilters().add(extensionFilterForCode);

        MenuItem generateTheCode = new MenuItem("Generate Code");
        generateTheCode.setOnAction(e -> {
            fileChooserForCode.setInitialFileName(clickerPane.getRootName());
            File selectedCodeFile = fileChooserForCode.showSaveDialog(primaryStage);
            clickerPane.generateCode(selectedCodeFile);
        });
        loadMenu.getItems().add(generateTheCode);

        Menu edit = new Menu("Edit", null);

        MenuItem redo = new MenuItem("Redo");
        redo.setOnAction(e -> clickerPane.redo());
        edit.getItems().add(redo);

        MenuItem undo = new MenuItem("Undo");
        undo.setOnAction(e -> clickerPane.undo());
        edit.getItems().add(undo);

        MenuItem clear = new MenuItem(("Clear"));
        clear.setOnAction(e -> clickerPane.processClearPress());
        edit.getItems().add(clear);

        Menu help = new Menu("Help", null);

        MenuItem about = new MenuItem(("About"));
        about.setOnAction(e -> {
            if (Desktop.isDesktopSupported()) {
                try {
                    Desktop.getDesktop().browse(new URI("https://github.com/Team2901/PurpleGearsPathfinder/blob/master/README.md"));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        });
        help.getItems().add(about);

        MenuBar loadOptions = new MenuBar(loadMenu, edit, help);
        VBox vBox = new VBox(loadOptions, clickerPane);
        Scene scene = new Scene(vBox, 1050, 543, Color.BLANCHEDALMOND);
        scene.setFill(Color.BLANCHEDALMOND);
        primaryStage.setTitle("Duckinator 3000");
        primaryStage.setScene(scene);
        primaryStage.setX(150);
        primaryStage.setY(150);
        primaryStage.show();
        primaryStage.setResizable(false);
    }
}
