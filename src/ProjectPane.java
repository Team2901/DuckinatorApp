/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextArea;
import javafx.scene.control.*;
import javafx.scene.control.TextField;
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

public class ProjectPane extends Pane {

    private final static String DEFAULT_CLASS_NAME = "DuckinatorAuto";
    private static class WayPoint extends Circle {

        Color defaultColor;

        WayPoint lastWayPoint;
        WayPoint nextWayPoint;
        Line lastLine;

        int xPoint;
        int yPoint;

        double orgSceneX, orgSceneY;

        public WayPoint(WayPoint lastWayPoint, int xPoint, int yPoint, Color defaultColor, Pane parent) {
            super(xPoint, yPoint, 4, defaultColor);

            this.defaultColor = defaultColor;

            this.xPoint = xPoint;
            this.yPoint = yPoint;

            setLastWayPoint(lastWayPoint);

            parent.getChildren().add(this);

            if (lastLine != null) {
                parent.getChildren().add(lastLine);
            }
        }

        public void resetColor() {
            this.setFill(defaultColor);
        }

        public void delete(Pane parent) {
            final Line deleteLine;
            if (lastWayPoint != null) {
                deleteLine = this.lastLine;
            } else if (nextWayPoint != null) {
                deleteLine = nextWayPoint.lastLine;
            } else {
                deleteLine = null;
            }

            if (nextWayPoint != null) {
                nextWayPoint.setLastWayPoint(lastWayPoint);
            }

            if (deleteLine != null) {
                parent.getChildren().remove(deleteLine);
            }

            parent.getChildren().remove(this);
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

        public void setLastWayPoint(WayPoint lastWayPoint) {

            this.lastWayPoint = lastWayPoint;

            if (lastWayPoint != null) {
                this.defaultColor = Color.BLACK;

                lastWayPoint.nextWayPoint = this;

                if (this.lastLine == null) {
                    this.lastLine = new Line(lastWayPoint.getCenterX(), lastWayPoint.getCenterY(), xPoint, yPoint);
                } else {
                    this.lastLine.setStartX(lastWayPoint.getCenterX());
                    this.lastLine.setStartY(lastWayPoint.getCenterY());
                }
            } else {
                this.defaultColor = Color.RED;
            }

            this.resetColor();
        }
    }

    private final ImageView fieldHolder;
    private final ImageView duckHolder;
    final ArrayList<Integer> xPixel = new ArrayList<>();
    final ArrayList<Integer> yPixel = new ArrayList<>();
    final ArrayList<Double> lineLength = new ArrayList<>();
    final ArrayList<Double> actualPathLength = new ArrayList<>();
    final ArrayList<Double> encoderPathLength = new ArrayList<>();
    final ArrayList<Double> angleChanges = new ArrayList<>();
    final ArrayList<String> leftOrRight = new ArrayList<>();
    final ArrayList<String> movements = new ArrayList<>();
    ArrayList<WayPoint> wayPoints = new ArrayList<>();
    private final Button clear;
    private final Button generate;
    private final Rectangle rect;
    private final static int FIELD_MEASUREMENT_PIXELS = 510;
    private final TextArea code;
    private final Label classNameLabel;
    private final TextField classNameTextArea;
    private final RadioButton tankDrive;
    private final RadioButton holonomicDrive;
    private final RadioButton mecanumDrive;
    private final ToggleGroup drives;
    private int togglingKeep = 1;

    private final Label mouseLocationReporter;
    private final Label wayPointLocationReporter;

    private final Hyperlink github;

    private WayPoint movingWayPoint;

    private boolean editMode = false;

    private final String className = DEFAULT_CLASS_NAME;

    ContextMenu modeContextMenu;

    public ProjectPane (){
        rect = new Rectangle(1200, 600, Color.BLANCHEDALMOND);
        getChildren().add(rect);

        Image field = new Image(this.getClass().getResourceAsStream("/field.png"));
        fieldHolder = new ImageView(field);
        fieldHolder.setFitHeight(fieldMeasurementPixels);
        fieldHolder.setFitWidth(fieldMeasurementPixels);
        fieldHolder.setLayoutX(0);
        fieldHolder.setLayoutY(0);
        getChildren().add(fieldHolder);

        Image duck = new Image(this.getClass().getResourceAsStream("/duck.png"));
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

        generate = new Button("Generate Code");
        generate.setLayoutX(600);
        generate.setLayoutY(20);
        getChildren().add(generate);

        classNameLabel = new Label("Class Name: ");
        classNameLabel.setLayoutX(540);
        classNameLabel.setLayoutY(70);
        getChildren().add(classNameLabel);

        classNameTextArea = new TextField(DEFAULT_CLASS_NAME);
        classNameTextArea.setLayoutX(630);
        classNameTextArea.setLayoutY(67);
        getChildren().add(classNameTextArea);

        code = new TextArea("Click on the field to make points on a path for your robot to follow. \n\nThen, hit the \"Generate Code\" button to generate copy and paste-able code!");
        code.setLayoutX(540);
        code.setLayoutY(100);
        getChildren().add(code);

        drives = new ToggleGroup();

        tankDrive = new RadioButton("Tank Drive");
        tankDrive.setLayoutX(545);
        tankDrive.setLayoutY(300);
        tankDrive.setToggleGroup(drives);
        tankDrive.setSelected(true);
        getChildren().add(tankDrive);

        holonomicDrive = new RadioButton("X-Drive");
        holonomicDrive.setLayoutX(670);
        holonomicDrive.setLayoutY(300);
        holonomicDrive.setToggleGroup(drives);
        getChildren().add(holonomicDrive);

        mecanumDrive = new RadioButton("Mecanum");
        mecanumDrive.setLayoutX(795);
        mecanumDrive.setLayoutY(300);
        mecanumDrive.setToggleGroup(drives);
        getChildren().add(mecanumDrive);

        tankDrive.setOnAction(this::processRadioButtons);
        holonomicDrive.setOnAction(this::processRadioButtons);
        mecanumDrive.setOnAction(this::processRadioButtons);
        clear.setOnAction(this::processButtonPress);
        generate.setOnAction(this::generation);
        fieldHolder.setOnMouseClicked(this::processMousePress);
        github.setOnAction(this::hyperlink);

        this.setOnContextMenuRequested(this::handleModeMenuRequest);

        this.setOnKeyPressed(this::processWayPointKeyPress);

        Rectangle rect = new Rectangle(0, 0, 100, 100);
        Tooltip t = new Tooltip("A Square");
        Tooltip.install(rect, t);

        modeContextMenu = new ContextMenu();

        mouseLocationReporter = new Label();
        mouseLocationReporter.setLayoutX(0);
        mouseLocationReporter.setLayoutY(0);
        getChildren().add(mouseLocationReporter);


        wayPointLocationReporter = new Label();
        wayPointLocationReporter.setLayoutX(0);
        wayPointLocationReporter.setLayoutY(20);
        getChildren().add(wayPointLocationReporter);

        this.setOnMouseMoved(this::handleMouseTracking);
        this.setOnMouseDragged(this::handleMouseTracking);
    }

    public void handleMouseTracking(MouseEvent event) {
        double xInches = (int) Math.round(convertToInches(event.getSceneX()));
        double yInches = (int) Math.round(convertToInches(event.getSceneY()));
        String msg = String.format("Mouse: (%.1f, %.1f)", xInches, yInches);
        mouseLocationReporter.setText(msg);

        String msg2;
        if (movingWayPoint != null) {
            double xInches2 = (int) Math.round(convertToInches(movingWayPoint.getCenterX()));
            double yInches2 = (int) Math.round(convertToInches(movingWayPoint.getCenterY()));
            msg2  = String.format("Waypoint: (%.1f, %.1f)", xInches2, yInches2);
        } else {
            msg2 = "";
        }

        wayPointLocationReporter.setText(msg2);

    }

    public void hyperlink(ActionEvent e){
        if (e.getSource() == github){
            if (Desktop.isDesktopSupported()){
                try {
                    Desktop.getDesktop().browse(new URI("https://www.github.com/yup-its-rowan"));
                } catch (IOException | URISyntaxException e1) {
                    e1.printStackTrace();
                }
            }
        }
    }

    public void handleWayPointDrag(MouseEvent t) {

        if (t.getButton().equals(MouseButton.PRIMARY)) {

            WayPoint wayPoint = (WayPoint) t.getTarget();

            if (movingWayPoint == wayPoint) {
                double offsetX = t.getSceneX() - wayPoint.orgSceneX;
                double offsetY = t.getSceneY() - wayPoint.orgSceneY;

                wayPoint.orgSceneX = t.getSceneX();
                wayPoint.orgSceneY = t.getSceneY();

                wayPoint.updateCenter((int) (wayPoint.xPoint + offsetX), (int) (wayPoint.yPoint + offsetY));
            }
        }
    }

    public void handleWayPointClick(MouseEvent t) {

        if (t.getButton().equals(MouseButton.PRIMARY)) {

            if (!editMode) {
                return;
            }

            if (movingWayPoint != null) {
                movingWayPoint.resetColor();
            }

            WayPoint wayPoint = (WayPoint) t.getTarget();

            wayPoint.getParent().requestFocus();

            wayPoint.orgSceneX = t.getSceneX();
            wayPoint.orgSceneY = t.getSceneY();
            wayPoint.setFill(Color.GREEN);


            movingWayPoint = wayPoint;
        }
    }

    public void handleModeMenuRequest(ContextMenuEvent event) {

        if (modeContextMenu != null) {
            modeContextMenu.hide();
        }

        modeContextMenu = new ContextMenu();

        MenuItem editModeItem = new MenuItem("Edit");
        MenuItem placeModeItem = new MenuItem("Append");

        editModeItem.setOnAction(e -> editMode = true);

        placeModeItem.setOnAction(e -> {
            editMode = false;

            if (movingWayPoint != null) {
                movingWayPoint.resetColor();
            }

            movingWayPoint = null;
        });

        if (editMode) {
            modeContextMenu.getItems().add(placeModeItem);
        } else {
            modeContextMenu.getItems().add(editModeItem);
        }

        modeContextMenu.show((Node) event.getTarget(), event.getScreenX(), event.getScreenY());
    }

    public void processWayPointKeyPress(final KeyEvent keyEvent) {

        System.out.println(keyEvent.getCode());
        System.out.println(keyEvent.getText());

        if (!editMode || movingWayPoint == null) {
            return;
        }

        if (keyEvent.getCode().equals( KeyCode.DELETE)) {
            wayPoints.remove(movingWayPoint);
            movingWayPoint.delete(this);

        } else if (keyEvent.getCode().isArrowKey()) {

            int stepSize = 1;
            int dx = 0;
            int dy = 0;

            switch (keyEvent.getCode()) {

                case UP:
                    dy = -stepSize;
                    break;
                case DOWN:
                    dy = stepSize;
                    break;
                case LEFT:
                    dx = -stepSize;
                    break;
                case RIGHT:
                    dx = stepSize;
                    break;
            }
            movingWayPoint.updateCenter(movingWayPoint.xPoint + dx, movingWayPoint.yPoint + dy);
        }
    }

    public void processMousePress(MouseEvent e){

        if (e.getButton().equals(MouseButton.PRIMARY)) {


            if (e.getSource() == fieldHolder) {

                if (editMode) {
                    return;
                }

                Point point = new Point();
                point.x = (int) e.getSceneX();
                point.y = (int) e.getSceneY();

                boolean firstPoint = wayPoints.size() == 0;

                WayPoint lastWayPoint = firstPoint ? null : wayPoints.get(wayPoints.size() -1);
                Color defaultColor = firstPoint ? Color.RED : Color.BLACK;

                WayPoint wayPoint = new WayPoint(lastWayPoint, (int) e.getSceneX(), (int) e.getSceneY(), defaultColor, this);
                wayPoint.setOnMousePressed(this::handleWayPointClick);
                wayPoint.setOnMouseDragged(this::handleWayPointDrag);
                wayPoint.setOnKeyPressed(this::processWayPointKeyPress);
                wayPoint.setOnKeyTyped(this::processWayPointKeyPress);

                wayPoints.add(wayPoint);
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
            getChildren().add(github);
            if (togglingKeep ==1){
                tankDrive.setSelected(true);
            }else if (togglingKeep ==2){
                holonomicDrive.setSelected(true);
            } else if (togglingKeep == 3) {
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
            wayPoints = new ArrayList<>();
            movingWayPoint = null;
            editMode = false;
        }
    }

    public double getTargetAngle(WayPoint wayPoint1, WayPoint wayPoint2){
        double dx = wayPoint2.xPoint-wayPoint1.xPoint;
        double dy = wayPoint2.yPoint-wayPoint1.yPoint;
        double targetAnglePi = Math.atan2(dy, dx);
        double targetAngle = ((targetAnglePi*180)/Math.PI);
        return normalizeAngle(targetAngle);
    }

    public double getLineLength(WayPoint wayPoint1, WayPoint wayPoint2){
        double dx = wayPoint2.xPoint-wayPoint1.xPoint;
        double dy = wayPoint2.yPoint-wayPoint1.yPoint;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
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

    public void generation(ActionEvent DIO){

        if (DIO.getSource()==generate){
            if (wayPoints.size()>0){

                String className = classNameTextArea.getText();

                if (className == null || className.isBlank()) {
                    className = DEFAULT_CLASS_NAME;
                }

                className = className.trim();

                code.setText(
                        "package org.firstinspires.ftc.teamcode.Autonomous;\n" +
                                "import com.qualcomm.robotcore.eventloop.opmode.Autonomous;\n" +
                                "\n" +
                                "/**\n" +
                                " * Created with Team 6183's Duckinator 3000\n" +
                                " */\n" +
                                "\n" +
                                "@Autonomous(name = \""+className+"\", group = \"DuckSquad\")\n" +
                                "public class "+ className + " extends " + getBaseClass() + " {\n" +
                                "    @Override\n" +
                                "    public void runOpMode() throws InterruptedException {\n" +
                                "        initRobot();\n" +
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

    private String getBaseClass() {

        switch (togglingKeep) {
            case 1:
                return "BaseDominatorTankDrive";
            case 2:
                return "BaseDominatorXDrive";
            case 3:
            default:
                return "BaseDominatorMechanum";
        }
    }

    private String convertArrayList(ArrayList<String> stringList) {
        return String.join("", stringList);
    }

    private double normalizeAngle(double angle) {
        return ((angle + 180) % 360) - 180;
    }

    private String moveHere() {
        ArrayList<String> movements = new ArrayList<>();

        if (wayPoints.size() >= 2) {

            double currentAngle = 0;

            for (int i = 1; i < wayPoints.size(); i++) {

                WayPoint lastPoint = wayPoints.get(i - 1);
                WayPoint targetPoint = wayPoints.get(i);

                double targetAngle = getTargetAngle(lastPoint, targetPoint);

                double diffAngle = normalizeAngle(targetAngle - currentAngle);
                int diffAngleInt = (int) Math.round(diffAngle);

                currentAngle = targetAngle;

                // Only add if different and not the first point
                if (diffAngleInt != 0 && i != 1) {
                    movements.add("            rotate(" + diffAngleInt + ");\n");
                }

                double pathLength = getLineLength(lastPoint, targetPoint);
                int encoderPathLengthInt = (int) Math.round(convertToInches(pathLength));

                movements.add("            goForward(" + encoderPathLengthInt + ");\n");
            }
        }

        return convertArrayList(movements);
    }

    private double convertToInches(double pixelValue) {
        int fieldMeasurementInches = 144;
        double conversionFactorPixelInch = ((double) fieldMeasurementInches / (double) fieldMeasurementPixels);
        return pixelValue * conversionFactorPixelInch;
    }
}
