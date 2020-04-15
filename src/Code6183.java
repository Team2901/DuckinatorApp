/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.stage.Stage;

/**
 *
 * @author akkir
 */
public class Code6183 extends Application{

    public ProjectPane clickerPane;

    /**
     * @param args the command line arguments
     */
    @Override
    public void start(Stage primaryStage) {

        clickerPane = new ProjectPane();

        Pane root = new Pane(clickerPane);

        Scene scene = new Scene(root, 0, 0);
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
