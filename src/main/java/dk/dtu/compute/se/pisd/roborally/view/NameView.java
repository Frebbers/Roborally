package dk.dtu.compute.se.pisd.roborally.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;

/**
 * This class represents a view component displaying a player's name.
 */
public class NameView extends StackPane {

    private Label nameLabel;

    /**
     * Constructor to initialize the NameView with a player's name.
     * @param playerName the name of the player to display
     */
    public NameView(String playerName) {
        nameLabel = new Label(playerName);
        styleNameLabel(playerName);
        setPadding(new Insets(0, 0, 40, 0));
        getChildren().add(nameLabel);
    }

    /**
     * Styles the nameLabel with CSS properties for better visibility
     */
    private void styleNameLabel(String playerName) {
        if (playerName != null && !playerName.isEmpty()) {
            nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-font-size: 10px; "
                    + "-fx-effect: dropshadow(gaussian, rgba(0,0,0,0.8), 3, 0, 0, 1); "
                    + "-fx-background-color: rgba(0, 0, 0, 0.5); -fx-padding: 1;");
        } else {
            nameLabel.setStyle(null);
        }
    }

    /**
     * Updates the name displayed in this view.
     * @param playerName the new name to display
     */
    public void updateName(String playerName) {
        nameLabel.setText(playerName);
    }
}
