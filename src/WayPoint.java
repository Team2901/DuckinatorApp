import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class WayPoint extends Circle {

    Color defaultColor;

    WayPoint lastWayPoint;
    WayPoint nextWayPoint;
    Line lastLine;

    int xPoint;
    int yPoint;

    double orgSceneX, orgSceneY;

    public WayPoint(WayPoint lastWayPoint, int xPoint, int yPoint, Color defaultColor, Pane parent) {
        super(xPoint, yPoint, 4, defaultColor);

        this.defaultColor = defaultColor;

        this.xPoint = xPoint;
        this.yPoint = yPoint;

        setLastWayPoint(lastWayPoint);

        parent.getChildren().add(this);

        if (lastLine != null) {
            parent.getChildren().add(lastLine);
        }
    }

    public void resetColor() {
        this.setFill(defaultColor);
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

        parent.getChildren().remove(this);
    }

    public void updateCenter( int xPoint, int yPoint) {

        this.xPoint = Math.max(Math.min(xPoint, ProjectPane.FIELD_MEASUREMENT_PIXELS), 0);
        this.yPoint = Math.max(Math.min(yPoint, ProjectPane.FIELD_MEASUREMENT_PIXELS), 0);

        setCenterX(this.xPoint);
        setCenterY(this.yPoint);

        if (lastLine != null) {
            lastLine.setEndX(this.xPoint);
            lastLine.setEndY(this.yPoint);
        }

        if (nextWayPoint != null && nextWayPoint.lastLine != null) {
            nextWayPoint.lastLine.setStartX(getCenterX());
            nextWayPoint.lastLine.setStartY(getCenterY());
        }
    }

    public void setLastWayPoint(WayPoint lastWayPoint) {

        this.lastWayPoint = lastWayPoint;

        if (lastWayPoint != null) {
            this.defaultColor = Color.BLACK;

            lastWayPoint.nextWayPoint = this;

            if (this.lastLine == null) {
                this.lastLine = new Line(lastWayPoint.getCenterX(), lastWayPoint.getCenterY(), xPoint, yPoint);
            } else {
                this.lastLine.setStartX(lastWayPoint.getCenterX());
                this.lastLine.setStartY(lastWayPoint.getCenterY());
            }
        } else {
            this.defaultColor = Color.RED;
        }

        this.resetColor();
    }
}
