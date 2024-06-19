package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyController;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.util.List;

public class LobbyView extends BaseView {

    private ListView<String> playerListView;
    private LobbyController lobbyController;
    private AppController appController;
    private ApiServices apiServices;
    private Long gameId;

    public LobbyView(AppController appController, Long gameId) {
        super(appController);
        this.appController = appController;
        this.apiServices = appController.getApiServices();
        this.gameId = gameId;
        this.lobbyController = new LobbyController(appController);
    }

    @Override
    public void initialize(){
        setupLobbyView();
    }

    public void setupLobbyView() {
        BorderPane mainLayout = new BorderPane();

        // Get the game lobby from the API services
        Game game = apiServices.getGameById(gameId);

        // Player list on the left
        playerListView = new ListView<>();
        playerListView.setPrefWidth(300);
        playerListView.setPrefHeight(400);

        VBox leftContainer = new VBox(playerListView);
        leftContainer.setSpacing(10);
        mainLayout.setLeft(leftContainer);

        // Map and server details on the right
        VBox rightContainer = new VBox(20);
        rightContainer.setPadding(new Insets(20));
        rightContainer.setAlignment(Pos.TOP_CENTER);

        // Placeholder for the map image
        ImageView mapView = new ImageView(getBoardImageByIndex(0));
        mapView.setFitWidth(200);
        mapView.setFitHeight(200);

        // Server details
        List<PlayerDTO> players = apiServices.getPlayersInGame(gameId);
        Text serverNameText = new Text(game.name);
        Text maxPlayersText = new Text();

        rightContainer.getChildren().addAll(mapView, serverNameText, maxPlayersText);
        mainLayout.setRight(rightContainer);

        // Set up a button to leave the lobby
        Button leaveButton = new Button("Leave");
        leaveButton.setOnAction(event -> {
            appController.leave(true);
            lobbyController.stopLobbyPolling();
        });

        // Set up button to set the players ready state
        Button readyButton = new Button("Ready");
        readyButton.setOnAction(e -> appController.toggleReady());

        // Spacer to push buttons to each side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add the buttons and spacer to a container
        HBox buttonContainer = new HBox(leaveButton, spacer, readyButton);
        buttonContainer.setAlignment(Pos.CENTER);
        buttonContainer.setPadding(new Insets(10));
        mainLayout.setBottom(buttonContainer);

        // Start polling to refresh the list periodically
        lobbyController.startLobbyPolling(gameId, maxPlayersText, playerListView);

        // Add the components to the VBox
        getChildren().addAll(mainLayout);

        // Ensure that the scheduler is closed if the scene is switched
        appController.getRoboRally().getActiveScene().getWindow().setOnCloseRequest(event -> lobbyController.stopLobbyPolling());
    }

    private Image getBoardImageByIndex(int boardIndex) {
        try {
            // Get the resource URL for the images directory
            URL resourceUrl = getClass().getClassLoader().getResource("images/boards/");
            if (resourceUrl != null) {
                File dir = new File(resourceUrl.toURI());
                File[] files = dir.listFiles((d, name) -> name.endsWith(".png"));
                if (files != null && boardIndex >= 0 && boardIndex < files.length) {
                    File selectedFile = files[boardIndex];
                    return new Image(selectedFile.toURI().toString());
                } else {
                    System.err.println("Board index out of range or no PNG files found.");
                }
            } else {
                System.err.println("Boards directory not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle errors (e.g., directory not found, no permission, etc.)
        }
        return null; // Return null if the image cannot be loaded
    }
}
