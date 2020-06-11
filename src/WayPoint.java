import javafx.scene.layout.Pane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

import java.util.List;

public class WayPoint extends Circle implements Drawable {

    private final Circle subCircle;

    private WayLine priorLine;
    private WayLine nextLine;

    private boolean selected;

    public WayPoint(final Double xPoint, final Double yPoint) {
        super(xPoint, yPoint, 10, Color.TRANSPARENT);
        subCircle = new Circle(xPoint, yPoint, 4);
        setCenter(xPoint, yPoint);
    }
    public WayPoint(final List<Double> location) {
        this(location.get(0), location.get(1));
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
    public void setPriorDrawable(final Drawable priorDrawable) {
        priorLine = (WayLine) priorDrawable;

        if (priorLine != null) {
            priorLine.setNextPoint(this);
        }

        redraw();
    }

    @Override
    public void setNextDrawable(final Drawable nextDrawable) {
        nextLine = (WayLine) nextDrawable;

        if (nextLine != null) {
            nextLine.setPriorPoint(this);
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

    @Override
    public String formatLocation() {
        double xInches = FieldUtils.convertToInches(getXPoint());
        double yInches = FieldUtils.convertToInches(getYPoint());
        return String.format("WayPoint: (%.1f, %.1f)", xInches, yInches);
    }

    public WayLine getPriorLine() {
        return priorLine;
    }

    public WayLine getNextLine() {
        return nextLine;
    }

    public void setPriorLine(final WayLine priorLine) {
        this.priorLine = priorLine;
        updateColor();
    }

    public void setNextLine(final WayLine nextLine) {
        this.nextLine = nextLine;
        updateColor();
    }

    private void updateColor() {
        if (selected) {
            subCircle.setFill(Color.GREEN);
        } else {
            subCircle.setFill(priorLine == null ? Color.RED : Color.BLACK);
        }
    }

    public void setCenter(final List<Double> location) {
        setCenter(location.get(0), location.get(1));
    }

    public void setCenter(double xPoint, double yPoint) {

        setCenterX(Math.max(Math.min(xPoint, FieldUtils.FIELD_MEASUREMENT_PIXELS), 0));
        setCenterY(Math.max(Math.min(yPoint, FieldUtils.FIELD_MEASUREMENT_PIXELS), 0));

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
