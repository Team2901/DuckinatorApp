import javafx.scene.shape.Line;

public class LineConnector extends Line implements Drawable {
    public Drawable drawBefore = null;
    public Drawable drawAfter = null;

    public LineConnector(Integer x1, Integer y1, Integer x2, Integer y2) {
        super(x1,y1,x2,y2);
    }

    @Override
    public void setBefore(Drawable before) {
        this.drawBefore = before;
    }

    @Override
    public void setAfter(Drawable after) {
        this.drawAfter = after;
    }

    @Override
    public Drawable getBefore() {
        return this.drawBefore;
    }

    @Override
    public Drawable getAfter() {
        return this.drawAfter;
    }

    public void setStartPoint(WayPoint point){
        this.setStartX(point.getCenterX());
        this.setStartY(point.getCenterY());
    }
}
