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
import javafx.stage.Stage;
import javafx.scene.control.MenuBar;

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

        clickerPane = new ProjectPane();

        Pane root = new Pane(clickerPane);
        Menu loadMenu = new Menu("File", null);
        MenuItem open = new MenuItem("Open");
        MenuItem save = new MenuItem("Save");
        loadMenu.getItems().add(open);
        loadMenu.getItems().add(save);
        MenuBar loadOptions = new MenuBar();
        VBox vBox = new VBox(loadOptions, clickerPane);
        loadOptions.getMenus().add(loadMenu);
        Scene scene = new Scene(vBox, 1050, 543, Color.BLANCHEDALMOND);
        scene.setFill(Color.BLANCHEDALMOND);
        primaryStage.setTitle("Duckinator 3000");
        primaryStage.setScene(scene);
        primaryStage.setWidth(1050);
        primaryStage.setHeight(543);
        primaryStage.setX(150);
        primaryStage.setY(150);
        primaryStage.show();
        primaryStage.setResizable(false);
    }

    public static void main(String[] args) {
        launch(args);
    }
}
