/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.scene.control.MenuBar;

import java.io.File;
import java.io.IOException;

/**
 *
 * @author akkir
 */
public class Code6183 extends Application{

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
         */

        FileChooser fileChooser = new FileChooser();

        clickerPane = new ProjectPane();

        Pane root = new Pane(clickerPane);
        Menu loadMenu = new Menu("File", null);
        MenuItem open = new MenuItem("Open");
        open.setOnAction(e -> {
            File selectedFile = fileChooser.showOpenDialog(primaryStage);
            try {
                clickerPane.loadPoints(selectedFile);
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
        MenuBar loadOptions = new MenuBar(loadMenu);
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
