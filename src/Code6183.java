/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;

/**
 * @author akkir
 */
public class Code6183 extends Application {

    public ProjectPane clickerPane;

    /**
     * @param primaryStage the command line arguments
     */

    //Added comment
    @Override
    public void start(Stage primaryStage) {

        /*
         * How to create a menu bar for housing the save/load options (http://tutorials.jenkov.com/javafx/menubar.html)
         * How to create a popup to navigate your file system (http://tutorials.jenkov.com/javafx/filechooser.html)
         * Default directory: https://stackoverflow.com/questions/14256588/opening-a-javafx-filechooser-in-the-user-directory
         */

        FileChooser fileChooser = new FileChooser();
        File file = new File("DuckinatorPathways");
        if(!file.exists()){
            file.mkdir();
        }
        fileChooser.setInitialDirectory(file);

        FileChooser.ExtensionFilter extensionFilter = new FileChooser.ExtensionFilter("CSV files (*.csv)", "*.csv");
        fileChooser.getExtensionFilters().add(extensionFilter);

        clickerPane = new ProjectPane();

        Menu loadMenu = new Menu("File", null);
        MenuItem open = new MenuItem("Open");
        open.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            try {
                if (selectedFile != null){
                    clickerPane.loadPoints(selectedFile);
                }
            } catch (IOException ioException) {
                ioException.printStackTrace();
            }
        });
        MenuItem save = new MenuItem("Save");
        save.setOnAction(e -> {
            File selectedFile = fileChooser.showSaveDialog(primaryStage);
            clickerPane.savePoints(selectedFile);
        });
        loadMenu.getItems().add(open);
        loadMenu.getItems().add(save);

        FileChooser fileChooserForCode = new FileChooser();
        File fileForCode = new File("DuckinatorCode");
        if(!fileForCode.exists()){
            fileForCode.mkdir();
        }
        fileChooserForCode.setInitialDirectory(fileForCode);

        FileChooser.ExtensionFilter extensionFilterForCode = new FileChooser.ExtensionFilter("Java files (*.java)", "*.java");
        fileChooserForCode.getExtensionFilters().add(extensionFilterForCode);

        MenuItem generateTheCode = new MenuItem("Generate Code");
        generateTheCode.setOnAction(e -> {
            File selectedCodeFile = fileChooserForCode.showSaveDialog(primaryStage);
            clickerPane.generateCode(selectedCodeFile);
        });

        loadMenu.getItems().add(generateTheCode);

        Menu edit = new Menu("Edit",null);
        MenuItem redo = new MenuItem("Redo");
        redo.setOnAction(e -> {
            clickerPane.redo();
        });
        MenuItem undo = new MenuItem("Undo");
        undo.setOnAction(e -> {
            clickerPane.undo();
        });
        edit.getItems().add(redo);
        edit.getItems().add(undo);

        MenuBar loadOptions = new MenuBar(loadMenu,edit);
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



    public static void main(String[] args) {
        launch(args);
    }
}
