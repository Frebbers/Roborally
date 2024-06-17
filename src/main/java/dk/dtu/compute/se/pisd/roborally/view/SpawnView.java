package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Spawn;
import dk.dtu.compute.se.pisd.roborally.model.Wall;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import org.jetbrains.annotations.NotNull;

/**
 * View for a checkpoint on the game board.
 *
 *  @author Nicolai D. Madsen, s213364@dtu.dk
 */
public class SpawnView extends StackPane implements ViewObserver {
    final public static int SPACE_HEIGHT = 60;
    final public static int SPACE_WIDTH = 60;
    private Spawn spawn;


    /**
     * Create a view for the given spawn.
     *
     * @param spawn the spawn this view will represent
     */
    public SpawnView(@NotNull Spawn spawn) {
        this.spawn = spawn;

        ImageView imageView = new ImageView(new Image("/images/startField.png"));

        imageView.setFitWidth(SPACE_WIDTH);
        imageView.setFitHeight(SPACE_HEIGHT);

        // Add the image view as the first child of the stack pane
        this.getChildren().add(0, imageView);

        spawn.attach(this);
        update(spawn);
    }

    /**
     * Update view to include a recent change in state.
     *
     * @param subject the subject that triggered the update
     */
    @Override
    public void updateView(Subject subject) {
    }
}
