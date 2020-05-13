import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Line;

public class WayLine extends Line implements Drawable {

    private final Line subLine;
    private WayPoint priorPoint;
    private WayPoint nextPoint;

    private boolean selected;

    public WayLine(final WayPoint priorPoint, final WayPoint nextPoint) {
        super(priorPoint.getXPoint(), priorPoint.getYPoint(), nextPoint.getXPoint(), nextPoint.getYPoint());
        setStrokeWidth(6);
        setStroke(Color.TRANSPARENT);

        subLine = new Line(priorPoint.getXPoint(), priorPoint.getYPoint(), nextPoint.getXPoint(), nextPoint.getYPoint());
        subLine.setStrokeWidth(2);

        priorPoint.setNextDrawable(this, true);
        nextPoint.setPriorDrawable(this, true);

        redraw();
    }

    @Override
    public WayPoint getPriorPoint() {
        return priorPoint;
    }

    @Override
    public WayPoint getNextPoint() {
        return nextPoint;
    }

    @Override
    public WayLine getPriorLine() {
        if (priorPoint != null) {
            return priorPoint.getPriorLine();
        } else {
            return null;
        }
    }

    @Override
    public WayLine getNextLine() {

        if (nextPoint != null) {
            return nextPoint.getNextLine();
        } else {
            return null;
        }
    }

    @Override
    public Drawable getPriorDrawable() {
        return getPriorLine();
    }

    @Override
    public Drawable getNextDrawable() {
        return getNextLine();
    }

    @Override
    public void setPriorDrawable(final Drawable drawable, final boolean recurse) {
        WayPoint wayPoint = (WayPoint) drawable;

        priorPoint = wayPoint;

        if (wayPoint != null && recurse) {
            wayPoint.setNextDrawable(this, false);
        }

        redraw();
    }

    @Override
    public void setNextDrawable(final Drawable drawable, final boolean recurse) {
        WayPoint wayPoint = (WayPoint) drawable;

        nextPoint = wayPoint;

        if (wayPoint != null && recurse) {
            wayPoint.setPriorDrawable(this, false);
        }

        redraw();
    }

    @Override
    public void addToPane(final Pane pane) {

        int index = pane.getChildren().indexOf(priorPoint);
        pane.getChildren().add(index,subLine);
        pane.getChildren().add(index,this);
    }

    @Override
    public void removeFromPane(final Pane pane) {
        pane.getChildren().remove(this);
        pane.getChildren().remove(subLine);
    }

    @Override
    public void redraw() {

        if (priorPoint != null && nextPoint != null) {
            setStartX(priorPoint.getXPoint());
            setStartY(priorPoint.getYPoint());
            setEndX(nextPoint.getXPoint());
            setEndY(nextPoint.getYPoint());

            subLine.setStartX(priorPoint.getXPoint());
            subLine.setStartY(priorPoint.getYPoint());
            subLine.setEndX(nextPoint.getXPoint());
            subLine.setEndY(nextPoint.getYPoint());

        }
    }

    @Override
    public void setSelected(final boolean selected) {
        this.selected = selected;
        updateColor();
    }

    private void updateColor() {
        if (selected) {
            subLine.setStroke(Color.GREEN);
        } else {
            subLine.setStroke(Color.BLACK);
        }
    }
}
