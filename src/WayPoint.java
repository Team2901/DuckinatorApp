import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class WayPoint extends Circle {

    Color defaultColor;

    WayLine inputLine;
    WayLine outputLine;

    Circle subCircle;

    double xPoint;
    double yPoint;

    double pressOffsetX;
    double pressOffsetY;

    boolean pointSelected;

    public WayPoint(WayPoint lastWayPoint, double xPoint, double yPoint, Color defaultColor, Pane parent) {
        this(lastWayPoint, null, xPoint, yPoint, defaultColor, parent);

    }

    public WayPoint(WayLine lastWayPoint, double xPoint, double yPoint, Color defaultColor, Pane parent) {
        this(lastWayPoint.getStartPoint(), lastWayPoint, xPoint, yPoint, defaultColor, parent);
    }

    public WayPoint(WayPoint lastWayPoint, WayLine wayLine, double xPoint, double yPoint, Color defaultColor, Pane parent) {
        super(xPoint, yPoint, 10, Color.TRANSPARENT);

        this.xPoint = xPoint;
        this.yPoint = yPoint;
        this.defaultColor = defaultColor;

        if (lastWayPoint != null) {
            this.inputLine = new WayLine(lastWayPoint, this);
            lastWayPoint.setOutputLine(this.inputLine);
        }

        if (wayLine != null) {
            this.setOutputLine(wayLine);
        }

        this.subCircle = new Circle(xPoint, yPoint, 4, defaultColor);

        if (inputLine != null) {
            parent.getChildren().add(inputLine);
        }

        parent.getChildren().add(subCircle);

        parent.getChildren().add(this);
    }

    public void setSelected(boolean selected) {

        pointSelected = selected;
        updateColor();
    }

    public void delete(Pane parent) {

        WayLine removeLine;
        if (inputLine != null) {

            removeLine = inputLine;

            WayPoint lastPoint = inputLine.getStartPoint();

            lastPoint.setOutputLine(outputLine);

        } else if (outputLine != null) {

            removeLine = outputLine;

            WayPoint nextPoint = outputLine.getEndPoint();

            nextPoint.setInputLine(null);

        } else {
            removeLine = null;
        }

        if (removeLine != null) {
            parent.getChildren().remove(removeLine);
        }

        parent.getChildren().remove(subCircle);
        parent.getChildren().remove(this);
    }

    private void setDefaultColor(Color defaultColor) {
        this.defaultColor = defaultColor;
        this.updateColor();
    }

    public void setInputLine(WayLine inputLine) {
        this.inputLine = inputLine;

        if (inputLine != null) {
            inputLine.setEndPoint(this);
            setDefaultColor(Color.BLACK);
        } else {
            setDefaultColor(Color.RED);
        }
    }

    public void setOutputLine(WayLine outputLine) {
        this.outputLine = outputLine;

        if (outputLine != null) {
            outputLine.setStartPoint(this);
        }
    }

    public void update() {

        if (inputLine != null) {
            inputLine.update();
        }

        if (outputLine != null) {
            outputLine.update();
        }

        this.setCenterX(xPoint);
        this.setCenterY(yPoint);

        this.updateColor();
        this.subCircle.setCenterX(xPoint);
        this.subCircle.setCenterY(yPoint);
    }

    private void updateColor() {
        if (pointSelected) {
            subCircle.setFill(Color.GREEN);
        } else {
            subCircle.setFill(defaultColor);
        }
    }

    public void updateCenter(double xPoint, double yPoint) {

        this.xPoint = Math.max(Math.min(xPoint, ProjectPane.FIELD_MEASUREMENT_PIXELS), 0);
        this.yPoint = Math.max(Math.min(yPoint, ProjectPane.FIELD_MEASUREMENT_PIXELS), 0);

        update();
    }
}
