import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class WayLine extends Line {

    private WayPoint startPoint;
    private WayPoint endPoint;

    private boolean lineSelected;

    public WayLine(WayPoint startPoint, WayPoint endPoint) {
        super(startPoint.xPoint, startPoint.yPoint, endPoint.xPoint, endPoint.yPoint);
        this.setStrokeWidth(2);
        this.startPoint = startPoint;
        this.endPoint = endPoint;

        this.update();
    }

    public WayPoint getStartPoint() {
        return startPoint;
    }

    public void setStartPoint(WayPoint startPoint) {
        this.startPoint = startPoint;
        this.update();
    }

    public WayPoint getEndPoint() {
        return endPoint;
    }

    public void setEndPoint(WayPoint endPoint) {
        this.endPoint = endPoint;
        this.update();
    }

    public void update() {

        this.setStartX(startPoint.xPoint);
        this.setStartY(startPoint.yPoint);
        this.setEndX(endPoint.xPoint);
        this.setEndY(endPoint.yPoint);
    }

    public void setSelected(boolean selected) {
        this.lineSelected = selected;
        updateColor();
    }

    private void updateColor() {
        if (lineSelected) {
            this.setStroke(Color.GREEN);
        } else {
            this.setStroke(Color.BLACK);
        }
    }
}
