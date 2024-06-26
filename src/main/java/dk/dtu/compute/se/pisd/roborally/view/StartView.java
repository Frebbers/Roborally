package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
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
    private Text nameLabel;

    public StartView(AppController appController) {
        super(appController);
        this.appController = appController;
    }

    /**
     * Initializes the StartView by setting up the UI components and their event handlers.
     */
    @Override
    public void initialize() {
        nameLabel = new Text();
        nameLabel.setId("nameLabel");
        updatePlayerName();

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
        startLobbyButton.setId("startLobbyButton");
        Button joinLobbyButton = new Button("Join lobby");
        joinLobbyButton.setId("joinLobbyButton");
        Button settingsButton = new Button("Settings");
        settingsButton.setId("settingsButton");
        Button exitButton = new Button("Exit");
        exitButton.setId("exitButton");

        startLobbyButton.setOnAction(e -> appController.getRoboRally().createNewLobbyView(appController));
        joinLobbyButton.setOnAction(e -> appController.getRoboRally().createLobbyBrowserView(appController));
        settingsButton.setOnAction(e -> appController.getRoboRally().createRobotSettingsView(appController));
        exitButton.setOnAction(e -> appController.exit());

        VBox buttonContainer = new VBox(10, startLobbyButton, joinLobbyButton, settingsButton, exitButton);
        buttonContainer.setAlignment(Pos.CENTER);

        StackPane nameLabelContainer = new StackPane(nameLabel);
        nameLabelContainer.setBackground(new Background(new BackgroundFill(Color.rgb(45, 45, 45, 0.5), CornerRadii.EMPTY, Insets.EMPTY)));
        nameLabelContainer.setPadding(new Insets(20));

        VBox infoContainer = new VBox(10, nameLabelContainer);
        infoContainer.setAlignment(Pos.BOTTOM_CENTER);
        infoContainer.setPadding(new Insets(20));

        StackPane mainLayout = new StackPane();
        mainLayout.getChildren().addAll(imageView, infoContainer, buttonContainer);
        mainLayout.setAlignment(Pos.CENTER);

        getChildren().add(mainLayout);
    }

    /**
     * Updates the displayed player name based on the application configuration.
     */
    public void updatePlayerName() {
        String name = AppConfig.getProperty("local.player.name");
        if (name != null) {
            nameLabel.setText("Character name: " + name);
        } else {
            nameLabel.setText("Character: None");
        }
    }
}
