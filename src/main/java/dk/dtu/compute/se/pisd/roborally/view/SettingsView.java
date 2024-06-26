package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import dk.dtu.compute.se.pisd.roborally.util.Utilities;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public class SettingsView extends BaseView {
    private AppController appController;
    private ApiServices apiServices;
    private TextField nameField;
    private GridPane robotsSelection;

    public SettingsView(AppController appController) {
        super(appController);
        this.appController = appController;
        this.apiServices = appController.getApiServices();
    }

    @Override
    public void initialize(){

        // Server and Lobby list on the left
        Text serverHeader = new Text("Server:");
        serverHeader.setId("settings-header");

        TextField serverIPDialog = new TextField();
        serverIPDialog.setId("settings-ip-dialog");
        serverIPDialog.setPromptText("Enter server IP");
        serverIPDialog.setText(apiServices.getServerIP());
        Utilities.restrictToNumbersDotsAndColons(serverIPDialog);

        // Server button and feedback
        Text connectToServerFeedback = new Text();

        Button connectToServerButton = new Button("Connect to server");
        connectToServerButton.setId("settings-connect-button");
        connectToServerButton.setOnAction(event -> {
            String ip = serverIPDialog.getText();
            if (apiServices.connectToServer(ip)) {
                connectToServerFeedback.setFill(Color.GREEN);
                connectToServerFeedback.setText("Connected to " + ip);
            } else {
                connectToServerFeedback.setFill(Color.RED);
                connectToServerFeedback.setText("Failed to connect to " + ip);
            }
        });

        HBox connectToServerBox = new HBox(connectToServerButton, connectToServerFeedback);
        connectToServerBox.setSpacing(10);

        // Name entry
        Text nameHeader = new Text("Name:");
        nameHeader.setId("settings-name-header");
        nameField = new TextField();
        nameField.setPromptText("Type your name here");
        nameField.setId("settings-name-field");
        nameField.setText(AppConfig.getProperty("local.player.name"));
        Utilities.restrictTextLength(nameField, 8);
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveName(newValue);
        });

        // Robots selection grid
        Text robotHeader = new Text("Robot:");
        robotHeader.setId("settings-robot-header");
        robotsSelection = new GridPane();
        robotsSelection.setHgap(10);
        robotsSelection.setVgap(10);
        loadRobotImages();

        // Add a button to go back
        Button backButton = new Button("Back");
        backButton.setId("settings-back-button");
        backButton.setOnAction(event -> appController.getRoboRally().createStartView(appController));
        HBox bottomContainer = new HBox(backButton);
        bottomContainer.setAlignment(Pos.BOTTOM_LEFT);
        bottomContainer.setPadding(new Insets(10));

        // Layout arrangement
        VBox mainLayout = new VBox(20, serverHeader, serverIPDialog, connectToServerBox, nameHeader, nameField, robotHeader, robotsSelection, backButton);
        getChildren().addAll(mainLayout);

        // Highlight the selected robot from the configuration
        String selectedRobotType = AppConfig.getProperty("local.player.robotType");
        if (selectedRobotType != null) {
            int selectedRobotId = Integer.parseInt(selectedRobotType);
            highlightSelected(selectedRobotId);
        }
    }

    private void loadRobotImages() {
        try {
            URL resourceUrl = getClass().getClassLoader().getResource("images/robots/");
            File dir = new File(resourceUrl.toURI());
            File[] files = dir.listFiles((d, name) -> name.endsWith(".png"));
            for (int i = 0; i < Objects.requireNonNull(files).length; i++) {
                Image robotImage = new Image(files[i].toURI().toString());
                ImageView robotView = new ImageView(robotImage);
                robotView.setFitHeight(100);
                robotView.setFitWidth(100);
                Button robotButton = new Button();
                robotButton.setGraphic(robotView);
                int imageId = i + 1;
                robotButton.setOnAction(event -> {
                    selectRobot(imageId);
                    highlightSelected(imageId);
                });
                robotsSelection.add(robotButton, i % 3, i / 3);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle errors (e.g., directory not found, no permission, etc.)
        }
    }
    private void saveName(String name) {
        AppConfig.setProperty("local.player.name", name);
    }

    private void selectRobot(int id) {
        AppConfig.setProperty("local.player.robotType", String.valueOf(id));
    }

    private void highlightSelected(int id) {
        for (Node child : robotsSelection.getChildren()) {
            if (child instanceof Button) {
                Button button = (Button) child;
                int buttonId = robotsSelection.getRowIndex(button) * 3 + robotsSelection.getColumnIndex(button) + 1;
                if (buttonId == id) {
                    button.setStyle("-fx-border-color: blue; -fx-border-width: 2; -fx-border-style: solid;");
                } else {
                    button.setStyle(""); // Reset style for other buttons
                }
            }
        }
    }
}
