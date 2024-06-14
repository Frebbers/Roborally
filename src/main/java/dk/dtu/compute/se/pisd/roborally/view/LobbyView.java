package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyController;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.scene.Scene;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;

public class LobbyView extends VBox {

    private ListView<String> playerListView;
    private Button readyButton;
    private LobbyController lobbyController;
    private AppController appController;

    public LobbyView(LobbyController lobbyController, Long gameId) {
        this.lobbyController = lobbyController;
        this.appController = lobbyController.getAppController();
        setupLobbyView(gameId);
    }

    public void setupLobbyView(Long gameId) {
        playerListView = new ListView<>();
        readyButton = new Button("Ready");

        // Initial population of the ListView
        lobbyController.updateLobby(playerListView, gameId);

        // Set up button to set the players ready state
        readyButton.setOnAction(e -> appController.toggleReady());

        // Start polling to refresh the list periodically
        lobbyController.startLobbyPolling(gameId, playerListView);

        // Add the components to the VBox
        getChildren().addAll(playerListView, readyButton);

        // Ensure that the scheduler is closed if the scene is switched
        lobbyController.getScene().getWindow().setOnCloseRequest(event -> lobbyController.stopLobbyPolling());
    }
}
