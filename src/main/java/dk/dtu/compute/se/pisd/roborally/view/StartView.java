package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.geometry.Insets;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.control.Button;
import javafx.scene.layout.*;
import javafx.geometry.Pos;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;


public class StartView extends StackPane {

    private static final double MIN_START_WIDTH = 600;

    public StartView(AppController appController) {
        String imagePath = "/images/RoboLobbyBackground.png";
        Image backgroundImage = new Image(getClass().getResourceAsStream(imagePath));
        ImageView imageView = new ImageView(backgroundImage);
        imageView.setFitWidth(MIN_START_WIDTH); // Adjust width as needed
        imageView.setFitHeight(MIN_START_WIDTH);
        imageView.setPreserveRatio(true);

        // Buttons for navigation
        Button startLobbyButton = new Button("Create lobby");
        startLobbyButton.setOnAction(e -> appController.createLobby());

        Button createCharacterButton = new Button("Create Character");
        createCharacterButton.setOnAction(e -> appController.createCharacter());

        Button joinLobbyButton = new Button("Join lobby");
        joinLobbyButton.setOnAction(e -> appController.joinLobby());

        Button exitButton = new Button("Exit");
        exitButton.setOnAction(e -> appController.exit());

        VBox buttonContainer = new VBox(10, startLobbyButton, createCharacterButton, joinLobbyButton, exitButton);
        buttonContainer.setAlignment(Pos.CENTER); // Center the buttons


        // StackPanes for text labels with background
        Text nameLabel = new Text("Name: ");
        Text statusLabel = new Text("Status: ");

        StackPane nameLabelContainer = new StackPane(nameLabel);
        StackPane statusLabelContainer = new StackPane(statusLabel);

        BackgroundFill backgroundFill = new BackgroundFill(Color.rgb(45, 45, 45, 0.5), CornerRadii.EMPTY, null);
        Background background = new Background(backgroundFill);

        nameLabelContainer.setBackground(background);
        statusLabelContainer.setBackground(background);

        // VBox for text labels and set its alignment to bottom center
        VBox infoContainer = new VBox(10, nameLabelContainer, statusLabelContainer);
        infoContainer.setAlignment(Pos.BOTTOM_CENTER);
        infoContainer.setPadding(new Insets(20));

        // Add all to StackPane
        getChildren().addAll(imageView, infoContainer, buttonContainer);
        setAlignment(Pos.CENTER); // Center the StackPane content
    }
}
