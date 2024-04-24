package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;

public class ConveyorBeltView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60;
    final public static int SPACE_WIDTH = 60;
    private ConveyorBelt belt;
    private ImageView imageView;

    /**
     * Create a view for the given conveyor belt.
     *
     * @param belt the conveyor belt this view will represent
     */
    public ConveyorBeltView(ConveyorBelt belt, Space space) {
        this.belt = belt;
        this.imageView = new ImageView(new Image("/images/blue.png")); // Load image from resources

        this.imageView.setFitWidth(SPACE_WIDTH);
        this.imageView.setFitHeight(SPACE_HEIGHT);
        this.getChildren().add(imageView); // Add the ImageView to the StackPane
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
        // You may need to update the view based on the state of the conveyor belt
        // For example, if the image should change based on the state of the belt
        // you can modify the imageView accordingly here.
    }
}