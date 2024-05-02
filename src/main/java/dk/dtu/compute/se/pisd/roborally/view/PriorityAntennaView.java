package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.model.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.PriorityAntenna;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;

public class PriorityAntennaView extends StackPane {
    final public static int SPACE_HEIGHT = 60;
    final public static int SPACE_WIDTH = 60;
    private final PriorityAntenna priorityAntenna;

    public PriorityAntennaView(PriorityAntenna priorityAntenna) {
        this.priorityAntenna = priorityAntenna;
        ImageView imageView = new ImageView(new Image("/images/antenna.png"));

        imageView.setFitWidth(SPACE_WIDTH);
        imageView.setFitHeight(SPACE_HEIGHT);

        // Add the image view as the first child of the stack pane
        this.getChildren().add(0, imageView);
    }
}
