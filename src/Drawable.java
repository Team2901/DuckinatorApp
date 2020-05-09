import javafx.scene.paint.Color;
import javafx.scene.shape.Circle;

public interface Drawable {
    public void setBefore(Drawable before);
    public void setAfter(Drawable after);
    public Drawable getBefore();
    public Drawable getAfter();
}
