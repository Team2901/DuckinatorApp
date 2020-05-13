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

    private final TextArea code;
    private final TextField classNameTextArea;
    private int togglingKeep = 1;

    private final Label wayPointLocationReporter;

    ContextMenu modeContextMenu;

    private final ArrayList<WayPoint> wayPoints = new ArrayList<>();

    private Drawable selectedDrawable;

    private double pressOffsetX = 0;
    private double pressOffsetY = 0;
    private boolean editMode = false;

    public ProjectPane () {
        Rectangle rect1 = new Rectangle(1200, 600, Color.BLANCHEDALMOND);
        getChildren().add(rect1);

        Image field = new Image(this.getClass().getResourceAsStream("/field.png"));
        ImageView fieldHolder = new ImageView(field);
        fieldHolder.setFitHeight(FieldUtils.FIELD_MEASUREMENT_PIXELS);
        fieldHolder.setFitWidth(FieldUtils.FIELD_MEASUREMENT_PIXELS);
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
            double xInches = FieldUtils.convertToInches(event.getSceneX());
            double yInches = FieldUtils.convertToInches(event.getSceneY());
            final String msg = String.format("Mouse: (%.1f, %.1f)", xInches, yInches);
            mouseLocationReporter.setText(msg);
        };

        this.setOnMouseMoved(processMouseMovement);
        this.setOnMouseDragged(processMouseMovement);
    }

    public void reportMovingWayPointLocation() {

        final String msg;
        if (selectedDrawable != null) {
            msg = selectedDrawable.formatLocation();
        } else {
            msg = null;
        }

        wayPointLocationReporter.setText(msg);
    }

    public void processCleanButtonOnPressed(final ActionEvent e) {

        selectedDrawable = null;
        while (wayPoints.size() > 0) {
            deleteWayPoint(wayPoints.get(0));
        }

        code.clear();
        editMode = false;
    }

    public void processModeMenuRequest(final ContextMenuEvent me) {

        if (modeContextMenu != null) {
            modeContextMenu.hide();
        }

        modeContextMenu = new ContextMenu();

        MenuItem editModeItem = new MenuItem("Edit");
        MenuItem placeModeItem = new MenuItem("Append");

        editModeItem.setOnAction(e -> editMode = true);

        placeModeItem.setOnAction(e -> {
            editMode = false;
            setSelectedDrawable(null);
        });

        if (editMode) {
            modeContextMenu.getItems().add(placeModeItem);
        } else {
            modeContextMenu.getItems().add(editModeItem);
        }

        modeContextMenu.show((Node) me.getTarget(), me.getScreenX(), me.getScreenY());
    }

    public void processWayPointOnMouseDragged(final MouseEvent e) {

        if (e.getButton().equals(MouseButton.PRIMARY)) {

            final WayPoint wayPoint = (WayPoint) e.getTarget();

            if (selectedDrawable == wayPoint) {

                double newX = pressOffsetX + e.getSceneX();
                double newY = pressOffsetY + e.getSceneY();

                wayPoint.setCenter(newX, newY);
                reportMovingWayPointLocation();
            }
        }
    }

    public void processWayPointOnMousePressed(final MouseEvent e) {

        if (e.getButton().equals(MouseButton.PRIMARY)) {

            if (!editMode) {
                processFieldHolderOnMousePressed(e);
                return;
            }

            final WayPoint wayPoint = (WayPoint) e.getTarget();

            pressOffsetX = wayPoint.getXPoint() - e.getSceneX();
            pressOffsetY = wayPoint.getYPoint() - e.getSceneY();

            setSelectedDrawable(wayPoint);

            wayPoint.getParent().requestFocus();
        }
    }

    public void processWayLineOnMousePressed(final MouseEvent e) {

        if (e.getButton().equals(MouseButton.PRIMARY)) {

            if (!editMode) {
                processFieldHolderOnMousePressed(e);
                return;
            }

            WayLine wayLine = (WayLine) e.getTarget();
            setSelectedDrawable(wayLine);
            wayLine.getParent().requestFocus();
        }
    }

    public void processFieldHolderOnKeyPressed(final KeyEvent e) {

        if (!editMode) {
            return;
        }

        if (selectedDrawable != null) {
            final WayPoint selectedWayPoint = (selectedDrawable instanceof WayPoint) ? (WayPoint) selectedDrawable : null;

            if (e.getCode().equals(KeyCode.ESCAPE)) {
                setSelectedDrawable(null);
            }

            if (e.getCode().equals(KeyCode.DELETE) && selectedWayPoint != null) {
                deleteWayPoint(selectedWayPoint);
                setSelectedDrawable(null);
                reportMovingWayPointLocation();
            } else if (e.getCode().isArrowKey() && selectedWayPoint != null) {

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

                selectedWayPoint.setCenter(selectedWayPoint.getXPoint() + dx, selectedWayPoint.getYPoint() + dy);
            }

            reportMovingWayPointLocation();
        }
    }

    public void processFieldHolderOnMousePressed(final MouseEvent e){

        if (e.getButton().equals(MouseButton.PRIMARY)) {

            if (!editMode) {

                final WayPoint lastWayPoint = (wayPoints.size() == 0) ? null : wayPoints.get(wayPoints.size() - 1);
                final WayPoint wayPoint = new WayPoint(e.getSceneX(), e.getSceneY());

                addWayPoint(wayPoint, lastWayPoint, null);

            } else if (selectedDrawable != null) {

                if (selectedDrawable instanceof WayLine){

                    final WayPoint lastWayPoint = selectedDrawable.getPriorPoint();
                    final WayPoint nextWayPoint = selectedDrawable.getNextPoint();
                    final WayPoint wayPoint = new WayPoint(e.getSceneX(), e.getSceneY());

                    addWayPoint(wayPoint, lastWayPoint, nextWayPoint);

                    setSelectedDrawable(wayPoint);

                } else {

                    final WayPoint selectedWayPoint = (WayPoint) selectedDrawable;
                    selectedWayPoint.setCenter(e.getSceneX(), e.getSceneY());

                }
            }
        }
    }

    public void addWayPoint(final WayPoint wayPoint, final WayPoint lastWayPoint, final WayPoint nextWayPoint) {

        int addIndex;

        if (lastWayPoint == null) {
            addIndex = 0;
        } else if (nextWayPoint == null) {
            addIndex = wayPoints.size();
        } else {
            addIndex = wayPoints.indexOf(nextWayPoint);
        }


        if (lastWayPoint != null) {
            WayLine wayLine = new WayLine(lastWayPoint, wayPoint);
            wayLine.addToPane(this);
            wayLine.setOnMousePressed(this::processWayLineOnMousePressed);
        }

        if (nextWayPoint != null) {
            wayPoint.setNextDrawable(nextWayPoint.getPriorLine(), true);
        }

        wayPoint.setOnMousePressed(this::processWayPointOnMousePressed);
        wayPoint.setOnMouseDragged(this::processWayPointOnMouseDragged);

        wayPoint.addToPane(this);
        wayPoints.add(addIndex, wayPoint);
    }

    public void deleteWayPoint(final WayPoint wayPoint){

        final WayPoint priorPoint = wayPoint.getPriorPoint();
        final WayPoint nextPoint = wayPoint.getNextPoint();

        final WayLine removeLine;

        if (priorPoint != null) {
            removeLine = wayPoint.getPriorLine();
            priorPoint.setNextDrawable(wayPoint.getNextLine(), true);
        } else if (nextPoint != null) {
            removeLine = nextPoint.getPriorLine();
            nextPoint.setPriorDrawable(null, true);
        } else {
            removeLine = null;
        }

        if (removeLine != null) {
            removeLine.removeFromPane(this);
        }

        wayPoint.removeFromPane(this);
        wayPoints.remove(wayPoint);
    }

    private void setSelectedDrawable(final Drawable drawable) {

        if (selectedDrawable != null) {
            selectedDrawable.setSelected(false);
        }

        if (drawable != null) {
            drawable.setSelected(true);
        }

        selectedDrawable = drawable;
        reportMovingWayPointLocation();
    }

    public void generation(final ActionEvent e){

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

                double targetAngle = FieldUtils.getTargetAngle(lastPoint, targetPoint);

                double diffAngle = FieldUtils.normalizeAngle(targetAngle - currentAngle);

                currentAngle = targetAngle;

                // Only add if different and not the first point
                if (diffAngle != 0 && i != 1) {
                    movements.add(String.format("\t\t\trotate(%.1f);\n", diffAngle));
                }

                double pathLength = FieldUtils.getLineLength(lastPoint, targetPoint);
                double pathLengthInches = FieldUtils.convertToInches(pathLength);

                movements.add(String.format("\t\t\tgoForward(%.1f);\n", pathLengthInches));
            }
        }

        return String.join("", movements);
    }
}
