import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class WayPoint extends Drawable {
    public Color originalColor;
    public Circle subCircle;

    public WayPoint(int xPoint, int yPoint, int i, int bufferZone) {
        super(xPoint, yPoint, i);
        originalColor = Color.BLACK;
    }

    public WayPoint(int xPoint, int yPoint, int i, int bufferZone, Color color) {
        super(xPoint, yPoint, bufferZone, Color.TRANSPARENT);
        originalColor = color;
        subCircle = new Circle(xPoint, yPoint, i, originalColor);
    }
}
