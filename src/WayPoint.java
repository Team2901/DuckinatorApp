import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class WayPoint extends Drawable {
    public Color originalColor;

    public WayPoint(int xPoint, int yPoint, int i) {
        super(xPoint, yPoint, i);
        originalColor = Color.BLACK;
    }

    public WayPoint(int xPoint, int yPoint, int i, Color color) {
        super(xPoint, yPoint, i, color);
        originalColor = color;
    }
}
