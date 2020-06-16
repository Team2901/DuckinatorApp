import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;
import javafx.scene.shape.Line;

public class LineConnector extends Line implements Drawable {
    public Drawable drawBefore = null;
    public Drawable drawAfter = null;
    public Line subLine;

    public LineConnector(Integer x1, Integer y1, Integer x2, Integer y2) {
        super(x1,y1,x2,y2);
        this.setStroke(Color.TRANSPARENT);
        this.setStrokeWidth(5.0);
        subLine = new Line(x1,y1,x2,y2);
        subLine.setStroke(Color.BLACK);
    }

    public void setSelected(boolean selected){
        if(selected){
            subLine.setStroke(Color.GREEN);
        }else{
            subLine.setStroke(Color.BLACK);
        }
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
        drawBefore = point;
        setLinePositionSetStart(point.getCenterX(),point.getCenterY());
    }

    public void setLinePositionSetStart(double x, double y){
        subLine.setStartX(x);
        subLine.setStartY(y);
        this.setStartX(x);
        this.setStartY(y);
    }

    public void setLinePositionSetEnd(double x, double y){
        subLine.setEndX(x);
        subLine.setEndY(y);
        this.setEndX(x);
        this.setEndY(y);
    }
}
