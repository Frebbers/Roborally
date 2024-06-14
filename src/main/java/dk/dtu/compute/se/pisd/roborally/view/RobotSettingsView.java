package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Node;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;

import java.io.File;
import java.net.URL;
import java.util.Objects;

public class RobotSettingsView extends BaseView {
    private AppController appController;
    private TextField nameField;
    private GridPane robotsSelection;

    public RobotSettingsView(AppController appController) {
        super();
        this.appController = appController;
    }

    @Override
    public void initialize(){
        // Name entry
        Text nameHeader = new Text("Enter your name:");
        nameField = new TextField();
        nameField.setPromptText("Type your name here");
        nameField.textProperty().addListener((observable, oldValue, newValue) -> {
            saveName(newValue); // Method to save the name each time it is modified
        });

        // Robots selection grid
        robotsSelection = new GridPane();
        robotsSelection.setHgap(10);
        robotsSelection.setVgap(10);
        loadRobotImages();

        // Add a button to go back
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> appController.getRoboRally().createStartView(appController));
        HBox bottomContainer = new HBox(backButton);
        bottomContainer.setAlignment(Pos.BOTTOM_LEFT);
        bottomContainer.setPadding(new Insets(10));

        // Layout arrangement
        VBox mainLayout = new VBox(20, nameHeader, nameField, robotsSelection, backButton);
        getChildren().addAll(mainLayout);
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
                    highlightSelected(robotButton);
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

    private void highlightSelected(Button selectedButton) {
        // Iterating through all children of the robotsSelection GridPane
        for (Node child : robotsSelection.getChildren()) {
            if (child instanceof Button) {
                child.setStyle(""); // Reset style for all buttons
            }
        }
        // Apply style to highlight the selected button
        selectedButton.setStyle("-fx-border-color: blue; -fx-border-width: 2; -fx-border-style: solid;");
    }

}
