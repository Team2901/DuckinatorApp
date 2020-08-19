package main.java;/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.control.*;
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

    private static final String DEFAULT_ROOT_NAME = "DuckinatorAuto";
    private final Image field;
    private final Image duck;
    private final ImageView fieldHolder;
    private final ImageView duckHolder;
    private final int lineTicker = 0;
    private final Rectangle rect;
    private final int fieldMeasurementPixels = 510;
    private final int fieldMeasurementInches = 144;
    private final double conversionFactorPixelInch = ((double) fieldMeasurementInches / (double) fieldMeasurementPixels);
    private final String tankDriveMotors;
    private final String holonomicDriveMotors;
    private final String tankDriveInit;
    private final String holonomicDriveInit;
    private final String resetBusyForwardTank;
    private final String rotateTank;
    private final String rotateHolo;
    private final String tankZPower;
    private final String holoZPower;
    private final RadioButton tankDrive;
    private final RadioButton holonomicDrive;
    private final RadioButton mecanumDrive;
    private final ToggleGroup drives;
    private final Label wheelDi;
    private final Label ticksPer;
    private final TextField wheelDia;
    private final TextField ticksPerr;
    private final Label careful;
    private final Label mouseLocation;
    private final Label selectedDrawableLocation;
    ArrayList<Integer> xPixel = new ArrayList<Integer>();
    ArrayList<Integer> yPixel = new ArrayList<Integer>();
    ArrayList<Double> lineLength = new ArrayList<Double>();
    ArrayList<Double> actualPathLength = new ArrayList<Double>();
    ArrayList<Double> encoderPathLength = new ArrayList<Double>();
    ArrayList<Double> angleChanges = new ArrayList<Double>();
    ArrayList<String> leftOrRight = new ArrayList<String>();
    ArrayList<String> movements = new ArrayList<String>();
    ArrayList<WayPoint> points = new ArrayList<WayPoint>();
    ArrayList<Drawable> drawables = new ArrayList<>();
    private Button clear;
    private double wheelDiameter = 4;
    private double ticksPerRotation = 1120;
    private double multiplier;
    private String driveMotors;
    private String driveInit;
    private String movementTemp;
    private String moveHere;
    private String resetBusyForwardHoloMeca;
    private String resetBusyForward;
    private String rotating;
    private String zPower;
    private int togglingKeep = 1;
    private WayPoint selectedPoint;
    private LineConnector selectedLine;
    private List<List<Point>> pointHistory = new ArrayList<>();
    private Integer currentIndex = null;
    private String rootName;

    public ProjectPane() {

        this.setOnKeyPressed(this::gameAreaKeyPress);
        rect = new Rectangle(1200, 600, Color.BLANCHEDALMOND);
        getChildren().add(rect);

        field = new Image(this.getClass().getResourceAsStream("/main/resources/field.png"));
        fieldHolder = new ImageView(field);
        fieldHolder.setFitHeight(fieldMeasurementPixels);
        fieldHolder.setFitWidth(fieldMeasurementPixels);
        fieldHolder.setLayoutX(0);
        fieldHolder.setLayoutY(0);
        getChildren().add(fieldHolder);

        duck = new Image(this.getClass().getResourceAsStream("/main/resources/duck.png"));
        duckHolder = new ImageView(duck);
        duckHolder.setFitHeight(150);
        duckHolder.setFitWidth(163);
        duckHolder.setLayoutX(870);
        duckHolder.setLayoutY(350);
        getChildren().add(duckHolder);

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

        tankDriveMotors = (
                "    private DcMotor leftWheel;\n" +
                        "    private DcMotor rightWheel;\n"
        );

        holonomicDriveMotors = (
                "    private DcMotor fl;\n" +
                        "    private DcMotor fr;\n" +
                        "    private DcMotor bl;\n" +
                        "    private DcMotor br;\n" +
                        "//holonomic encoder counts are slightly innacurate and need to be tested due to different amounts of force and friction on the wheels depending on what you get\n" +
                        "//please adjust personally to each program, we have accounted for slight slippage but just please make sure\n"
        );

        tankDriveInit = (
                "        leftWheel = hardwareMap.dcMotor.get(\"leftWheel\");\n" +
                        "        rightWheel = hardwareMap.dcMotor.get(\"rightWheel\");\n" +
                        "        rightWheel.setDirection(DcMotor.Direction.REVERSE);\n"
        );

        holonomicDriveInit = (
                "        fl = hardwareMap.dcMotor.get(\"fl\");\n" +
                        "        fr = hardwareMap.dcMotor.get(\"fr\");\n" +
                        "        bl = hardwareMap.dcMotor.get(\"bl\");\n" +
                        "        br = hardwareMap.dcMotor.get(\"br\");\n"
        );

        resetBusyForwardTank = (
                "    public void motorReset() {\n" +
                        "        leftWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
                        "        rightWheel.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
                        "        rightWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
                        "        leftWheel.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
                        "    }\n" +
                        "    public void powerBusy() {\n" +
                        "        leftWheel.setPower(0.5);\n" +
                        "        rightWheel.setPower(0.5);\n" +
                        "        while ((rightWheel.isBusy() && leftWheel.isBusy())){}\n" +
                        "        leftWheel.setPower(0);\n" +
                        "        rightWheel.setPower(0);\n" +
                        "    }\n" +
                        "    public void goForward(int gofront){\n" +
                        "        motorReset();\n" +
                        "        rightWheel.setTargetPosition(gofront);\n" +
                        "        leftWheel.setTargetPosition(gofront);\n" +
                        "        powerBusy();\n" +
                        "    }\n"
        );


        rotateTank = (
                "    private void rotate(int degrees) {\n" +
                        "        double leftPower, rightPower;\n" +
                        "        resetAngle();\n" +
                        "        if (degrees < 0) {   // turn right.\n" +
                        "            leftPower = 0.5;\n" +
                        "            rightPower = -0.5;\n" +
                        "        }\n" +
                        "        else if (degrees > 0) {   // turn left.\n" +
                        "            leftPower = -0.5;\n" +
                        "            rightPower = 0.5;\n" +
                        "        }\n" +
                        "        else return;\n" +
                        "        leftWheel.setPower(leftPower);\n" +
                        "        rightWheel.setPower(rightPower);\n"
        );

        rotateHolo = (
                "    private void rotate(int degrees) {\n" +
                        "        double flp, frp, blp, brp;\n" +
                        "        resetAngle();\n" +
                        "        if (degrees < 0) {   // turn right.\n" +
                        "            flp = 0.5;\n" +
                        "            frp = 0.5;\n" +
                        "            blp = 0.5;\n" +
                        "            brp = 0.5;\n" +
                        "        }\n" +
                        "        else if (degrees > 0) {   // turn left.\n" +
                        "            flp = -0.5;\n" +
                        "            frp = -0.5;\n" +
                        "            blp = -0.5;\n" +
                        "            brp = -0.5;\n" +
                        "        }\n" +
                        "        else return;\n" +
                        "        fl.setPower(flp);\n" +
                        "        fr.setPower(frp);\n" +
                        "        bl.setPower(blp);\n" +
                        "        br.setPower(brp);\n"
        );

        tankZPower = (
                "        rightWheel.setPower(0);\n" +
                        "        leftWheel.setPower(0);\n"
        );

        holoZPower = (
                "        fl.setPower(0);\n" +
                        "        fr.setPower(0);\n" +
                        "        bl.setPower(0);\n" +
                        "        br.setPower(0);\n"
        );

        tankDrive.setOnAction(this::processRadioButtons);
        holonomicDrive.setOnAction(this::processRadioButtons);
        mecanumDrive.setOnAction(this::processRadioButtons);
        fieldHolder.setOnMouseClicked(this::processMousePress);
        this.setOnKeyPressed(this::gameAreaKeyPress);
        driveMotors = tankDriveMotors;
        driveInit = tankDriveInit;
        resetBusyForward = resetBusyForwardTank;
        rotating = rotateTank;
        zPower = tankZPower;
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
        double mouseX = (event.getSceneX() - this.getLayoutX()) * fieldMeasurementInches / fieldMeasurementPixels;
        double mouseY = (event.getSceneY() - this.getLayoutY()) * fieldMeasurementInches / fieldMeasurementPixels;
        String mouseString = String.format("%.2f,%.2f", mouseX, mouseY);
        mouseLocation.setText(mouseString);
    }

    public void selectedWayPointLocationUpdate() {
        if (selectedPoint != null) {
            double wayPointX = (selectedPoint.getX()) * fieldMeasurementInches / fieldMeasurementPixels;
            double wayPointY = (selectedPoint.getY()) * fieldMeasurementInches / fieldMeasurementPixels;
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
        if (newValue.equals(currentValue)) {
            // Don't do add anything
        } else {
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
        robotSpecs();
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
                nextWayPoint = null;
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
        if (mouseX > fieldMeasurementPixels) {
            mouseX = fieldMeasurementPixels;
        }
        if (mouseY > fieldMeasurementPixels) {
            mouseY = fieldMeasurementPixels;
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
        drives.selectToggle(null);

        while (points.size() != 0) {
            deleteWayPoint(points.get(0));
        }
    }

    public double convertInchesToEncoderTicks(double c) {
        return ((c / (Math.PI * wheelDiameter)) * ticksPerRotation);
    }

    public void gameAreaKeyPress(KeyEvent event) {
        if (event.getCode() == KeyCode.DELETE) {
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

    public void processRadioButtons(ActionEvent e) {

        if ((e.getSource() == tankDrive)) {
            driveMotors = tankDriveMotors;
            driveInit = tankDriveInit;
            resetBusyForward = resetBusyForwardTank;
            rotating = rotateTank;
            zPower = tankZPower;
            togglingKeep = 1;
        } else if (e.getSource() == holonomicDrive) {
            driveMotors = holonomicDriveMotors;
            driveInit = holonomicDriveInit;
            multiplier = 1.2;
            resety();
            resetBusyForward = resetBusyForwardHoloMeca;
            rotating = rotateHolo;
            zPower = holoZPower;
            togglingKeep = 2;

        } else if (e.getSource() == mecanumDrive) {
            driveMotors = holonomicDriveMotors;
            driveInit = holonomicDriveInit;
            multiplier = 1;
            resety();
            resetBusyForward = resetBusyForwardHoloMeca;
            rotating = rotateHolo;
            zPower = holoZPower;
            togglingKeep = 3;

        }
    }

    public void resety() {
        resetBusyForwardHoloMeca = (
                "    public void motorReset() {\n" +
                        "        fl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
                        "        fr.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
                        "        bl.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
                        "        br.setMode(DcMotor.RunMode.STOP_AND_RESET_ENCODER);\n" +
                        "        fl.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
                        "        fr.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
                        "        bl.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
                        "        br.setMode(DcMotor.RunMode.RUN_TO_POSITION);\n" +
                        "    }\n" +
                        "    public void powerBusy() {\n" +
                        "        fl.setPower(0.5);\n" +
                        "        fr.setPower(0.5);\n" +
                        "        bl.setPower(0.5);\n" +
                        "        br.setPower(0.5);\n" +
                        "        while ((fl.isBusy() && fr())&&(bl.isBusy() && br.isBusy())){}\n" +
                        "        fl.setPower(0);\n" +
                        "        fr.setPower(0);\n" +
                        "        bl.setPower(0);\n" +
                        "        br.setPower(0);\n" +
                        "    }\n" +
                        "    public void goForward(int gofront){\n" +
                        "        motorReset();\n" +
                        "        fl.setTargetPosition((int)Math.round(" + multiplier + "*gofront));\n" +
                        "        fr.setTargetPosition((int)Math.round(-" + multiplier + "*gofront));\n" +
                        "        bl.setTargetPosition((int)Math.round(" + multiplier + "*gofront));\n" +
                        "        br.setTargetPosition((int)Math.round(" + multiplier + "*gofront ));\n" +
                        "        powerBusy();\n" +
                        "    }\n"
        );
    }

    public void robotSpecs() {
        wheelDiameter = new Double(wheelDia.getText());
        ticksPerRotation = new Double(ticksPerr.getText());
    }

    private String convertArrayList(ArrayList<String> stringList) {
        String joinedString = String.join("", stringList);
        return joinedString;
    }

    private String moveHereTwo(ArrayList<WayPoint> points) {
        ArrayList<String> movements = new ArrayList<String>();

        for (int ii = 0; ii < points.size(); ii++) {
            // first point.. nothing to do. assume robot was placed here.
            if (ii == 0) continue;
            // 2 or more points present.
            // calculate the distance to travel between last point and this point
            Double encoderCounts = distanceBetweenTwoPointsInEncoderCounts(points.get(ii), points.get(ii - 1));
            // Three or more points present
            // calculate change in direction.
            if (ii > 1) {
                double angleChange = changeInOrientation(points.get(ii - 2), points.get(ii - 1), points.get(ii));
                movements.add(" rotate(" + (int) Math.round(angleChange) + ");\n");
            }
            movements.add(" goForward(" + (int) Math.round(encoderCounts) + ");\n");
        }

        return convertArrayList(movements);
    }

    private Double distanceBetweenTwoPointsInEncoderCounts(WayPoint firstPoint, WayPoint secondPoint) {
        int dx = secondPoint.getX() - firstPoint.getX();
        int dy = secondPoint.getY() - firstPoint.getY();

        double dxSquared = Math.pow(dx, 2);
        double dySquared = Math.pow(dy, 2);

        double distanceInPixels = Math.sqrt(dxSquared + dySquared);

        double distanceInInches = distanceInPixels * fieldMeasurementInches / fieldMeasurementPixels;

        return convertInchesToEncoderTicks(distanceInInches);
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
            String fileName = codeFile.getName();
            String[] fileNameArray = fileName.split("\\.");
            setRootName(fileNameArray[0]);
            String code = "package org.firstinspires.ftc.teamcode;\n" +
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
                    "@Autonomous(name = \"" + fileNameArray[0] + "\", group = \"DuckSquad\")\n" +
                    "public class " + fileNameArray[0] + " extends LinearOpMode {\n" +
                    driveMotors +
                    "    private int globalAngle;\n" +
                    "    BNO055IMU imu;\n" +
                    "    Orientation lastAngles = new Orientation();\n" +
                    "    @Override\n" +
                    "    public void runOpMode() throws InterruptedException {\n" +
                    "        BNO055IMU.Parameters parameters = new BNO055IMU.Parameters();\n" +
                    "        parameters.mode = BNO055IMU.SensorMode.IMU;\n" +
                    "        parameters.angleUnit = BNO055IMU.AngleUnit.DEGREES;\n" +
                    "        parameters.accelUnit = BNO055IMU.AccelUnit.METERS_PERSEC_PERSEC;\n" +
                    "        parameters.loggingEnabled = false;\n" +
                    "        imu = hardwareMap.get(BNO055IMU.class, \"imu\");\n" +
                    "        imu.initialize(parameters);\n" +
                    "        // make sure the imu gyro is calibrated before continuing.\n" +
                    "        while (!isStopRequested() && !imu.isGyroCalibrated())\n" +
                    "        {\n" +
                    "            sleep(50);\n" +
                    "            idle();\n" +
                    "        }\n" +
                    driveInit +
                    "        waitForStart();\n" +
                    "        if (opModeIsActive()){\n" +
                    " //Our version \n" +
                    moveHereTwo(points) +
                    "\n" +
                    "        }\n" +
                    "    }\n" +
                    resetBusyForward +
                    "    private void resetAngle() {\n" +
                    "        lastAngles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);\n" +
                    "        globalAngle = 0;\n" +
                    "    }\n" +
                    "    private double getAngle() {\n" +
                    "        Orientation angles = imu.getAngularOrientation(AxesReference.INTRINSIC, AxesOrder.ZYX, AngleUnit.DEGREES);\n" +
                    "        double deltaAngle = angles.firstAngle - lastAngles.firstAngle;\n" +
                    "        if (deltaAngle < -180)\n" +
                    "            deltaAngle += 360;\n" +
                    "        else if (deltaAngle > 180)\n" +
                    "            deltaAngle -= 360;\n" +
                    "        globalAngle += deltaAngle;\n" +
                    "        lastAngles = angles;\n" +
                    "        return globalAngle;\n" +
                    "    }\n" +
                    rotating +
                    "        if (degrees < 0) {//right\n" +
                    "            while (opModeIsActive() && getAngle() == 0) {}\n" +
                    "            while (opModeIsActive() && getAngle() > degrees) {}\n" +
                    "        } else {//left\n" +
                    "            while (opModeIsActive() && getAngle() < degrees) {}\n" +
                    "        }\n" +
                    zPower +
                    "        sleep(1000);\n" +
                    "        resetAngle();\n" +
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
        if (rootName == null) {
            this.rootName = DEFAULT_ROOT_NAME;
        } else {
            this.rootName = rootName;
        }
    }
}
