import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class WayPoint extends Circle {

    Color defaultColor;

    WayPoint lastWayPoint;
    WayPoint nextWayPoint;
    Line lastLine;
    Circle subCircle;

    double xPoint;
    double yPoint;

    double pressOffsetX;
    double pressOffsetY;

    public WayPoint(WayPoint lastWayPoint, double xPoint, double yPoint, Color defaultColor, Pane parent) {
        super(xPoint, yPoint, 10, Color.TRANSPARENT);

        this.xPoint = xPoint;
        this.yPoint = yPoint;
        this.defaultColor = defaultColor;

        this.subCircle = new Circle(xPoint, yPoint, 4, defaultColor);

        setLastWayPoint(lastWayPoint);

        if (lastLine != null) {
            parent.getChildren().add(lastLine);
        }

        parent.getChildren().add(subCircle);

        parent.getChildren().add(this);
    }

    public void setSelected(boolean selected) {
        if (selected) {
            subCircle.setFill(Color.GREEN);
        } else {
            subCircle.setFill(defaultColor);
        }
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

        parent.getChildren().remove(subCircle);
        parent.getChildren().remove(this);
    }

    public void updateCenter(double xPoint, double yPoint) {

        this.xPoint = Math.max(Math.min(xPoint, ProjectPane.FIELD_MEASUREMENT_PIXELS), 0);
        this.yPoint = Math.max(Math.min(yPoint, ProjectPane.FIELD_MEASUREMENT_PIXELS), 0);

        setCenterX(this.xPoint);
        setCenterY(this.yPoint);

        subCircle.setCenterX(this.xPoint);
        subCircle.setCenterY(this.yPoint);

        if (lastLine != null) {
            lastLine.setEndX(this.xPoint);
            lastLine.setEndY(this.yPoint);
        }

        if (nextWayPoint != null && nextWayPoint.lastLine != null) {
            nextWayPoint.lastLine.setStartX(this.xPoint);
            nextWayPoint.lastLine.setStartY(this.yPoint);
        }
    }

    public void setLastWayPoint(WayPoint lastWayPoint) {

        this.lastWayPoint = lastWayPoint;

        if (lastWayPoint != null) {
            this.defaultColor = Color.BLACK;

            lastWayPoint.nextWayPoint = this;

            if (this.lastLine == null) {
                this.lastLine = new Line(lastWayPoint.xPoint, lastWayPoint.yPoint, xPoint, yPoint);
            } else {
                this.lastLine.setStartX(lastWayPoint.xPoint);
                this.lastLine.setStartY(lastWayPoint.yPoint);
            }
        } else {
            this.defaultColor = Color.RED;
        }

        this.setSelected(false);
    }
}
