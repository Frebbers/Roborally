package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyBrowserController;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import dk.dtu.compute.se.pisd.roborally.util.Utilities;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;

import java.io.File;
import java.net.URL;
import java.util.List;

public class LobbyBrowserView extends BaseView {

    private AppController appController;
    private ApiServices apiServices;
    private LobbyBrowserController lobbyBrowserController;

    private ListView<String> lobbyListView;
    private ImageView mapView;
    private Text serverNameText;
    private Text maxPlayersText;

    public LobbyBrowserView(AppController appController) {
        super(appController);
        this.appController = appController;
        this.apiServices = appController.getApiServices();
        this.lobbyBrowserController = new LobbyBrowserController(appController);
    }

    @Override
    public void initialize() {
        BorderPane mainLayout = new BorderPane();

        // Server and Lobby list on the left
        Text serverHeader = new Text("Server");

        TextField serverIPDialog = new TextField();
        serverIPDialog.setPromptText("Enter server IP");
        serverIPDialog.setText("4.180.19.186");
        Utilities.restrictToNumbersDotsAndColons(serverIPDialog);

        // Server button and feedback
        Text connectToServerFeedback = new Text();

        Button connectToServerButton = new Button("Connect to server");
        connectToServerButton.setOnAction(event -> {
            String ip = serverIPDialog.getText();
            if (lobbyBrowserController.connectToServer(ip)) {
                connectToServerFeedback.setFill(Color.GREEN);
                connectToServerFeedback.setText("Connected to " + ip);
            } else {
                connectToServerFeedback.setFill(Color.RED);
                connectToServerFeedback.setText("Failed to connect to " + ip);
            }
        });

        HBox connectToServerBox = new HBox(connectToServerButton, connectToServerFeedback);
        connectToServerBox.setSpacing(10);

        // Lobby list view
        lobbyListView = new ListView<>();
        lobbyListView.setPrefWidth(300);
        lobbyListView.setPrefHeight(400);

        // On the left
        VBox leftContainer = new VBox(serverHeader, serverIPDialog, connectToServerBox,new Text("Lobbies in server:"), lobbyListView);
        leftContainer.setSpacing(10);
        mainLayout.setLeft(leftContainer);

        // Map and server details on the right
        VBox rightContainer = new VBox(20);
        rightContainer.setAlignment(Pos.CENTER);

        // Placeholder for the map image
        mapView = new ImageView();
        mapView.setFitWidth(200);
        mapView.setFitHeight(200);

        // Server details
        serverNameText = new Text();
        maxPlayersText = new Text();

        rightContainer.getChildren().addAll(mapView, serverNameText, maxPlayersText);
        mainLayout.setRight(rightContainer);

        // Fetch and observe the list of games
        lobbyBrowserController.startLobbyPolling(lobbyListView);

        // Join lobby button
        Button joinLobbyButton = new Button("Join lobby");
        joinLobbyButton.setOnAction(event -> {
            String selectedGameName = lobbyListView.getSelectionModel().getSelectedItem();
            lobbyBrowserController.joinSelectedLobby(selectedGameName);
        });

        // Add a listener to the lobby list view selection
        lobbyListView.getSelectionModel().selectedItemProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue != null) {
                updateLobbyDetails(newValue);
                boolean isFull = lobbyBrowserController.isLobbyFull(newValue);
                joinLobbyButton.setDisable(isFull);
            } else {
                joinLobbyButton.setDisable(true);
            }
        });

        // Add a button to go back
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> appController.getRoboRally().createStartView(appController));

        // Spacer to push buttons to each side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add the buttons and spacer to a container
        HBox buttonContainer = new HBox(backButton, spacer, joinLobbyButton);

        // Add spacing between main content and the button container
        VBox mainContainer = new VBox(mainLayout, buttonContainer);
        mainContainer.setSpacing(10);
        mainContainer.setPadding(new Insets(0,30,0,0));

        // Add the main container to the scene
        getChildren().add(mainContainer);
    }

    private void updateLobbyDetails(String lobbyName) {
        Game game = lobbyBrowserController.getGameByName(lobbyName);
        if (game != null) {
            Image boardImage = getBoardImageByIndex(Math.toIntExact(game.boardId - 1));
            mapView.setImage(boardImage);

            List<PlayerDTO> players = apiServices.getPlayersInGame(game.id);
            serverNameText.setText(game.name);
            maxPlayersText.setText(players.size() + " / " + game.maxPlayers + " players");
        }
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
