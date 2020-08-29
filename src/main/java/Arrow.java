package main.java;

import javafx.scene.shape.LineTo;
import javafx.scene.shape.MoveTo;
import javafx.scene.shape.Path;

public class Arrow extends Path {

    private static final int ARROW_LENGTH = 25;
    private static final int ARROW_SIDE_LENGTH = 5;
    private static final int ARROW_SIDE_ANGLE = 45;

    double angle = 0;
    double startX = 0;
    double startY = 0;

    public Arrow() {
        this(0,0,0);
    }

    public Arrow(double startX, double startY, double angle) {

        //Line
        getElements().add(new MoveTo(0, 0));
        getElements().add(new LineTo(0, 0));

        //ArrowHead
        getElements().add(new LineTo(0, 0));
        getElements().add(new LineTo(0, 0));
        getElements().add(new LineTo(0, 0));

        setStart(startX, startY);
        setAngle(angle);
    }

    public void setStart(double startX, double startY) {
        this.startX = startX;
        this.startY = startY;
        update();
    }

    public void setAngle(double angle) {
        this.angle = angle;
        update();
    }

    public void update() {

        double a0 = Math.toRadians(-angle);
        double a1 = Math.toRadians(-angle + 90 + ARROW_SIDE_ANGLE);
        double a2 = Math.toRadians(-angle - 90 - ARROW_SIDE_ANGLE);

        double endX = Math.cos(a0) * ARROW_LENGTH + startX;
        double endY = Math.sin(a0) * ARROW_LENGTH + startY;

        double x1 = Math.cos(a1) * ARROW_SIDE_LENGTH + endX;
        double y1 = Math.sin(a1) * ARROW_SIDE_LENGTH + endY;
        double x2 = Math.cos(a2) * ARROW_SIDE_LENGTH + endX ;
        double y2 = Math.sin(a2) * ARROW_SIDE_LENGTH + endY ;

        update(startX, startY, endX, endY, x1, y1, x2, y2);
    }

    private void update(double startX, double startY, double endX, double endY,double x1, double y1, double x2, double y2) {

        // Start point
        MoveTo moveTo = ((MoveTo) getElements().get(0));
        moveTo.setX(startX);
        moveTo.setY(startY);

        // end point
        LineTo line1 = (LineTo) getElements().get(1);
        line1.setX(endX);
        line1.setY(endY);

        // first arrow side
        LineTo line2 = (LineTo) getElements().get(2);
        line2.setX(x1);
        line2.setY(y1);

        // back to end point
        LineTo line3 = (LineTo) getElements().get(3);
        line3.setX(endX);
        line3.setY(endY);

        // second arrow side
        LineTo line4 = (LineTo) getElements().get(4);
        line4.setX(x2);
        line4.setY(y2);

    }
}