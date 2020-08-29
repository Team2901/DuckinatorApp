package main.java;

import javafx.scene.control.Label;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import static main.java.ProjectPane.FIELD_MEASUREMENT_INCHES;
import static main.java.ProjectPane.FIELD_MEASUREMENT_PIXELS;

public class WayPoint extends Circle implements Drawable {

    public String name;
    public static int firstPointRadius = 3;
    public static int subsequentPointsRadius = 4;
    public static int bufferZone = 8;
    public Drawable drawBefore = null;
    public Drawable drawAfter = null;
    public Color originalColor;
    public Circle subCircle;

    public double xInches = 0;
    public double yInches = 0;

    public double zAngle = 0;

    public Label label;

    public Label getLabel() {
        return label;
    }

    private void updateLabel() {
        String wayPointString = String.format("%s (%.2f,%.2f)  %.2f°", getName(), getXInches(), getYInches(), getZAngle());
        label.setText(wayPointString);
    }

    public WayPoint(String name, double xInches, double yInches, double zAngle) {
        super(0, 0, bufferZone, Color.TRANSPARENT);

        originalColor = Color.BLACK;
        subCircle = new Circle(0, 0, subsequentPointsRadius, originalColor);

        label = new Label();

        setName(name);
        setCenterInches(xInches, yInches);
        setZAngle(zAngle);
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        if (name.isBlank()) {
            this.name = "WayPoint";
        } else {
            this.name = name;
        }

        updateLabel();
    }

    public void setCenterInches(double xInches, double yInches) {

        this.xInches = Math.max(Math.min(Math.round(xInches * 100.0) / 100.0, FIELD_MEASUREMENT_INCHES), 0);
        this.yInches = Math.max(Math.min(Math.round(yInches * 100.0) / 100.0, FIELD_MEASUREMENT_INCHES), 0);

        double xPixels = this.xInches / FIELD_MEASUREMENT_INCHES * FIELD_MEASUREMENT_PIXELS;
        double yPixels = FIELD_MEASUREMENT_PIXELS - (this.yInches / FIELD_MEASUREMENT_INCHES * FIELD_MEASUREMENT_PIXELS);

        _setCenterPixels(xPixels, yPixels);
    }

    public void setCenterPixels(double xPixelsRaw, double yPixelsRaw) {

        double xPixels = Math.max(Math.min(xPixelsRaw, FIELD_MEASUREMENT_PIXELS), 0);
        double yPixels = Math.max(Math.min(yPixelsRaw, FIELD_MEASUREMENT_PIXELS), 0);

        this.xInches = Math.round(xPixels / FIELD_MEASUREMENT_PIXELS * FIELD_MEASUREMENT_INCHES * 100.0) / 100.0;
        this.yInches = Math.round((FIELD_MEASUREMENT_PIXELS - yPixels) / FIELD_MEASUREMENT_PIXELS * FIELD_MEASUREMENT_INCHES * 100.0) / 100.0;

        _setCenterPixels(xPixels, yPixels);
    }

    private void _setCenterPixels(double xPixels, double yPixels) {

        subCircle.setCenterX(xPixels);
        subCircle.setCenterY(yPixels);
        this.setCenterX(xPixels);
        this.setCenterY(yPixels);

        LineConnector beforeLine = (LineConnector) this.getBefore();
        LineConnector afterLine = (LineConnector) this.getAfter();
        if (beforeLine != null) {
            beforeLine.setLinePositionSetEnd(xPixels, yPixels);
        }
        if (afterLine != null) {
            afterLine.setLinePositionSetStart(xPixels, yPixels);
        }

        updateLabel();
    }

    public WayPoint getPriorPoint() {
        LineConnector line = (LineConnector) this.getBefore();

        if (line == null) {
            return null;
        }

        return (WayPoint) line.getBefore();
    }

    public WayPoint getNextPoint() {
        LineConnector line = (LineConnector) this.getAfter();

        if (line == null) {
            return null;
        }

        return (WayPoint) line.getAfter();
    }

    @Override
    public Drawable getBefore() {
        return this.drawBefore;
    }

    @Override
    public void setBefore(Drawable before) {
        this.drawBefore = before;
    }

    @Override
    public Drawable getAfter() {
        return this.drawAfter;
    }

    @Override
    public void setAfter(Drawable after) {
        this.drawAfter = after;
    }

    public void setSelected(boolean selected) {
        if (selected) {
            subCircle.setFill(Color.GREEN);
        } else {
            subCircle.setFill(originalColor);
        }
    }

    boolean isFirstPoint() {
        return originalColor.equals(Color.RED);
    }

    public void setFirstPoint(boolean b) {
        if (!b) {
            subCircle.setFill(Color.BLACK);
            originalColor = Color.BLACK;
            subCircle.setRadius(subsequentPointsRadius);
        } else {
            subCircle.setFill(Color.RED);
            originalColor = Color.RED;
            subCircle.setRadius(firstPointRadius);
        }
    }

    public double getXInches() {
        return xInches;
    }

    public double getYInches() {
        return yInches;
    }

    public double getZAngle() {
        return zAngle;
    }

    public void setZAngle(double zAngle) {
        this.zAngle = zAngle;
        updateLabel();
    }
}
