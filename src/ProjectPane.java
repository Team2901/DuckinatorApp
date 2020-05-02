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
    protected final static int FIELD_MEASUREMENT_PIXELS = 510;
    protected final static int FIELD_MEASUREMENT_INCHES = 144;

    private final TextArea code;
    private final TextField classNameTextArea;
    private int togglingKeep = 1;

    private final Label wayPointLocationReporter;

    ContextMenu modeContextMenu;

    private final ArrayList<WayPoint> wayPoints = new ArrayList<>();
    private WayPoint movingWayPoint;
    private boolean editMode = false;

    public ProjectPane () {
        Rectangle rect1 = new Rectangle(1200, 600, Color.BLANCHEDALMOND);
        getChildren().add(rect1);

        Image field = new Image(this.getClass().getResourceAsStream("/field.png"));
        ImageView fieldHolder = new ImageView(field);
        fieldHolder.setFitHeight(FIELD_MEASUREMENT_PIXELS);
        fieldHolder.setFitWidth(FIELD_MEASUREMENT_PIXELS);
        fieldHolder.setLayoutX(0);
        fieldHolder.setLayoutY(0);
        getChildren().add(fieldHolder);

        Image duck = new Image(this.getClass().getResourceAsStream("/duck.png"));
        ImageView duckHolder = new ImageView(duck);
        duckHolder.setFitHeight(150);
        duckHolder.setFitWidth(163);
        duckHolder.setLayoutX(870);
        duckHolder.setLayoutY(350);
        getChildren().add(duckHolder);

        Button clear = new Button("Clear");
        clear.setLayoutX(540);
        clear.setLayoutY(20);
        getChildren().add(clear);

        Hyperlink github = new Hyperlink("github.com/yup-its-rowan");
        github.setLayoutX(850);
        github.setLayoutY(22);
        getChildren().add(github);

        Button generate = new Button("Generate Code");
        generate.setLayoutX(600);
        generate.setLayoutY(20);
        getChildren().add(generate);

        final Label classNameLabel;
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

        ToggleGroup drives = new ToggleGroup();

        RadioButton tankDrive = new RadioButton("Tank Drive");
        tankDrive.setLayoutX(545);
        tankDrive.setLayoutY(300);
        tankDrive.setOnAction((e) -> togglingKeep = 1);
        tankDrive.setToggleGroup(drives);
        tankDrive.setSelected(true);
        getChildren().add(tankDrive);

        RadioButton holonomicDrive = new RadioButton("X-Drive");
        holonomicDrive.setLayoutX(670);
        holonomicDrive.setLayoutY(300);
        holonomicDrive.setOnAction((e) -> togglingKeep = 2);
        holonomicDrive.setToggleGroup(drives);
        getChildren().add(holonomicDrive);

        RadioButton mecanumDrive = new RadioButton("Mecanum");
        mecanumDrive.setLayoutX(795);
        mecanumDrive.setLayoutY(300);
        mecanumDrive.setOnAction((e) -> togglingKeep = 3);
        mecanumDrive.setToggleGroup(drives);
        getChildren().add(mecanumDrive);

        clear.setOnAction(this::processCleanButtonOnPressed);
        generate.setOnAction(this::generation);
        fieldHolder.setOnMouseClicked(this::processFieldHolderOnMousePressed);
        github.setOnAction((e) -> {
            if (Desktop.isDesktopSupported()){
            try {
                Desktop.getDesktop().browse(new URI("https://www.github.com/yup-its-rowan"));
            } catch (IOException | URISyntaxException e1) {
                e1.printStackTrace();
            }
        }});

        this.setOnContextMenuRequested(this::processModeMenuRequest);

        this.setOnKeyPressed(this::processFieldHolderOnKeyPressed);

        final Label mouseLocationReporter = new Label();
        mouseLocationReporter.setLayoutX(0);
        mouseLocationReporter.setLayoutY(0);
        getChildren().add(mouseLocationReporter);

        wayPointLocationReporter = new Label();
        wayPointLocationReporter.setLayoutX(0);
        wayPointLocationReporter.setLayoutY(20);
        getChildren().add(wayPointLocationReporter);

        EventHandler<MouseEvent> processMouseMovement = event -> {
            final String msg = String.format("Mouse: %s", formatLocation(event.getSceneX(), event.getSceneY()));
            mouseLocationReporter.setText(msg);
        };

        this.setOnMouseMoved(processMouseMovement);
        this.setOnMouseDragged(processMouseMovement);
    }

    public void reportMovingWayPointLocation(WayPoint wayPoint) {

        final String msg;
        if (wayPoint != null) {
            msg = String.format("WayPoint: %s", formatLocation(wayPoint.xPoint, wayPoint.yPoint));
        } else {
            msg = null;
        }

        wayPointLocationReporter.setText(msg);
    }

    public void processCleanButtonOnPressed(ActionEvent e) {

        setMovingWayPoint(null);

        for (WayPoint wayPoint : wayPoints) {
            wayPoint.delete(this);
        }

        wayPoints.clear();
        code.clear();
        editMode = false;
    }

    public void processModeMenuRequest(ContextMenuEvent me) {

        if (modeContextMenu != null) {
            modeContextMenu.hide();
        }

        modeContextMenu = new ContextMenu();

        MenuItem editModeItem = new MenuItem("Edit");
        MenuItem placeModeItem = new MenuItem("Append");

        editModeItem.setOnAction(e -> editMode = true);

        placeModeItem.setOnAction(e -> {
            editMode = false;
            setMovingWayPoint(null);
        });

        if (editMode) {
            modeContextMenu.getItems().add(placeModeItem);
        } else {
            modeContextMenu.getItems().add(editModeItem);
        }

        modeContextMenu.show((Node) me.getTarget(), me.getScreenX(), me.getScreenY());
    }

    public void processWayPointOnMouseDragged(MouseEvent e) {

        if (e.getButton().equals(MouseButton.PRIMARY)) {

            WayPoint wayPoint = (WayPoint) e.getTarget();

            if (movingWayPoint == wayPoint) {

                double newX = wayPoint.pressOffsetX + e.getSceneX();
                double newY = wayPoint.pressOffsetY + e.getSceneY();

                wayPoint.updateCenter(newX, newY);
                reportMovingWayPointLocation(movingWayPoint);
            }
        }
    }

    public void processWayPointOnMousePressed(MouseEvent e) {

        if (e.getButton().equals(MouseButton.PRIMARY)) {

            if (!editMode) {
                processFieldHolderOnMousePressed(e);
                return;
            }

            WayPoint wayPoint = (WayPoint) e.getTarget();

            wayPoint.pressOffsetX = wayPoint.xPoint - e.getSceneX();
            wayPoint.pressOffsetY = wayPoint.yPoint - e.getSceneY();

            setMovingWayPoint(wayPoint);

            wayPoint.getParent().requestFocus();
        }
    }

    public void processFieldHolderOnKeyPressed(final KeyEvent e) {

        if (!editMode || movingWayPoint == null) {
            return;
        }

        if (e.getCode().equals( KeyCode.DELETE)) {
            wayPoints.remove(movingWayPoint);
            movingWayPoint.delete(this);
            setMovingWayPoint(null);
            reportMovingWayPointLocation(null);
        } else if (e.getCode().isArrowKey()) {

            int stepSize = 1;
            int dx = 0;
            int dy = 0;

            switch (e.getCode()) {

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
            reportMovingWayPointLocation(movingWayPoint);
        }
    }

    public void processFieldHolderOnMousePressed(MouseEvent e){

        if (e.getButton().equals(MouseButton.PRIMARY)) {

            if (editMode) {
                return;
            }

            boolean firstPoint = wayPoints.size() == 0;

            WayPoint lastWayPoint = firstPoint ? null : wayPoints.get(wayPoints.size() -1);
            Color defaultColor = firstPoint ? Color.RED : Color.BLACK;

            WayPoint wayPoint = new WayPoint(lastWayPoint, e.getSceneX(), e.getSceneY(), defaultColor, this);
            wayPoint.setOnMousePressed(this::processWayPointOnMousePressed);
            wayPoint.setOnMouseDragged(this::processWayPointOnMouseDragged);
            wayPoints.add(wayPoint);
        }
    }

    private void setMovingWayPoint(WayPoint wayPoint) {

        if (movingWayPoint != null) {
            movingWayPoint.setSelected(false);
        }

        if (wayPoint != null) {
            wayPoint.setSelected(true);
        }

        movingWayPoint = wayPoint;
        reportMovingWayPointLocation(movingWayPoint);
    }

    public void generation(ActionEvent e){

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
                            generateMoveHere() +
                            "\n" +
                            "        }\n" +
                            "    }\n" +
                            "}"
            );
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

    private String generateMoveHere() {
        ArrayList<String> movements = new ArrayList<>();

        if (wayPoints.size() >= 2) {

            double currentAngle = 0;

            for (int i = 1; i < wayPoints.size(); i++) {

                WayPoint lastPoint = wayPoints.get(i - 1);
                WayPoint targetPoint = wayPoints.get(i);

                double targetAngle = getTargetAngle(lastPoint, targetPoint);

                double diffAngle = normalizeAngle(targetAngle - currentAngle);

                currentAngle = targetAngle;

                // Only add if different and not the first point
                if (diffAngle != 0 && i != 1) {
                    movements.add(String.format("\t\t\trotate(%.1f);\n", diffAngle));
                }

                double pathLength = getLineLength(lastPoint, targetPoint);
                double pathLengthInches = convertToInches(pathLength);

                movements.add(String.format("\t\t\tgoForward(%.1f);\n", pathLengthInches));
            }
        }

        return convertArrayList(movements);
    }

    // Helpers

    public static String formatLocation(double x, double y) {
        double xInches = convertToInches(x);
        double yInches = convertToInches(y);
        return String.format("(%.1f, %.1f)", xInches, yInches);
    }

    public static double getTargetAngle(WayPoint wayPoint1, WayPoint wayPoint2) {
        double dx = wayPoint2.xPoint-wayPoint1.xPoint;
        double dy = wayPoint2.yPoint-wayPoint1.yPoint;
        double targetAnglePi = Math.atan2(dy, dx);
        double targetAngle = ((targetAnglePi*180)/Math.PI);
        return normalizeAngle(targetAngle);
    }

    public static double getLineLength(WayPoint wayPoint1, WayPoint wayPoint2) {
        double dx = wayPoint2.xPoint-wayPoint1.xPoint;
        double dy = wayPoint2.yPoint-wayPoint1.yPoint;
        return Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
    }

    private static double convertToInches(double pixelValue) {
        double conversionFactorPixelInch = ((double) FIELD_MEASUREMENT_INCHES / (double) FIELD_MEASUREMENT_PIXELS);
        return pixelValue * conversionFactorPixelInch;
    }

    private static String convertArrayList(ArrayList<String> stringList) {
        return String.join("", stringList);
    }

    private static double normalizeAngle(double angle) {
        return ((angle + 180) % 360) - 180;
    }

}
