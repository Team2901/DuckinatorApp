import javafx.scene.layout.Pane;

public interface Drawable {

    WayPoint getPriorPoint();
    WayPoint getNextPoint();

    /**
     * Sets up a two way relationship between this drawable and nextDrawable
     * @param nextDrawable other drawable to set up the relationship with
     */
    void setNextDrawable(final Drawable nextDrawable);

    /**
     * Sets up a two way relationship between this nextDrawable and priorDrawable
     * @param priorDrawable other drawable to set up the relationship with
     */
    void setPriorDrawable(final Drawable priorDrawable);

    /**
     * Removes the drawable from the pane
     * @param pane pane to remove drawable from
     */
    void removeFromPane(final Pane pane);

    /**
     * Adds the drawable to the pane
     * @param pane pane to add drawable to
     */
    void addToPane(final Pane pane);

    /**
     * Redraws the drawable to update any properties that have changes (ex color and position)
     */
    void redraw();

    /**
     * Set the selected state of the drawable
     * @param selected selected status
     */
    void setSelected(final boolean selected);

    String formatLocation();
}
