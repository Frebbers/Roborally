package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.Wall;
import javafx.application.Platform;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

/**
 * View for a checkpoint on the game board.
 *
 *  @author Nicolai D. Madsen, s213364@dtu.dk
 */
public class WallView extends StackPane implements ViewObserver {
    final public static int SPACE_HEIGHT = 60;
    final public static int SPACE_WIDTH = 10;


    /**
     * Create a view for the given checkpoint.
     *
     * @param wall the wall this view will represent
     */
    public WallView(Wall wall) {
        ImageView imageView = new ImageView(new Image("/images/wall.png"));

        imageView.setFitWidth(SPACE_WIDTH);
        imageView.setFitHeight(SPACE_HEIGHT);

        switch(wall.getHeading()) {
            case NORTH:
                break;
            case EAST:
                imageView.setRotate(90);
                break;
            case SOUTH:
                imageView.setRotate(180);
                break;
            case WEST:
                imageView.setRotate(270);
                break;
            default:
                // code block
        }

        double translateX = 0.0;
        double translateY = 0.0;
        switch (wall.getOffset()) {
            case NORTH:
                translateY = (double) -SPACE_HEIGHT / 2; // Adjust vertically within the cell
                break;
            case SOUTH:
                translateY = (double) SPACE_HEIGHT / 2;  // Adjust vertically within the cell
                break;
            case EAST:
                translateX = (double) SPACE_WIDTH * ((double) SPACE_HEIGHT / SPACE_WIDTH) / 2;   // Adjust horizontally within the cell
                break;
            case WEST:
                translateX = (double) -SPACE_WIDTH * ((double) SPACE_HEIGHT / SPACE_WIDTH) / 2;  // Adjust horizontally within the cell
                break;
        }

        setTranslateX(translateX);
        setTranslateY(translateY);

        // Add the image view as the first child of the stack pane
        this.getChildren().add(0, imageView);

        // Register as an observer to the conveyor belt
        wall.attach(this);
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
