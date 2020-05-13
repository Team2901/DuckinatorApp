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

        setPriorDrawable(priorPoint);
        setNextDrawable(nextPoint);
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
    public void setPriorDrawable(final Drawable priorDrawable) {
        priorPoint = (WayPoint) priorDrawable;

        if (priorPoint != null) {
            priorPoint.setNextLine(this);
        }

        redraw();
    }

    @Override
    public void setNextDrawable(final Drawable nextDrawable) {
        nextPoint = (WayPoint) nextDrawable;

        if (nextPoint != null) {
            nextPoint.setPriorLine(this);
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

    @Override
    public String formatLocation() {
        double priorXInches = FieldUtils.convertToInches(priorPoint.getXPoint());
        double priorYInches = FieldUtils.convertToInches(priorPoint.getYPoint());
        double nextXInches = FieldUtils.convertToInches(nextPoint.getXPoint());
        double nextYInches = FieldUtils.convertToInches(nextPoint.getYPoint());
        return String.format("WayLine: (%.1f, %.1f) to (%.1f, %.1f)", priorXInches, priorYInches, nextXInches, nextYInches);
    }

    public void setPriorPoint(WayPoint priorPoint) {
        this.priorPoint = priorPoint;
        redraw();
    }

    public void setNextPoint(WayPoint nextPoint) {
        this.nextPoint = nextPoint;
        redraw();
    }

    private void updateColor() {
        if (selected) {
            subLine.setStroke(Color.GREEN);
        } else {
            subLine.setStroke(Color.BLACK);
        }
    }
}
