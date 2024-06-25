package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyBrowserController;
import dk.dtu.compute.se.pisd.roborally.model.ApiType;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListView;
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

    private Text connectToServerFeedback;
    private ListView<String> lobbyListView;
    private ImageView mapView;
    private Text serverNameText;
    private Text maxPlayersText;
    private Button joinLobbyButton;

    public LobbyBrowserView(AppController appController) {
        super(appController);
        this.appController = appController;
        this.apiServices = appController.getApiServices();
        this.lobbyBrowserController = new LobbyBrowserController(appController);
    }

    @Override
    public void initialize() {
        BorderPane mainLayout = new BorderPane();
        connectToServerFeedback = new Text();

        // Server and Lobby list on the left
        Text serverHeader = new Text("Select Connection Type");

        // Dropdown for selecting connection type
        ComboBox<String> connectionTypeDropdown = new ComboBox<>();
        connectionTypeDropdown.setId("connectionTypeDropdown");

        connectionTypeDropdown.getItems().addAll("Local", "Server");
        connectionTypeDropdown.setValue("Server");

        // Adding listener to connectionTypeDropdown
        connectionTypeDropdown.getSelectionModel().selectedItemProperty().addListener((options, oldValue, newValue) -> {
            initializeConnection(newValue);
            lobbyBrowserController.updateLobbies(lobbyListView, (apiServices.isReachable()));

        });

        HBox connectToServerBox = new HBox(10, connectionTypeDropdown, connectToServerFeedback);
        connectToServerBox.setAlignment(Pos.CENTER_LEFT);

        // Lobby list view
        lobbyListView = new ListView<>();
        lobbyListView.setPrefWidth(300);
        lobbyListView.setPrefHeight(400);
        lobbyListView.setId("lobbyListView");

        // On the left
        VBox leftContainer = new VBox(10, serverHeader, connectToServerBox, new Text("Lobbies:"), lobbyListView);
        leftContainer.setPadding(new Insets(10));
        mainLayout.setLeft(leftContainer);

        // Map and server details on the right
        VBox rightContainer = new VBox(20);
        rightContainer.setAlignment(Pos.CENTER);

        mapView = new ImageView();
        mapView.setFitWidth(200);
        mapView.setFitHeight(200);

        serverNameText = new Text();
        maxPlayersText = new Text();

        rightContainer.getChildren().addAll(mapView, serverNameText, maxPlayersText);
        mainLayout.setRight(rightContainer);

        lobbyBrowserController.startLobbyPolling(lobbyListView);

        // Join lobby button
        joinLobbyButton = new Button("Join lobby");
        joinLobbyButton.setId("joinLobbyButton");
        joinLobbyButton.setOnAction(event -> {
            lobbyBrowserController.stopLobbyPolling();
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
        backButton.setId("backButton");
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

        // Set the initial connection type on startup
        initializeConnection(connectionTypeDropdown.getValue());

        // Add the main container to the scene
        getChildren().add(mainContainer);
    }

    private void initializeConnection(String connectionType) {
        boolean isConnected;
        if ("Server".equals(connectionType)) {
            apiServices.setApiType(ApiType.SERVER);
            isConnected = apiServices.testConnection(apiServices.getServerIP());
        } else {
            apiServices.setApiType(ApiType.LOCAL);
            isConnected = apiServices.testConnection("localhost");
        }
        if (isConnected) {
            lobbyBrowserController.startLobbyPolling(lobbyListView);
        }
        updateConnectionFeedback(isConnected, connectionType);
    }

    private void updateConnectionFeedback(boolean isConnected, String connectionType) {
        if (isConnected) {
            connectToServerFeedback.setFill(Color.GREEN);
            connectToServerFeedback.setText("Connected successfully to " + connectionType);
            joinLobbyButton.setDisable(false);
        } else {
            connectToServerFeedback.setFill(Color.RED);
            connectToServerFeedback.setText("Failed to connect to " + connectionType);
            joinLobbyButton.setDisable(true);
        }
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
                    showAlert(Alert.AlertType.ERROR, "Invalid file", "Board index out of range or no PNG files found.");
                }
            } else {
                showAlert(Alert.AlertType.ERROR, "Board directory not found.", "Board directory not found. Please check the resources folder.");
                //System.err.println("Boards directory not found.");
            }
        } catch (Exception e) {
            e.printStackTrace();
            // Handle errors (e.g., directory not found, no permission, etc.)
        }
        return null; // Return null if the image cannot be loaded
    }
}
