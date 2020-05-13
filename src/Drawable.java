import javafx.scene.layout.Pane;

public interface Drawable {

    WayPoint getPriorPoint();
    WayPoint getNextPoint();

    WayLine getPriorLine();
    WayLine getNextLine();

    Drawable getPriorDrawable();
    Drawable getNextDrawable();

    void setNextDrawable(final Drawable drawable, final boolean recurse);
    void setPriorDrawable(final Drawable drawable, final boolean recurse);

    void removeFromPane(final Pane pane);
    void addToPane(final Pane pane);

    void redraw();
    
    void setSelected(final boolean selected);

    String formatLocation();
}
