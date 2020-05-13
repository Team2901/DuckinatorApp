import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class WayPoint extends Circle implements Drawable {

    private final Circle subCircle;

    private WayLine priorLine;
    private WayLine nextLine;

    private boolean selected;

    public WayPoint(final double xPoint, final double yPoint) {
        super(xPoint, yPoint, 10, Color.TRANSPARENT);
        subCircle = new Circle(xPoint, yPoint, 4);
        setCenter(xPoint, yPoint);
    }

    @Override
    public WayPoint getPriorPoint() {
        if (priorLine != null) {
            return priorLine.getPriorPoint();
        } else {
            return null;
        }
    }

    @Override
    public WayPoint getNextPoint() {
        if (nextLine != null) {
            return nextLine.getNextPoint();
        } else {
            return null;
        }
    }

    @Override
    public WayLine getPriorLine() {
        return priorLine;
    }

    @Override
    public WayLine getNextLine() {
        return nextLine;
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
        final WayLine wayLine = (WayLine) drawable;

        priorLine = wayLine;

        if (wayLine != null && recurse) {
            wayLine.setNextDrawable(this, false);
        }

        redraw();
    }

    @Override
    public void setNextDrawable(final Drawable drawable, final boolean recurse) {
        WayLine wayLine = (WayLine) drawable;

        nextLine = wayLine;

        if (wayLine != null && recurse) {
            wayLine.setPriorDrawable(this, false);
        }

        redraw();
    }

    @Override
    public void addToPane(Pane pane) {
        pane.getChildren().add(subCircle);
        pane.getChildren().add(this);
    }

    @Override
    public void removeFromPane(Pane pane) {
        pane.getChildren().remove(subCircle);
        pane.getChildren().remove(this);
    }

    @Override
    public void redraw() {

        subCircle.setCenterX(getXPoint());
        subCircle.setCenterY(getYPoint());

        updateColor();
    }

    @Override
    public void setSelected(final boolean selected) {
        this.selected = selected;
        updateColor();
    }

    private void updateColor() {
        if (selected) {
            subCircle.setFill(Color.GREEN);
        } else {
            subCircle.setFill(priorLine == null ? Color.RED : Color.BLACK);
        }
    }

    public void setCenter(double xPoint, double yPoint) {

        setCenterX(Math.max(Math.min(xPoint, ProjectPane.FIELD_MEASUREMENT_PIXELS), 0));
        setCenterY(Math.max(Math.min(yPoint, ProjectPane.FIELD_MEASUREMENT_PIXELS), 0));

        redraw();

        if (priorLine != null) {
            priorLine.redraw();
        }

        if (nextLine != null) {
            nextLine.redraw();
        }
    }

    public double getXPoint() {
        return getCenterX();
    }

    public double getYPoint() {
        return getCenterY();
    }
}
