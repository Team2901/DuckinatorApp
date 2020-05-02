import javafx.scene.shape.Line;

public class WayLine extends Line {

    private WayPoint startPoint;
    private WayPoint endPoint;


    public WayLine(WayPoint startPoint, WayPoint endPoint) {
        super(startPoint.xPoint, startPoint.yPoint, endPoint.xPoint, endPoint.yPoint);
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
}
