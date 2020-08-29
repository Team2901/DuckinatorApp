package main.java;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * @author akkir
 */

public class ProjectPane extends Pane {

    public enum DriveBase {
        TANK_DRIVE,
        X_DRIVE,
        MECHANUM
    }

    private static final String DEFAULT_ROOT_NAME = "DuckinatorAuto";
    private final ImageView fieldHolder;
    private final Pane fieldPane;
    public static final double FIELD_MEASUREMENT_PIXELS = 720;
    public static final double FIELD_MEASUREMENT_INCHES = 144;

    public static final double PANE_PADDING_PIXELS = 5;
    public static final double PANE_WIDTH_PIXELS = 1200;
    public static final double PANE_HEIGHT_PIXELS = FIELD_MEASUREMENT_PIXELS + 2*PANE_PADDING_PIXELS;
    private final Label mouseLocation;
    final ArrayList<WayPoint> points = new ArrayList<>();

    private final ListView<Label> pointsListView;
    private WayPoint selectedPoint;
    private LineConnector selectedLine;
    private List<List<Point>> pointHistory = new ArrayList<>();
    private Integer currentIndex = null;
    private String rootName;

    private final VBox updateOptionsLayout;
    private final TextField updateDrawableNameField;
    private final TextField updateDrawableXField;
    private final TextField updateDrawableYField;
    private final TextField updateDrawableZAngleField;

    DriveBase selectedDriveBase = DriveBase.TANK_DRIVE;

    public ProjectPane() {

        this.setOnKeyPressed(this::gameAreaKeyPress);
        Rectangle rect = new Rectangle(PANE_WIDTH_PIXELS, PANE_HEIGHT_PIXELS, Color.BLANCHEDALMOND);
        getChildren().add(rect);

        fieldPane = new Pane();
        fieldPane.setLayoutX(PANE_PADDING_PIXELS);
        fieldPane.setLayoutY(PANE_PADDING_PIXELS);

        Image field = new Image(this.getClass().getResourceAsStream("/main/resources/Field Images/SkyStone.png"));
        fieldHolder = new ImageView(field);
        fieldHolder.setFitHeight(FIELD_MEASUREMENT_PIXELS);
        fieldHolder.setFitWidth(FIELD_MEASUREMENT_PIXELS);
        fieldPane.getChildren().add(fieldHolder);

        getChildren().add(fieldPane);

        updateOptionsLayout = new VBox();
        updateOptionsLayout.setLayoutX(FIELD_MEASUREMENT_PIXELS + 10);
        updateOptionsLayout.setLayoutY(PANE_PADDING_PIXELS);
        updateOptionsLayout.setSpacing(8);

        updateDrawableNameField = new TextField();

        updateDrawableXField = new TextField();
        setupTextOptionListeners(updateDrawableXField, 0, FIELD_MEASUREMENT_INCHES);

        updateDrawableYField = new TextField();
        setupTextOptionListeners(updateDrawableYField, 0, FIELD_MEASUREMENT_INCHES);

        updateDrawableZAngleField = new TextField();
        setupTextOptionListeners(updateDrawableZAngleField, -180, 360);

        Button updateDrawableButton = new Button("Update WayPoint");
        updateDrawableButton.setOnMouseClicked(e -> onUpdateOptionsClicked());
        updateOptionsLayout.getChildren().add(updateDrawableNameField);
        updateOptionsLayout.getChildren().add(updateDrawableXField);
        updateOptionsLayout.getChildren().add(updateDrawableYField);
        updateOptionsLayout.getChildren().add(updateDrawableZAngleField);
        updateOptionsLayout.getChildren().add(updateDrawableButton);

        pointsListView = new ListView<>();
        pointsListView.setPrefWidth(200);
        pointsListView.setPrefHeight(PANE_HEIGHT_PIXELS - 2*PANE_PADDING_PIXELS);
        pointsListView.setLayoutX(PANE_WIDTH_PIXELS - PANE_PADDING_PIXELS - 200);
        pointsListView.setLayoutY(PANE_PADDING_PIXELS);
        getChildren().add(pointsListView);

        pointsListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            final int newIndex = pointsListView.getItems().indexOf(newValue);
            final WayPoint wayPoint = newIndex < 0 ? null : points.get(newIndex);
            selectPoint(wayPoint);
        });

        fieldHolder.setOnMouseClicked(this::processMousePress);
        this.setOnMouseMoved(this::mouseLocationUpdate);
        this.setOnMouseDragged(this::mouseLocationUpdate);
        this.setOnKeyPressed(this::gameAreaKeyPress);

        mouseLocation = new Label();
        mouseLocation.setLayoutX(FIELD_MEASUREMENT_PIXELS + PANE_PADDING_PIXELS);
        mouseLocation.setLayoutY(PANE_HEIGHT_PIXELS/2);

        fieldPane.getChildren().add(mouseLocation);
    }

    private void setupTextOptionListeners(TextField textField, double min, double max) {

        textField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.matches("[0-9.-]*")) {
                textField.setText(newValue.replaceAll("[^0-9.-]", ""));
            }
        });

        textField.focusedProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue) { // focus lost
                final String text = formatTextValue(textField.getText(), min, max);
                textField.setText(text);
            }
        });
    }

    public String formatTextValue(String inputString, double min, double max) {

        String[] split = inputString.split("\\.");

        String value = split[0] + "." + (split.length > 1 ? split[1] : "00");
        try {
            double parsedInput = Double.parseDouble(value);
            double boundInput = Math.min(Math.max(parsedInput, min), max);
            return String.format("%.2f", boundInput);
        } catch (Exception e) {
            return value;
        }
    }

    public DriveBase getSelectedDriveBase() {
        return selectedDriveBase;
    }

    public void setSelectedDriveBase(DriveBase driveBase) {
        this.selectedDriveBase = driveBase;
    }

    public void onUpdateOptionsClicked() {

        String name = updateDrawableNameField.getText().trim();

        double xInches, yInches, zAngle;

        if (!updateDrawableXField.getText().isBlank()) {
            xInches = Double.parseDouble(updateDrawableXField.getText());
        } else {
            xInches = selectedPoint.getXInches();
        }

        if (!updateDrawableYField.getText().isBlank()) {
            yInches = Double.parseDouble(updateDrawableYField.getText());
        } else {
            yInches = selectedPoint.getYInches();
        }

        if (!updateDrawableZAngleField.getText().isBlank()) {
            zAngle = Double.parseDouble(updateDrawableZAngleField.getText());
        } else {
            zAngle = selectedPoint.getZAngle();
        }

        selectedPoint.setName(name);
        selectedPoint.setCenterInches(xInches, yInches);
        selectedPoint.setZAngle(zAngle);
        addToPointHistory();
        setSelectedPointOptions();
    }

    public void setSelectedPointOptions() {

        int index = getChildren().indexOf(updateOptionsLayout);

        if (selectedPoint == null) {
            if (index >=0) {
                getChildren().remove(updateOptionsLayout);
            }
        } else {
            if (index < 0) {
                getChildren().add(updateOptionsLayout);
            }

            updateDrawableXField.setText(String.valueOf(selectedPoint.getXInches()));
            updateDrawableYField.setText(String.valueOf(selectedPoint.getYInches()));
            updateDrawableZAngleField.setText(String.valueOf(selectedPoint.getZAngle()));
            updateDrawableNameField.setText(selectedPoint.getName());
        }
    }

    public void mouseLocationUpdate(MouseEvent event) {

        double xPixels = event.getX() - fieldPane.getLayoutX();
        double yPixels = event.getY() - fieldPane.getLayoutY();

        double xInches = xPixels / FIELD_MEASUREMENT_PIXELS * FIELD_MEASUREMENT_INCHES;
        double yInches = (FIELD_MEASUREMENT_PIXELS - yPixels) / FIELD_MEASUREMENT_PIXELS * FIELD_MEASUREMENT_INCHES;

        String mouseString = String.format("%.2f,%.2f", xInches, yInches);
        mouseLocation.setText(mouseString);
    }

    public void processMousePress(MouseEvent e) {

        double xPixels = e.getX();
        double yPixels = e.getY();

        double xInches = xPixels / FIELD_MEASUREMENT_PIXELS * FIELD_MEASUREMENT_INCHES;
        double yInches = (FIELD_MEASUREMENT_PIXELS - yPixels) / FIELD_MEASUREMENT_PIXELS * FIELD_MEASUREMENT_INCHES;

        createWayPoint("WayPoint",xInches, yInches);
        addToPointHistory();
    }

    public void addToPointHistory() {
        List<Point> newValue = new ArrayList<>();
        for (WayPoint wayPoint : points) {
            Point point = new Point(wayPoint);
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

    public void createWayPoint(String name, double xPoint, double yPoint) {

        WayPoint newWayPoint = new WayPoint(name, xPoint, yPoint);
        newWayPoint.setOnMousePressed(this::selectPointPress);
        newWayPoint.setOnMouseDragged(this::dragPoint);
        newWayPoint.setOnMouseReleased(this::releasePoint);

        final LineConnector line;
        final Integer index;

        if (points.isEmpty()) {
            line = null;
            index = null;
        } else {

            WayPoint lastWayPoint;
            WayPoint nextWayPoint;
            if (selectedLine != null) {
                lastWayPoint = (WayPoint) selectedLine.getBefore();
                nextWayPoint = (WayPoint) selectedLine.getAfter();
                index = points.indexOf(nextWayPoint);
                selectedLine.setStartPoint(newWayPoint);
                newWayPoint.setAfter(selectedLine);
            } else {
                lastWayPoint = points.get(points.size() - 1);
                index = points.size();
            }
            line = new LineConnector(lastWayPoint.getCenterX(),  lastWayPoint.getCenterY(), newWayPoint.getCenterX(), newWayPoint.getCenterY());
            line.setOnMousePressed(this::selectLinePress);
            addDrawable(line, lastWayPoint);
        }

        addDrawable(newWayPoint, line);

        if (index != null) {
            points.add(index, newWayPoint);
            pointsListView.getItems().add(index, newWayPoint.getLabel());
        } else {
            points.add(newWayPoint);
            pointsListView.getItems().add(newWayPoint.getLabel());
        }

        selectPoint(newWayPoint);
    }

    private void dragPoint(MouseEvent mouseEvent) {
        WayPoint wayPoint = (WayPoint) mouseEvent.getTarget();

        double xPixels = mouseEvent.getX();
        double yPixels = mouseEvent.getY();

        wayPoint.setCenterPixels(xPixels, yPixels);
        setSelectedPointOptions();

    }

    private void releasePoint(MouseEvent mouseEvent) {
        addToPointHistory();
    }

    private void selectPointPress(MouseEvent mouseEvent) {
        selectPoint((WayPoint) mouseEvent.getTarget());
    }

    private void selectLinePress(MouseEvent mouseEvent) {
        selectLine((LineConnector) mouseEvent.getTarget());
    }

    private void selectPoint(WayPoint point) {

        if (Objects.equals(point, selectedPoint)) {
            return;
        }

        selectDrawable(point);
    }

    private void selectLine(LineConnector line) {


        if (Objects.equals(line, selectedLine)) {
            return;
        }

        selectDrawable(line);
    }

    private void selectDrawable(Drawable drawable) {

        if (selectedPoint != null) {
            selectedPoint.setSelected(false);
            selectedPoint = null;
        }

        if (selectedLine != null) {
            selectedLine.setSelected(false);
            selectedLine = null;
        }

        if (drawable != null) {
            if (drawable instanceof WayPoint) {
                selectedPoint = (WayPoint) drawable;
                selectedPoint.setSelected(true);

            } else {
                selectedLine = (LineConnector) drawable;
                selectedLine.setSelected(true);
            }
        }

        if (selectedPoint != null) {
            final Label label = selectedPoint.getLabel();
            pointsListView.scrollTo(label);
            pointsListView.getSelectionModel().select(label);
        } else {
            pointsListView.getSelectionModel().select(null);
        }

        setSelectedPointOptions();
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
        if (event.getCode() == KeyCode.ENTER) {
            this.onUpdateOptionsClicked();
        }
        if (event.getCode() == KeyCode.Z && event.isControlDown()) {
            if (event.isShiftDown()) {
                redo();
            } else {
                undo();
            }
        }
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
            fieldPane.getChildren().add(point.subCircle);
            fieldPane.getChildren().add(point);
        } else if (drawable instanceof LineConnector) {
            LineConnector line = (LineConnector) drawable;
            int index = fieldPane.getChildren().indexOf(lastDrawable);
            fieldPane.getChildren().add(index, line.subLine);
            fieldPane.getChildren().add(index, line);
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

        selectPoint(null);

        points.remove(point);
        pointsListView.getItems().remove(point.getLabel());
    }

    public void removeDrawable(Drawable remove) {
        fieldPane.getChildren().remove(remove);

        if (remove instanceof WayPoint) {
            WayPoint point = (WayPoint) remove;
            fieldPane.getChildren().remove(point.subCircle);
        } else if (remove instanceof LineConnector) {
            LineConnector line = (LineConnector) remove;
            fieldPane.getChildren().remove(line.subLine);
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
            String pointString = String.format("%s,%f,%f\n", point.getName(), point.getXInches(), point.getYInches());
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
            createWayPoint(lineArray[0], Double.parseDouble(lineArray[1]), Double.parseDouble(lineArray[2]));
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
            createWayPoint(point.getName(), point.getX(), point.getY());
        }
    }

    public void generateCode(File codeFile) {
        try {

            FileWriter fileWriter = new FileWriter(codeFile);
            String javaClassName = codeFile.getName().split("\\.")[0];

            setRootName(javaClassName);

            ArrayList<String> movements = new ArrayList<>();

            double initAngle = 0;

            for (int ii = 0; ii < points.size(); ii++) {

                // first point.. nothing to do. assume robot was placed here.
                if (ii == 0) {
                    continue;
                }

                WayPoint currentPoint = points.get(ii);
                WayPoint lastPoint = points.get(ii -1);

                double dx = lastPoint.getXInches() - currentPoint.getXInches();
                double dy = lastPoint.getYInches() - currentPoint.getYInches();

                double distanceInInches = Math.sqrt(Math.pow(dx, 2) + Math.pow(dy, 2));
                double targetAngle = Math.atan2(dy, dx) * 180 / Math.PI;

                if (ii == 1) {
                    initAngle = targetAngle;
                } else {
                    movements.add("            rotateToAngle(" + targetAngle + ");\n");
                }
                movements.add("            goStraightInches(" + distanceInInches + ");\n");
            }

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
                    "            final double initAngle = "+initAngle+";\n" +
                    "\n" +
                    "            setRobotAngleOffset(getRobotAngle() - initAngle);\n" +
                    "\n" +
                    String.join("", movements) +
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
