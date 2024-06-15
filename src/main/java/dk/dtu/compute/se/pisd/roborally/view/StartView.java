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


public class StartView extends BaseView {

    private AppController appController;
    private static final double MIN_START_WIDTH = 600;

    public StartView(AppController appController) {
        super();
        this.appController = appController;
    }

    @Override
    public void initialize() {
        // Background image setup
        String imagePath = "/images/RoboLobbyBackground.png";
        Image backgroundImage = new Image(getClass().getResourceAsStream(imagePath));
        ImageView imageView = new ImageView(backgroundImage);
        imageView.setFitWidth(MIN_START_WIDTH);
        imageView.setFitHeight(MIN_START_WIDTH);
        imageView.setPreserveRatio(true);
        imageView.setMouseTransparent(true);

        // Buttons
        Button startLobbyButton = new Button("Create lobby");
        Button joinLobbyButton = new Button("Join lobby");
        Button modifyRobotButton = new Button("Modify Robot");
        Button exitButton = new Button("Exit");

        startLobbyButton.setOnAction(e -> appController.createLobby());
        modifyRobotButton.setOnAction(e -> appController.getRoboRally().createRobotSettingsView(appController));
        joinLobbyButton.setOnAction(e -> appController.getRoboRally().createLobbyBrowserView(appController));
        exitButton.setOnAction(e -> appController.exit());

        VBox buttonContainer = new VBox(10, startLobbyButton, modifyRobotButton, joinLobbyButton, exitButton);
        buttonContainer.setAlignment(Pos.CENTER);

        // Information
        Text nameLabel = new Text("Name: ");
        Text statusLabel = new Text("Status: ");
        StackPane nameLabelContainer = new StackPane(nameLabel);
        StackPane statusLabelContainer = new StackPane(statusLabel);

        BackgroundFill backgroundFill = new BackgroundFill(Color.rgb(45, 45, 45, 0.5), CornerRadii.EMPTY, Insets.EMPTY);
        Background background = new Background(backgroundFill);

        nameLabelContainer.setBackground(background);
        statusLabelContainer.setBackground(background);

        VBox infoContainer = new VBox(10, nameLabelContainer, statusLabelContainer);
        infoContainer.setAlignment(Pos.BOTTOM_CENTER);
        infoContainer.setPadding(new Insets(20));

        StackPane mainLayout = new StackPane();
        mainLayout.getChildren().addAll(imageView, infoContainer, buttonContainer);
        mainLayout.setAlignment(Pos.CENTER);

        getChildren().add(mainLayout);
    }
}
