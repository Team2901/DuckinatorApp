/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.event.EventHandler;
import javafx.scene.control.Label;
import javafx.scene.control.RadioButton;
import javafx.scene.control.ToggleGroup;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Rectangle;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

/**
 * @author akkir
 */

public class ProjectPane extends Pane {

    public static class WayPointHistory {
        private final double x;
        private final double y;
        private final boolean wayPointSelected;
        private final boolean wayLineSelected;

        public WayPointHistory(WayPoint wayPoint) {
            // this(wayPoint.getXPoint(), wayPoint.getYPoint(), wayPoint.isSelected(), wayPoint.getPriorLine() != null && wayPoint.getPriorLine().isSelected());
            this(wayPoint.getXPoint(), wayPoint.getYPoint(), false, false);
        }

        public WayPointHistory(double x, double y, boolean wayPointSelected, boolean wayLineSelected) {
            this.x = x;
            this.y = y;
            this.wayPointSelected = wayPointSelected;
            this.wayLineSelected = wayLineSelected;
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;

            WayPointHistory that = (WayPointHistory) o;

            if (Double.compare(that.x, x) != 0) return false;
            if (Double.compare(that.y, y) != 0) return false;
            if (wayPointSelected != that.wayPointSelected) return false;
            return wayLineSelected == that.wayLineSelected;
        }

        @Override
        public int hashCode() {
            int result;
            long temp;
            temp = Double.doubleToLongBits(x);
            result = (int) (temp ^ (temp >>> 32));
            temp = Double.doubleToLongBits(y);
            result = 31 * result + (int) (temp ^ (temp >>> 32));
            result = 31 * result + (wayPointSelected ? 1 : 0);
            result = 31 * result + (wayLineSelected ? 1 : 0);
            return result;
        }
    }
    private final static String DEFAULT_CLASS_NAME = "DuckinatorAuto";

    private int togglingKeep = 1;

    private final Label wayPointLocationReporter;

    private final List<WayPoint> wayPoints = new ArrayList<>();

    private Drawable selectedDrawable;

    private double pressOffsetX = 0;
    private double pressOffsetY = 0;

    private List<List<WayPointHistory>> wayPointLocationHistory = new ArrayList<>();
    private int currentIndex = -1;
    private String className = null;

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

        ToggleGroup drives = new ToggleGroup();

        RadioButton tankDrive = new RadioButton("Tank Drive");
        tankDrive.setLayoutX(545);
        tankDrive.setLayoutY(10);
        tankDrive.setOnAction((e) -> togglingKeep = 1);
        tankDrive.setToggleGroup(drives);
        tankDrive.setSelected(true);
        getChildren().add(tankDrive);

        RadioButton holonomicDrive = new RadioButton("X-Drive");
        holonomicDrive.setLayoutX(670);
        holonomicDrive.setLayoutY(10);
        holonomicDrive.setOnAction((e) -> togglingKeep = 2);
        holonomicDrive.setToggleGroup(drives);
        getChildren().add(holonomicDrive);

        RadioButton mecanumDrive = new RadioButton("Mecanum");
        mecanumDrive.setLayoutX(795);
        mecanumDrive.setLayoutY(10);
        mecanumDrive.setOnAction((e) -> togglingKeep = 3);
        mecanumDrive.setToggleGroup(drives);
        getChildren().add(mecanumDrive);

        fieldHolder.setOnMouseClicked(this::processFieldHolderOnMousePressed);

        this.setOnKeyPressed(this::processFieldHolderOnKeyPressed);
        this.setOnKeyReleased(this::processFieldHolderOnKeyReleased);

        final Label mouseLocationReporter = new Label();
        mouseLocationReporter.setLayoutX(0);
        mouseLocationReporter.setLayoutY(0);
        getChildren().add(mouseLocationReporter);

        wayPointLocationReporter = new Label();
        wayPointLocationReporter.setLayoutX(0);
        wayPointLocationReporter.setLayoutY(20);
        getChildren().add(wayPointLocationReporter);

        EventHandler<MouseEvent> processMouseMovement = event -> {

            List<Double> mouseLocation = getMouseLocation(event);
            double xInches = FieldUtils.convertToInches(mouseLocation.get(0));
            double yInches = FieldUtils.convertToInches(mouseLocation.get(1));
            final String msg = String.format("Mouse: (%.1f, %.1f)", xInches, yInches);
            mouseLocationReporter.setText(msg);
        };

        this.setOnMouseMoved(processMouseMovement);
        this.setOnMouseDragged(processMouseMovement);

        addHistory();
    }

    public List<Double> getMouseLocation(MouseEvent event) {

        double offsetX = this.getLayoutX();
        double offsetY = this.getLayoutY();

        Double mouseX = event.getSceneX() - offsetX;
        Double mouseY = event.getSceneY() - offsetY;

        List<Double> mouseLocation = new ArrayList<>();
        mouseLocation.add(mouseX);
        mouseLocation.add(mouseY);
        return mouseLocation;
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

    public void processCleanButtonOnPressed() {
        setClassName(null);
        clear();
        addHistory();
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

    public void processOnMouseReleased(final MouseEvent e) {
        if (e.getButton() == MouseButton.PRIMARY) {
            Drawable target = (Drawable) e.getTarget();
            if (target == selectedDrawable) {
                this.addHistory();
            }
        }
    }

    public void processWayPointOnMousePressed(final MouseEvent e) {

        if (e.getButton().equals(MouseButton.PRIMARY)) {

            final WayPoint wayPoint = (WayPoint) e.getTarget();

            pressOffsetX = wayPoint.getXPoint() - e.getSceneX();
            pressOffsetY = wayPoint.getYPoint() - e.getSceneY();

            setSelectedDrawable(wayPoint);

            wayPoint.getParent().requestFocus();
            addHistory();
        }
    }

    public void processWayLineOnMousePressed(final MouseEvent e) {

        if (e.getButton().equals(MouseButton.PRIMARY)) {

            WayLine wayLine = (WayLine) e.getTarget();
            setSelectedDrawable(wayLine);
            wayLine.getParent().requestFocus();
            addHistory();
        }
    }

    public void processFieldHolderOnKeyPressed(final KeyEvent e) {

        if (e.getCode().equals(KeyCode.Z) && e.isControlDown()) {
            if (e.isShiftDown()) {
                redo();
            } else {
                undo();
            }
        }

        else if (selectedDrawable != null) {
            final WayPoint selectedWayPoint = (selectedDrawable instanceof WayPoint) ? (WayPoint) selectedDrawable : null;

            if (e.getCode().equals(KeyCode.ESCAPE)) {
                setSelectedDrawable(null);
                addHistory();
            }

            if ((e.getCode().equals(KeyCode.DELETE) || e.getCode().equals(KeyCode.BACK_SPACE)) && selectedWayPoint != null) {
                deleteWayPoint(selectedWayPoint);
                setSelectedDrawable(null);
                reportMovingWayPointLocation();
                addHistory();
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

    public void processFieldHolderOnKeyReleased(final KeyEvent e) {

        final WayPoint selectedWayPoint = (selectedDrawable instanceof WayPoint) ? (WayPoint) selectedDrawable : null;

        if (e.getCode().isArrowKey() && selectedWayPoint != null) {
            addHistory();
        }
    }

    public void processFieldHolderOnMousePressed(final MouseEvent e){

        if (e.getButton().equals(MouseButton.PRIMARY)) {
            List<Double> mouseLocation = getMouseLocation(e);
            addWayPoint(mouseLocation);
            addHistory();
        }
    }

    public WayPoint addWayPoint(List<Double> mouseLocation) {

        WayPoint newWayPoint;
        if (selectedDrawable != null) {

            if (selectedDrawable instanceof WayLine){

                final WayLine selectedWayLine = (WayLine) selectedDrawable;

                final WayPoint lastWayPoint = selectedWayLine.getPriorPoint();
                final WayPoint nextWayPoint = selectedWayLine.getNextPoint();
                final WayPoint wayPoint = new WayPoint(mouseLocation);

                addWayPoint(wayPoint, lastWayPoint, nextWayPoint);

                setSelectedDrawable(wayPoint);

                newWayPoint = wayPoint;
            } else {

                final WayPoint selectedWayPoint = (WayPoint) selectedDrawable;
                selectedWayPoint.setCenter(mouseLocation);

                newWayPoint = selectedWayPoint;
            }
        } else {
            final WayPoint lastWayPoint = (wayPoints.size() == 0) ? null : wayPoints.get(wayPoints.size() - 1);
            final WayPoint wayPoint = new WayPoint(mouseLocation);

            addWayPoint(wayPoint, lastWayPoint, null);
            newWayPoint = wayPoint;
        }

        return newWayPoint;
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
            wayLine.setOnMouseReleased(this::processOnMouseReleased);
        }

        if (nextWayPoint != null) {
            wayPoint.setNextDrawable(nextWayPoint.getPriorLine());
        }

        wayPoint.setOnMousePressed(this::processWayPointOnMousePressed);
        wayPoint.setOnMouseDragged(this::processWayPointOnMouseDragged);
        wayPoint.setOnMouseReleased(this::processOnMouseReleased);

        wayPoint.addToPane(this);
        wayPoints.add(addIndex, wayPoint);
    }

    public void deleteWayPoint(final WayPoint wayPoint){

        final WayPoint priorPoint = wayPoint.getPriorPoint();
        final WayPoint nextPoint = wayPoint.getNextPoint();

        final WayLine removeLine;

        if (priorPoint != null) {
            removeLine = wayPoint.getPriorLine();
            priorPoint.setNextDrawable(wayPoint.getNextLine());
        } else if (nextPoint != null) {
            removeLine = nextPoint.getPriorLine();
            nextPoint.setPriorDrawable(null);
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
        List<String> movements = new ArrayList<>();

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

    public void undo() {
        if (currentIndex > 0) {
            currentIndex--;
            List<WayPointHistory> current = getCurrentHistory();
            this.loadHistory(current);
        }
    }

    public void redo() {
        if (currentIndex < wayPointLocationHistory.size() - 1) {
            currentIndex++;
            List<WayPointHistory> current = getCurrentHistory();
            this.loadHistory(current);
        }
    }

    public List<WayPointHistory> getCurrentHistory() {
        if (currentIndex < 0) {
            return new ArrayList<>();
        } else {
            return wayPointLocationHistory.get(currentIndex);
        }
    }

    public void addHistory() {

        List<WayPointHistory> newValue = new ArrayList<>();
        for (WayPoint wayPoint : this.wayPoints) {
            newValue.add(new WayPointHistory(wayPoint));
        }

        List<WayPointHistory> currentValue = currentIndex < 0 ? new ArrayList<>() : wayPointLocationHistory.get(currentIndex);

        if (!newValue.equals(currentValue)) {
            wayPointLocationHistory = wayPointLocationHistory.subList(0, currentIndex + 1);
            wayPointLocationHistory.add(newValue);
            currentIndex = wayPointLocationHistory.size() - 1;
        }
    }

    public void clear() {
        selectedDrawable = null;
        while (wayPoints.size() > 0) {
            deleteWayPoint(wayPoints.get(0));
        }
    }

    public void savePointsToFile(File file) {

        try {
            savePoints(file, wayPoints);
        } catch (IOException e) {
            throw new RuntimeException("error in savePointsToFile", e);
        }
    }

    public void loadPoints(List<List<Double>> wayPointLocations) {

        // Remove all the points from the list of waypoints and from the screen
        clear();

        for (List<Double> wayPointLocation : wayPointLocations) {
            this.addWayPoint(wayPointLocation);
        }
    }

    public void loadHistory(List<WayPointHistory> wayPointLocations) {

        // Remove all the points from the list of waypoints and from the screen
        clear();

        for (WayPointHistory wayPointHistory : wayPointLocations) {
            List<Double> location = new ArrayList<>();
            location.add(wayPointHistory.x);
            location.add(wayPointHistory.y);
            WayPoint wayPoint = this.addWayPoint(location);
            if (wayPointHistory.wayLineSelected) {
                setSelectedDrawable(wayPoint.getPriorLine());
            } else if (wayPointHistory.wayPointSelected) {
                setSelectedDrawable(wayPoint);
            }
        }
    }

    public void readPointsFromFile(File file) {

        if (file == null) {
            return;
        }

        try {

            setClassName(file.getName());

            // Load the points
            List<List<Double>> wayPointLocations = loadPoints(file);

            loadPoints(wayPointLocations);

        } catch (IOException e) {
            throw new RuntimeException("error in readPointsFromFile", e);
        }
    }

    public void savePoints(File file, List<WayPoint> wayPoints) throws IOException {

        try (FileWriter fileWriter = new FileWriter(file)) {
            for(WayPoint point : wayPoints) {

                // Format the line into a inches location of the form "x,y"
                String pointString = String.format("%s,%s\n",
                        FieldUtils.convertToInches(point.getXPoint()),
                        FieldUtils.convertToInches(point.getYPoint()));
                fileWriter.write(pointString);
            }
        }
    }

    public List<List<Double>> loadPoints(File file) throws IOException{

        List<List<Double>> wayPointLocations = new ArrayList<>();

        try (FileReader fileReader = new FileReader(file); BufferedReader bufferedReader = new BufferedReader(fileReader)) {

            String line = bufferedReader.readLine();

            while (line != null) {

                // Parse the line into a pixel x,y location
                String[] lineArray = line.split(",");
                double x = FieldUtils.convertToPixels(Double.parseDouble(lineArray[0]));
                double y = FieldUtils.convertToPixels(Double.parseDouble(lineArray[1]));
                List<Double> wayPointLocation = new ArrayList<>();
                wayPointLocation.add(x);
                wayPointLocation.add(y);
                wayPointLocations.add(wayPointLocation);

                line = bufferedReader.readLine();
            }
        }

        return wayPointLocations;
    }


    public String getClassName() {
        return (className == null) ? DEFAULT_CLASS_NAME : className;
    }

    public void setClassName(String className) {
        if (className == null) {
            this.className = null;
        } else {
            final String[] classNameSplit = className.trim().split("\\W+");
            if (classNameSplit.length > 0) {
                this.className = classNameSplit[0];
            } else {
                this.className = null;
            }
        }
    }

    public void generateCode(File codeFile){

        if (codeFile == null) {
            return;
        }

        setClassName(codeFile.getName());

        final String className = getClassName();

        try {
            FileWriter fileWriter = new FileWriter(codeFile);
            String code = "package org.firstinspires.ftc.teamcode.Autonomous;\n" +
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
                    "}";
            fileWriter.write(code);
            fileWriter.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
