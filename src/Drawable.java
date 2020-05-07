import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public class Drawable extends Circle {
    public Drawable drawBefore;
    public Drawable drawAfter;

    public Drawable(int xPoint, int yPoint, int i) {
        super(xPoint,yPoint,i);
    }

    public Drawable(int xPoint, int yPoint, int i, Color color) {
        super(xPoint,yPoint,i,color);
    }
}
