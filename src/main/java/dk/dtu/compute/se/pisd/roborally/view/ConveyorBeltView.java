package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.fromString;

public class ConveyorBeltView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60;
    final public static int SPACE_WIDTH = 60;
    private ConveyorBelt belt;
    private Heading header;
    private ImageView imageView;

    /**
     * Create a view for the given conveyor belt.
     *
     * @param belt the conveyor belt this view will represent
     */
    public ConveyorBeltView(ConveyorBelt belt) {
        this.belt = belt;
        this.imageView = new ImageView(new Image("/images/blue.png")); // Load image from resources

        this.imageView.setFitWidth(SPACE_WIDTH);
        this.imageView.setFitHeight(SPACE_HEIGHT);
        switch(fromString(belt.direction)) {
            case NORTH:
                break;
            case EAST:
                this.imageView.setRotate(90);
                break;
            case SOUTH:
                this.imageView.setRotate(180);
                break;
            case WEST:
                this.imageView.setRotate(270);
                break;
            default:
                // code block
        }

        // Add the image view as the first child of the stack pane
        this.getChildren().add(0, imageView);

        // Register as an observer to the conveyor belt
        belt.attach(this);
    }

    /**
     * Update view to include a recent change in state.
     *
     * @param subject the subject that triggered the update
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == this.belt) {
            updateView(subject);
        }
    }
}