/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.*;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.ArrayList;

/**
 * @author akkir
 */

public class ProjectPane extends Pane{

    private static class WayPoint extends Circle {

        WayPoint lastWaypoint;
        WayPoint nextWayPoint;
        Line lastLine;

        double xPoint;
        double yPoint;

        double orgSceneX, orgSceneY;

        public WayPoint(WayPoint lastWaypoint, int xPoint, int yPoint) {
            super(xPoint, yPoint, 4);

            this.xPoint = xPoint;
            this.yPoint = yPoint;

            if (lastWaypoint != null) {
                lastWaypoint.nextWayPoint = this;
                this.lastWaypoint = lastWaypoint;
                lastLine = new Line(lastWaypoint.getCenterX(), lastWaypoint.getCenterY(), xPoint, yPoint);
            }

        }

        public void updateCenter( int xPoint, int yPoint) {

            this.xPoint = xPoint;
            this.yPoint = yPoint;

            setCenterX(xPoint);
            setCenterY(yPoint);

            if (lastLine != null) {
                lastLine.setEndX(xPoint);
                lastLine.setEndY(yPoint);
            }

            if (nextWayPoint != null && nextWayPoint.lastLine != null) {
                nextWayPoint.lastLine.setStartX(getCenterX());
                nextWayPoint.lastLine.setStartY(getCenterY());
            }
        }
    }

    private Image field, duck;
    private ImageView fieldHolder, duckHolder;
    ArrayList<Integer> xPixel = new ArrayList<Integer>();
    ArrayList<Integer> yPixel = new ArrayList<Integer>();
    ArrayList<Double> lineLength = new ArrayList<Double>();
    ArrayList<Double> actualPathLength = new ArrayList<Double>();
    ArrayList<Double> encoderPathLength = new ArrayList<Double>();
    ArrayList<Double> angleChanges = new ArrayList<Double>();
    ArrayList<String> leftOrRight = new ArrayList<String>();
    ArrayList<String> movements = new ArrayList<String>();
    ArrayList<Point> points = new ArrayList<Point>();
    private Button clear, generate;
    private Rectangle rect;
    private int fieldMeasurementPixels = 510;
    private int fieldMeasurementInches = 144;
    private double conversionFactorPixelInch = ((double) fieldMeasurementInches/ (double) fieldMeasurementPixels);
    private double wheelDiameter = 4;
    private double ticksPerRotation = 1120;
    private TextArea code;
    private RadioButton tankDrive, holonomicDrive, mecanumDrive;
    private ToggleGroup drives;
    private int togglingKeep = 1;
    private Label wheelDi, ticksPer;
    private TextField wheelDia, ticksPerr;
    private Label careful;
    private Hyperlink github;
    private WayPoint selectedWayPoint;

    private WayPoint finalWayPoint;

    ContextMenu contextMenu;

    public ProjectPane (){
        rect = new Rectangle(1200, 600, Color.BLANCHEDALMOND);
        getChildren().add(rect);

        field = new Image(this.getClass().getResourceAsStream("/field.png"));
        fieldHolder = new ImageView(field);
        fieldHolder.setFitHeight(fieldMeasurementPixels);
        fieldHolder.setFitWidth(fieldMeasurementPixels);
        fieldHolder.setLayoutX(0);
        fieldHolder.setLayoutY(0);
        getChildren().add(fieldHolder);

        duck = new Image(this.getClass().getResourceAsStream("/duck.png"));
        duckHolder = new ImageView(duck);
        duckHolder.setFitHeight(150);
        duckHolder.setFitWidth(163);
        duckHolder.setLayoutX(870);
        duckHolder.setLayoutY(350);
        getChildren().add(duckHolder);

        clear = new Button("Clear");
        clear.setLayoutX(540);
        clear.setLayoutY(20);
        getChildren().add(clear);

        github = new Hyperlink("github.com/yup-its-rowan");
        github.setLayoutX(850);
        github.setLayoutY(22);
        getChildren().add(github);

        careful = new Label("Careful: \nAdjusting wheel dia or TPR mid-path affects the resulting code");
        careful.setLayoutX(540);
        careful.setLayoutY(450);
        getChildren().add(careful);

        wheelDi = new Label("Wheel Diameter (Inches): ");
        wheelDi.setLayoutX(540);
        wheelDi.setLayoutY(320);
        getChildren().add(wheelDi);

        wheelDia = new TextField("4");
        wheelDia.setLayoutX(680);
        wheelDia.setLayoutY(317);
        getChildren().add(wheelDia);

        ticksPer = new Label("Ticks Per Rotation: ");
        ticksPer.setLayoutX(540);
        ticksPer.setLayoutY(360);
        getChildren().add(ticksPer);

        ticksPerr = new TextField("1120");
        ticksPerr.setLayoutX(680);
        ticksPerr.setLayoutY(357);
        getChildren().add(ticksPerr);

        generate = new Button("Generate Code");
        generate.setLayoutX(600);
        generate.setLayoutY(20);
        getChildren().add(generate);

        code = new TextArea("Click on the field to make points on a path for your robot to follow. \n\nThen, hit the \"Generate Code\" button to generate copy and pastable code!");
        code.setLayoutX(540);
        code.setLayoutY(70);
        getChildren().add(code);

        drives = new ToggleGroup();

        tankDrive = new RadioButton("Tank Drive");
        tankDrive.setLayoutX(545);
        tankDrive.setLayoutY(270);
        tankDrive.setToggleGroup(drives);
        tankDrive.setSelected(true);
        getChildren().add(tankDrive);

        holonomicDrive = new RadioButton("X-Drive");
        holonomicDrive.setLayoutX(670);
        holonomicDrive.setLayoutY(270);
        holonomicDrive.setToggleGroup(drives);
        getChildren().add(holonomicDrive);

        mecanumDrive = new RadioButton("Mecanum");
        mecanumDrive.setLayoutX(795);
        mecanumDrive.setLayoutY(270);
        mecanumDrive.setToggleGroup(drives);
        getChildren().add(mecanumDrive);

        tankDrive.setOnAction(this::processRadioButtons);
        holonomicDrive.setOnAction(this::processRadioButtons);
        mecanumDrive.setOnAction(this::processRadioButtons);
        clear.setOnAction(this::processButtonPress);
        generate.setOnAction(this::generation);
        fieldHolder.setOnMouseClicked(this::processMousePress);
        code.setOnKeyPressed(this::processKeyPress);
        github.setOnAction(this::hyperlinky);

        contextMenu = new ContextMenu();
        // create menuitems
        MenuItem menuItem1 = new MenuItem("menu item 1");
        MenuItem menuItem2 = new MenuItem("menu item 2");
        MenuItem menuItem3 = new MenuItem("menu item 3");

        contextMenu.getItems().add(menuItem1);
        contextMenu.getItems().add(menuItem2);
        contextMenu.getItems().add(menuItem3);

    }

    public void hyperlinky(ActionEvent eeeee){
        if (eeeee.getSource() == github){
            if (Desktop.isDesktopSupported()){
                try {
                    Desktop.getDesktop().browse(new URI("https://www.github.com/yup-its-rowan"));
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void handleWayPointDrag(MouseEvent t) {

        if (t.getButton().equals(MouseButton.PRIMARY)) {

            System.out.println("dragging");
            WayPoint wayPoint = (WayPoint) t.getTarget();

            double offsetX = t.getSceneX() - wayPoint.orgSceneX;
            double offsetY = t.getSceneY() - wayPoint.orgSceneY;


            wayPoint.orgSceneX = t.getSceneX();
            wayPoint.orgSceneY = t.getSceneY();

            wayPoint.updateCenter((int) (wayPoint.xPoint + offsetX), (int) (wayPoint.yPoint + offsetY));
        }
    }

    public void handleWayPointClick(MouseEvent t) {

        if (t.getButton().equals(MouseButton.PRIMARY)) {
            System.out.println("click");
            WayPoint wayPoint = (WayPoint) t.getTarget();

            wayPoint.orgSceneX = t.getSceneX();
            wayPoint.orgSceneY = t.getSceneY();

            if (selectedWayPoint != null) {
                selectedWayPoint.setFill(Color.BLACK);
            }

            wayPoint.setFill(Color.GREEN);
            selectedWayPoint = wayPoint;
        }
    }

    public void handleContactMenuRequest(ContextMenuEvent event) {
        contextMenu.show((Node) event.getTarget(), event.getScreenX(), event.getScreenY());
    }

    public void processMousePress(MouseEvent e){

        if (e.getButton().equals(MouseButton.PRIMARY)) {

            if (e.getSource() == fieldHolder) {
                Point point = new Point();
                point.x = (int) e.getSceneX();
                point.y = (int) e.getSceneY();

                points.add(point);

                WayPoint wayPoint = new WayPoint(finalWayPoint, (int) e.getSceneX(), (int) e.getSceneY());
               wayPoint.setOnMousePressed(this::handleWayPointClick);
               wayPoint.setOnMouseDragged(this::handleWayPointDrag);

                wayPoint.setOnContextMenuRequested(this::handleContactMenuRequest);


                getChildren().add(wayPoint);

                if (finalWayPoint == null) {
                    wayPoint.setFill(Color.RED);
                }

                if (wayPoint.lastLine != null) {
                    getChildren().add(wayPoint.lastLine);
                }

                finalWayPoint = wayPoint;

            }
        }
    }

    public void processButtonPress(ActionEvent ev){
        if (ev.getSource() == clear){
            drives.selectToggle(null);
            getChildren().clear();
            getChildren().add(rect);
            getChildren().add(clear);
            getChildren().add(generate);
            getChildren().add(fieldHolder);
            getChildren().add(code);
            getChildren().add(duckHolder);
            getChildren().add(tankDrive);
            getChildren().add(mecanumDrive);
            getChildren().add(holonomicDrive);
            getChildren().add(ticksPer);
            getChildren().add(ticksPerr);
            getChildren().add(wheelDi);
            getChildren().add(wheelDia);
            getChildren().add(careful);
            getChildren().add(github);
            if (togglingKeep ==1){
                tankDrive.setSelected(true);
            }else if (togglingKeep ==2){
                holonomicDrive.setSelected(true);
            } else if (togglingKeep == 3){
                mecanumDrive.setSelected(true);
            }
            xPixel.clear();
            yPixel.clear();
            lineLength.clear();
            actualPathLength.clear();
            encoderPathLength.clear();
            leftOrRight.clear();
            angleChanges.clear();
            code.clear();
            movements.clear();
            finalWayPoint = null;
            points = new ArrayList<>();
        }
    }

    public double convertInchesToEncoderTicks(double c){
        return ((c/(Math.PI*wheelDiameter))*ticksPerRotation);
    }

    public double getTargetAngle(double x1, double x2, double y1, double y2){
        double dx = x2-x1;
        double dy = y2-y1;
        double targetAnglePi = Math.atan2(dy, dx);
        double targetAngle = ((targetAnglePi*180)/Math.PI);
        return normalizeAngle(targetAngle);
    }

    public double getLineLength(double x1, double x2, double y1, double y2){
        double dx = x2-x1;
        double dy = y2-y1;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    public void processKeyPress(KeyEvent event){
        if (event.getCode()== KeyCode.ENTER){
            if ((code.getText().equals("6183"))||(code.getText().equals("duck"))){
                code.setText("quack quack losers");
            }
        }
    }

    public void processRadioButtons(ActionEvent e){

        if ((e.getSource() == tankDrive)){
            togglingKeep = 1;
        } else if (e.getSource() == holonomicDrive){
            togglingKeep = 2;

        } else if (e.getSource() == mecanumDrive){
            togglingKeep = 3;
        }
    }

    public void robotSpecs(){
        wheelDiameter = new Double(wheelDia.getText());
        ticksPerRotation = new Double(ticksPerr.getText());
    }

    public void generation(ActionEvent DIO){
        robotSpecs();

        if (DIO.getSource()==generate){
            if (points.size()>0){
                code.setText(
                        "package org.firstinspires.ftc.teamcode;\n" +
                                "import com.qualcomm.hardware.bosch.BNO055IMU;\n" +
                                "import com.qualcomm.robotcore.eventloop.opmode.Autonomous;\n" +
                                "import com.qualcomm.robotcore.eventloop.opmode.LinearOpMode;\n" +
                                "import com.qualcomm.robotcore.hardware.DcMotor;\n" +
                                "\n" +
                                "import org.firstinspires.ftc.robotcore.external.navigation.AngleUnit;\n" +
                                "import org.firstinspires.ftc.robotcore.external.navigation.AxesOrder;\n" +
                                "import org.firstinspires.ftc.robotcore.external.navigation.AxesReference;\n" +
                                "import org.firstinspires.ftc.robotcore.external.navigation.Orientation;\n" +
                                "import org.firstinspires.ftc.robotcore.external.navigation.Position;\n" +
                                "import org.firstinspires.ftc.robotcore.external.navigation.Velocity;\n" +
                                "\n" +
                                "/**\n" +
                                " * Created with Team 6183's Duckinator 3000\n" +
                                " */\n" +
                                "\n" +
                                "@Autonomous(name = \"DuckinatorAuto\", group = \"DuckSquad\")\n" +
                                "public class DuckinatorAuto extends LinearOpMode {\n" +
                                "    @Override\n" +
                                "    public void runOpMode() throws InterruptedException {\n" +
                                "        waitForStart();\n" +
                                "        if (opModeIsActive()){\n" +
                                moveHere() +
                                "\n" +
                                "        }\n" +
                                "    }\n" +
                                "}"
                );
            }
        }
    }

    private String convertArrayList(ArrayList<String> stringList) {
        String joinedString = String.join("", stringList);
        return joinedString;
    }

    private double normalizeAngle(double angle) {
        return ((angle + 180) % 360) - 180;
    }

    private String moveHere() {
        ArrayList<String> movements = new ArrayList<String>();

        if (points.size() > 2) {

            double currentAngle = 0;

            for (int i = 1; i < points.size(); i++) {

                // turn to face point
                // go forward

                Point lastPoint = points.get(i - 1);
                Point targetPoint = points.get(i);

                double targetAngle = getTargetAngle(lastPoint.x, targetPoint.x, lastPoint.y, targetPoint.y);

                double diffAngle = normalizeAngle(targetAngle - currentAngle);
                int diffAngleInt = (int) Math.round(diffAngle);

                currentAngle = targetAngle;

                // Only add if different and not the first point
                if (diffAngleInt != 0 && i != 1) {
                    movements.add("            rotate(" + diffAngleInt + ");\n");
                }

                double pathLength = getLineLength(lastPoint.x, targetPoint.x, lastPoint.y, targetPoint.y);
                double actualPathLength = pathLength * conversionFactorPixelInch;
                double encoderPathLength = convertInchesToEncoderTicks(actualPathLength);

                int encoderPathLengthInt = (int) Math.round(encoderPathLength);

                movements.add("            goForward(" + encoderPathLengthInt + ");\n");
            }
        }

        return convertArrayList(movements);
    }
}
