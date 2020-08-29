package main.java;

import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class LineConnector extends Line implements Drawable {
    public Drawable drawBefore = null;
    public Drawable drawAfter = null;
    public Line subLine;

    public LineConnector(double x1, double y1, double x2, double y2) {
        super(x1, y1, x2, y2);
        this.setStroke(Color.TRANSPARENT);
        this.setStrokeWidth(5.0);
        subLine = new Line(x1, y1, x2, y2);
        subLine.setStroke(Color.BLACK);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            subLine.setStroke(Color.GREEN);
        } else {
            subLine.setStroke(Color.BLACK);
        }
    }

    @Override
    public Drawable getBefore() {
        return this.drawBefore;
    }

    @Override
    public void setBefore(Drawable before) {
        this.drawBefore = before;
        updateLine();
    }

    @Override
    public Drawable getAfter() {
        return this.drawAfter;
    }

    @Override
    public void setAfter(Drawable after) {
        this.drawAfter = after;
        updateLine();
    }

    public void setLinePositionSetStart(double x, double y) {
        subLine.setStartX(x);
        subLine.setStartY(y);
        this.setStartX(x);
        this.setStartY(y);
    }

    public void setLinePositionSetEnd(double x, double y) {
        subLine.setEndX(x);
        subLine.setEndY(y);
        this.setEndX(x);
        this.setEndY(y);
    }

    public double getAngle() {
        double dx = this.getEndX() - this.getStartX();
        double dy = -(this.getEndY() - this.getStartY());
        double thetaRad = Math.atan2(dy, dx);
        return Math.toDegrees(thetaRad);
    }

    public void updateLine() {

        if (drawBefore instanceof WayPoint) {
            WayPoint point = (WayPoint) drawBefore;
            setLinePositionSetStart(point.getCenterX(), point.getCenterY());
            point.updateArrow();
        }

        if (drawAfter instanceof WayPoint) {
            WayPoint point = (WayPoint) drawAfter;
            setLinePositionSetEnd(point.getCenterX(), point.getCenterY());
            point.updateArrow();
        }
    }

}
