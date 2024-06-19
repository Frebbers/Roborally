package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Checkpoint;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

/**
 * View for a checkpoint on the game board.
 *
 *  @author Nicolai D. Madsen, s213364@dtu.dk
 */
public class CheckpointView extends StackPane implements ViewObserver {
    final public static int SPACE_HEIGHT = 60;
    final public static int SPACE_WIDTH = 60;
    private final Checkpoint checkpoint;

    /**
     * Create a view for the given checkpoint.
     *
     * @param checkpoint the checkpoint this view will represent
     */
    public CheckpointView(Checkpoint checkpoint) {
        this.checkpoint = checkpoint;
        ImageView imageView = new ImageView(new Image("/images/" + checkpoint.getCheckpointId() + ".png"));

        imageView.setFitWidth(SPACE_WIDTH);
        imageView.setFitHeight(SPACE_HEIGHT);

        // Add the image view as the first child of the stack pane
        this.getChildren().add(0, imageView);

        // Register as an observer to the checkpoint
        checkpoint.attach(this);
    }
    /**
     * Return the checkpoint for this view
     *
     * @return Checkpoint return the checkpoint
     */
    public Checkpoint getCheckpoint(){
        return checkpoint;
    }

    /**
     * Update view to include a recent change in state.
     *
     * @param subject the subject that triggered the update
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == this.checkpoint) {
            updateView(subject);
        }
    }
}
