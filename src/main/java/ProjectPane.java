package main.java;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.awt.*;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

import static java.lang.Integer.parseInt;

/**
 * @author akkir
 */

public class ProjectPane extends Pane {

    private enum DriveBase {
        TANK_DRIVE,
        X_DRIVE,
        MECHANUM
    }

    private static final String DEFAULT_ROOT_NAME = "DuckinatorAuto";
    private final ImageView fieldHolder;
    public static final double FIELD_MEASUREMENT_PIXELS = 510;
    public static final double FIELD_MEASUREMENT_INCHES = 144;

    private final Label mouseLocation;
    private final Label selectedDrawableLocation;
    final ArrayList<WayPoint> points = new ArrayList<>();

    private WayPoint selectedPoint;
    private LineConnector selectedLine;
    private List<List<Point>> pointHistory = new ArrayList<>();
    private Integer currentIndex = null;
    private String rootName;

    DriveBase selectedDriveBase = DriveBase.TANK_DRIVE;

    public ProjectPane() {

        this.setOnKeyPressed(this::gameAreaKeyPress);
        Rectangle rect = new Rectangle(1200, 600, Color.BLANCHEDALMOND);
        getChildren().add(rect);

        Image field = new Image(this.getClass().getResourceAsStream("/main/resources/field.png"));
        fieldHolder = new ImageView(field);
        fieldHolder.setFitHeight(FIELD_MEASUREMENT_PIXELS);
        fieldHolder.setFitWidth(FIELD_MEASUREMENT_PIXELS);
        fieldHolder.setLayoutX(0);
        fieldHolder.setLayoutY(0);
        getChildren().add(fieldHolder);

        Image duck = new Image(this.getClass().getResourceAsStream("/main/resources/duck.png"));
        ImageView duckHolder = new ImageView(duck);
        duckHolder.setFitHeight(150);
        duckHolder.setFitWidth(163);
        duckHolder.setLayoutX(870);
        duckHolder.setLayoutY(350);
        getChildren().add(duckHolder);

        ToggleGroup drives = new ToggleGroup();

        RadioButton tankDrive = new RadioButton("Tank Drive");
        tankDrive.setLayoutX(545);
        tankDrive.setLayoutY(270);
        tankDrive.setToggleGroup(drives);
        tankDrive.setSelected(true);
        getChildren().add(tankDrive);

        RadioButton holonomicDrive = new RadioButton("X-Drive");
        holonomicDrive.setLayoutX(670);
        holonomicDrive.setLayoutY(270);
        holonomicDrive.setToggleGroup(drives);
        getChildren().add(holonomicDrive);

        RadioButton mecanumDrive = new RadioButton("Mecanum");
        mecanumDrive.setLayoutX(795);
        mecanumDrive.setLayoutY(270);
        mecanumDrive.setToggleGroup(drives);
        getChildren().add(mecanumDrive);

        tankDrive.setOnAction(e -> selectedDriveBase = DriveBase.TANK_DRIVE);
        holonomicDrive.setOnAction(e -> selectedDriveBase = DriveBase.X_DRIVE);
        mecanumDrive.setOnAction(e -> selectedDriveBase = DriveBase.MECHANUM);
        fieldHolder.setOnMouseClicked(this::processMousePress);
        this.setOnKeyPressed(this::gameAreaKeyPress);

        mouseLocation = new Label();
        selectedDrawableLocation = new Label();
        mouseLocation.setLayoutX(0);
        mouseLocation.setLayoutY(0);
        selectedDrawableLocation.setLayoutX(0);
        selectedDrawableLocation.setLayoutY(20);
        getChildren().add(mouseLocation);
        getChildren().add(selectedDrawableLocation);
        this.setOnMouseMoved(this::mouseLocationUpdate);
        this.setOnMouseDragged(this::mouseLocationUpdate);
    }

    public void mouseLocationUpdate(MouseEvent event) {
        double mouseX = (event.getSceneX() - this.getLayoutX()) * FIELD_MEASUREMENT_INCHES / FIELD_MEASUREMENT_PIXELS;
        double mouseY = (FIELD_MEASUREMENT_PIXELS - (event.getSceneY() - this.getLayoutY())) * FIELD_MEASUREMENT_INCHES / FIELD_MEASUREMENT_PIXELS;
        String mouseString = String.format("%.2f,%.2f", mouseX, mouseY);
        mouseLocation.setText(mouseString);
    }

    public void selectedWayPointLocationUpdate() {
        if (selectedPoint != null) {
            double wayPointX = (selectedPoint.getX()) * FIELD_MEASUREMENT_INCHES / FIELD_MEASUREMENT_PIXELS;
            double wayPointY = (selectedPoint.getY()) * FIELD_MEASUREMENT_INCHES / FIELD_MEASUREMENT_PIXELS;
            String wayPointString = String.format("%.2f,%.2f", wayPointX, wayPointY);
            selectedDrawableLocation.setText(wayPointString);
        } else {
            selectedDrawableLocation.setText("");
        }
    }

    public void processMousePress(MouseEvent e) {
        if (e.getSource() == fieldHolder) {
            int xPoint = (int) e.getSceneX() - (int) this.getLayoutX();
            int yPoint = (int) e.getSceneY() - (int) this.getLayoutY();
            createWayPoint(xPoint, yPoint);
            addToPointHistory();
        }
    }

    public void addToPointHistory() {
        List<Point> newValue = new ArrayList<>();
        for (WayPoint wayPoint : points) {
            Point point = new Point(wayPoint.getX(), wayPoint.getY());
            newValue.add(point);
        }

        List<Point> currentValue = pointHistory.isEmpty() ? null : pointHistory.get(currentIndex);
        if (!newValue.equals(currentValue)) {
            if (currentIndex != null) {
                pointHistory = pointHistory.subList(0, currentIndex + 1);
            }
            pointHistory.add(newValue);
            currentIndex = pointHistory.size() - 1;
        }
    }

    public void undo() {
        /*
         * Moves the current value to the previous value in the history
         */
        if (currentIndex != 0) {
            currentIndex--;
        }
        List<Point> currentValue = pointHistory.isEmpty() ? null : pointHistory.get(currentIndex);

        if (currentValue != null) {
            loadPoints(currentValue);
        }
    }

    public void redo() {
        /*
         * Moves the current value to the next value in the history
         */
        if (currentIndex != pointHistory.size() - 1) {
            currentIndex++;
        }

        List<Point> currentValue = pointHistory.isEmpty() ? null : pointHistory.get(currentIndex);

        if (currentValue != null) {
            loadPoints(currentValue);
        }
    }

    public void createWayPoint(int xPoint, int yPoint) {
        if (points.isEmpty()) {
            WayPoint startCircle = new WayPoint(xPoint, yPoint);
            startCircle.setOnMousePressed(this::selectPointPress);
            startCircle.setOnMouseDragged(this::dragPoint);
            startCircle.setOnMouseReleased(this::releasePoint);
            addDrawable(startCircle, null);
            points.add(startCircle);
        } else {
            WayPoint nextCircles = new WayPoint(xPoint, yPoint);
            nextCircles.setOnMousePressed(this::selectPointPress);
            nextCircles.setOnMouseDragged(this::dragPoint);
            nextCircles.setOnMouseReleased(this::releasePoint);
            WayPoint lastWayPoint;
            WayPoint nextWayPoint;
            int index;
            if (selectedLine != null) {
                lastWayPoint = (WayPoint) selectedLine.getBefore();
                nextWayPoint = (WayPoint) selectedLine.getAfter();
                index = points.indexOf(nextWayPoint);
                selectedLine.setStartPoint(nextCircles);
                nextCircles.setAfter(selectedLine);
            } else {
                lastWayPoint = points.get(points.size() - 1);
                index = points.size();
            }
            LineConnector line = new LineConnector((int) lastWayPoint.getCenterX(), (int) lastWayPoint.getCenterY(), xPoint, yPoint);
            line.setOnMousePressed(this::selectLinePress);
            addDrawable(line, lastWayPoint);
            addDrawable(nextCircles, line);
            points.add(index, nextCircles);
        }
        selectLine(null);
    }

    private void dragPoint(MouseEvent mouseEvent) {
        WayPoint circle = (WayPoint) mouseEvent.getTarget();
        double mouseX = mouseEvent.getSceneX() - this.getLayoutX();
        double mouseY = mouseEvent.getSceneY() - this.getLayoutY();
        if (mouseX < 0) {
            mouseX = 0;
        }
        if (mouseY < 0) {
            mouseY = 0;
        }
        if (mouseX > FIELD_MEASUREMENT_PIXELS) {
            mouseX = FIELD_MEASUREMENT_PIXELS;
        }
        if (mouseY > FIELD_MEASUREMENT_PIXELS) {
            mouseY = FIELD_MEASUREMENT_PIXELS;
        }
        circle.setCirclePositionSet(mouseX, mouseY);
        selectedWayPointLocationUpdate();
    }

    private void releasePoint(MouseEvent mouseEvent) {
        addToPointHistory();
    }

    private void selectPointPress(MouseEvent mouseEvent) {
        selectPoint((WayPoint) mouseEvent.getTarget());
    }

    private void selectPoint(WayPoint point) {
        if (selectedPoint != null) {
            selectedPoint.setSelected(false);
        }
        if (selectedLine != null) {
            selectedLine.setSelected(false);
            selectedLine = null;
        }
        if (point != null) {
            point.setSelected(true);
        }
        selectedPoint = point;
        selectedWayPointLocationUpdate();
    }

    private void selectLinePress(MouseEvent mouseEvent) {
        selectLine((LineConnector) mouseEvent.getTarget());
    }

    private void selectLine(LineConnector line) {
        if (selectedPoint != null) {
            selectedPoint.setSelected(false);
            selectedPoint = null;
        }
        if (selectedLine != null) {
            selectedLine.setSelected(false);
        }
        if (line != null) {
            line.setSelected(true);
        }
        selectedLine = line;
    }

    public void processClearPress() {
        clear();
        addToPointHistory();
    }

    public void clear() {

        while (points.size() != 0) {
            deleteWayPoint(points.get(0));
        }
    }

    public void gameAreaKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE || event.getCode() == KeyCode.BACK_SPACE) {
            if (selectedPoint != null) {
                deleteWayPoint(selectedPoint);
                addToPointHistory();
            }
        }
        if (event.getCode() == KeyCode.ESCAPE) {
            selectPoint(null);
            selectLine(null);
        }
        if (event.getCode() == KeyCode.Z && event.isControlDown()) {
            if (event.isShiftDown()) {
                redo();
            } else {
                undo();
            }
        }
    }

    private String convertArrayList(ArrayList<String> stringList) {
        return String.join("", stringList);
    }

    private String moveHereTwo(ArrayList<WayPoint> points) {
        ArrayList<String> movements = new ArrayList<>();

        for (int ii = 0; ii < points.size(); ii++) {
            // first point.. nothing to do. assume robot was placed here.
            if (ii == 0) continue;
            // 2 or more points present.
            // calculate the distance to travel between last point and this point
            double inches = distanceBetweenTwoPointsInInches(points.get(ii), points.get(ii - 1));

            // Three or more points present
            // calculate change in direction.
            if (ii > 1) {
                double angleChange = changeInOrientation(points.get(ii - 2), points.get(ii - 1), points.get(ii));
                movements.add("\t\t\trotateByAngle(" + (int) Math.round(angleChange) + ");\n");
            }
            movements.add("\t\t\tgoStraightInches(" + inches + ");\n");
        }

        return convertArrayList(movements);
    }

    private Double distanceBetweenTwoPointsInInches(WayPoint firstPoint, WayPoint secondPoint) {
        int dx = secondPoint.getX() - firstPoint.getX();
        int dy = secondPoint.getY() - firstPoint.getY();

        double dxSquared = Math.pow(dx, 2);
        double dySquared = Math.pow(dy, 2);

        double distanceInPixels = Math.sqrt(dxSquared + dySquared);

        return  distanceInPixels * FIELD_MEASUREMENT_INCHES / FIELD_MEASUREMENT_PIXELS;
    }

    private double changeInOrientation(WayPoint firstPoint, WayPoint secondPoint, WayPoint thirdPoint) {
        double dx1 = secondPoint.getX() - firstPoint.getX();
        double dx2 = thirdPoint.getX() - secondPoint.getX();
        double dy1 = secondPoint.getY() - firstPoint.getY();
        double dy2 = thirdPoint.getY() - secondPoint.getY();
        double length1 = Math.sqrt(Math.pow(dx1, 2) + Math.pow(dy1, 2));
        double length2 = Math.sqrt(Math.pow(dx2, 2) + Math.pow(dy2, 2));
        double angleTemp = Math.acos(((dx1 * dx2) + (dy1 * dy2)) / (length1 * length2));

        if (((secondPoint.getX() - firstPoint.getX()) * (thirdPoint.getY() - firstPoint.getY()) - (secondPoint.getY() - firstPoint.getY()) * (thirdPoint.getX() - firstPoint.getX())) > 0) {
            angleTemp *= -1;
        }

        return ((angleTemp * 180) / Math.PI);
    }

    public void addDrawable(Drawable drawable, Drawable lastDrawable) {
        drawable.setBefore(lastDrawable);
        if (lastDrawable != null) {
            lastDrawable.setAfter(drawable);
        }
        if (drawable instanceof WayPoint) {
            WayPoint point = (WayPoint) drawable;
            if (lastDrawable == null) {
                point.setFirstPoint(true);
            }
            getChildren().add(point.subCircle);
            getChildren().add((Node) drawable);
        } else if (drawable instanceof LineConnector) {
            LineConnector line = (LineConnector) drawable;
            int index = this.getChildren().indexOf(lastDrawable);
            getChildren().add(index, line.subLine);
            getChildren().add(index, (Node) drawable);
        }
    }

    public void deleteWayPoint(WayPoint point) {
        Drawable lineBefore = point.getBefore();
        LineConnector lineAfter = (LineConnector) point.getAfter();

        WayPoint pointBefore = point.getPriorPoint();

        if (point.isFirstPoint()) {
            WayPoint pointAfter = point.getNextPoint();
            if (pointAfter != null) {
                pointAfter.setFirstPoint(true);
            }
        }

        if (lineBefore != null) {
            removeDrawable(lineBefore);
        }

        if (lineAfter != null) {
            if (pointBefore != null) {
                lineAfter.setStartPoint(pointBefore);
            } else {
                removeDrawable(lineAfter);
            }
        }

        removeDrawable(point);
        points.remove(point);
        selectPoint(null);
    }

    public void removeDrawable(Drawable remove) {
        getChildren().remove(remove);
        if (remove instanceof WayPoint) {
            WayPoint point = (WayPoint) remove;
            getChildren().remove(point.subCircle);
        } else if (remove instanceof LineConnector) {
            LineConnector line = (LineConnector) remove;
            getChildren().remove(line.subLine);
        }

        Drawable before = remove.getBefore();
        Drawable after = remove.getAfter();

        if (before != null) {
            before.setAfter(remove.getAfter());
        }

        if (after != null) {
            after.setBefore(remove.getBefore());
        }
    }

    public void savePoints(File file) {
        try {
            if (file == null) {
                return;
            }
            savePoints(file, points);
            String fileName = file.getName();
            String[] fileNameArray = fileName.split("\\.");
            setRootName(fileNameArray[0]);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void savePoints(File filePath, ArrayList<WayPoint> pointsInGivenPathway) throws IOException {
        FileWriter fileWriter = new FileWriter(filePath);
        for (WayPoint point : pointsInGivenPathway) {
            String pointString = String.format("%s,%s\n", point.getX(), point.getY());
            fileWriter.write(pointString);
        }
        fileWriter.close();
    }

    public void loadPoints(File filePath) throws IOException {
        clear();
        FileReader fileReader = new FileReader(filePath);
        BufferedReader bufferedReader = new BufferedReader(fileReader);
        String line = bufferedReader.readLine();
        while (line != null) {
            String[] lineArray = line.split(",");
            createWayPoint(parseInt(lineArray[0]), parseInt(lineArray[1]));
            line = bufferedReader.readLine();
        }
        addToPointHistory();

        String fileName = filePath.getName();
        String[] fileNameArray = fileName.split("\\.");
        setRootName(fileNameArray[0]);
    }

    public void loadPoints(List<Point> points) {
        clear();
        for (Point point : points) {
            createWayPoint((int) point.getX(), (int) point.getY());
        }
    }

    public void generateCode(File codeFile) {
        try {

            FileWriter fileWriter = new FileWriter(codeFile);
            String javaClassName = codeFile.getName().split("\\.")[0];

            setRootName(javaClassName);

            String code = "package org.firstinspires.ftc.teamcode.PurplePathFinder;\n" +
                    "\n" +
                    "import com.qualcomm.robotcore.eventloop.opmode.Autonomous;\n" +
                    "\n" +
                    "@Autonomous(name = \"" + javaClassName + "\", group = \"PurplePathFinder\")\n" +
                    "public class " + javaClassName + " extends " + getBaseDriveClassName() + " {\n" +
                    "    @Override\n" +
                    "    public void runOpMode() throws InterruptedException {\n" +
                    "        initRobot();\n" +
                    "        waitForStart();\n" +
                    "        if (opModeIsActive()) {\n" +
                    "\n" +
                    // TODO init angle
                    "            final double initAngle = 0;\n" +
                    "\n" +
                    "            setRobotAngleOffset(getRobotAngle() - initAngle);\n" +
                    "\n" +
                    moveHereTwo(points) +
                    "        }\n" +
                    "    }\n" +
                    "}";

            fileWriter.write(code);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getRootName() {
        return rootName;
    }

    public void setRootName(String rootName) {
        this.rootName = (rootName == null) ? DEFAULT_ROOT_NAME : rootName;
    }

    private String getBaseDriveClassName() {
        switch (selectedDriveBase) {
            case MECHANUM:
                return "BasePurplePathFinderMechanum";
            case X_DRIVE:
                return "BasePurplePathFinderXDrive";
            case TANK_DRIVE:
            default:
                return "BasePurplePathFinderTankDrive";
        }
    }
}
