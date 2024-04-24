package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Checkpoint;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * View for a checkpoint on the game board.
 */
public class CheckpointView extends StackPane implements ViewObserver {
    final public static int SPACE_HEIGHT = 60; // 75;
    final public static int SPACE_WIDTH = 60; // 75;
    private Checkpoint checkpoint;

    /**
     * Create a view for the given checkpoint.
     *
     * @param checkpoint the checkpoint this view will represent
     */
    public CheckpointView(Checkpoint checkpoint) {
        this.checkpoint = checkpoint;

        this.setPrefWidth(SPACE_WIDTH);
        this.setMinWidth(SPACE_WIDTH);
        this.setMaxWidth(SPACE_WIDTH);

        this.setPrefHeight(SPACE_HEIGHT);
        this.setMinHeight(SPACE_HEIGHT);
        this.setMaxHeight(SPACE_HEIGHT);

        update(checkpoint);

        // Register as an observer to the checkpoint
        checkpoint.attach(this);
    }

    /**
     * Updates the view of the checkpoint. Could be extended to change appearance based on checkpoint status.
     */
    private void updateView() {
        this.getChildren().clear();

        // Creating a stylized "C" using a polygon with specified points
        Polygon cShape = new Polygon();
        // Define the points to approximate the shape of a "C"
        // Adjust these points as needed to better match your desired "C" shape
        cShape.getPoints().addAll(new Double[]{
                10.0, 0.0,   // Top left corner
                30.0, 0.0,   // Top right corner, starts curving down
                30.0, 5.0,   // Slight curve start
                20.0, 5.0,   // Curve inward
                20.0, 25.0,  // Vertical line down
                30.0, 25.0,  // Curve outward
                30.0, 30.0,  // Bottom right corner, ends curve
                10.0, 30.0   // Bottom left corner
        });
        cShape.setFill(Color.ORANGE);
        cShape.setStroke(Color.BLACK);

        this.getChildren().add(cShape);
    }

    /**
     * Called when the observed subject (Checkpoint) notifies changes.
     *
     * @param subject the subject that was updated
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == this.checkpoint) {
            updateView();
        }
    }
}
