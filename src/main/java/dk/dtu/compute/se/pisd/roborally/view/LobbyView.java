package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyBrowserController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyController;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.control.ListView;
import javafx.scene.control.Button;

public class LobbyView extends BaseView {

    private ListView<String> playerListView;
    private LobbyController lobbyController;
    private AppController appController;
    private Long gameId;

    public LobbyView(AppController appController, Long gameId) {
        super();
        this.appController = appController;
        this.gameId = gameId;
        this.lobbyController = new LobbyController(appController);
    }

    @Override
    public void initialize(){
        setupLobbyView();
    }

    public void setupLobbyView() {
        playerListView = new ListView<>();

        // Initial population of the ListView
        lobbyController.updateLobby(playerListView, gameId);

        // Set up a button to leave the lobby
        Button leaveButton = new Button("Leave");
        leaveButton.setOnAction(event -> appController.leave());

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

        // Start polling to refresh the list periodically
        lobbyController.startLobbyPolling(gameId, playerListView);

        // Add the components to the VBox
        getChildren().addAll(playerListView, buttonContainer);

        // Ensure that the scheduler is closed if the scene is switched
        appController.getRoboRally().getActiveScene().getWindow().setOnCloseRequest(event -> lobbyController.stopLobbyPolling());
    }
}
