package dk.dtu.compute.se.pisd.roborally.view;

import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.layout.StackPane;
public class NameView extends StackPane {

    private Label nameLabel;

    public NameView(String playerName) {
        this.nameLabel = new Label(playerName);
        this.nameLabel.setStyle("-fx-text-fill: white; -fx-font-weight: bold; -fx-effect: dropshadow( gaussian , black, 3,0,0,1 );");
        this.setPadding(new Insets(0, 0, 40, 0));
        this.getChildren().add(nameLabel);
    }


    public void updateName(String playerName) {
        this.nameLabel.setText(playerName);
    }
}
