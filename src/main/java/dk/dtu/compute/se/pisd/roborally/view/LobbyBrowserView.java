package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyBrowserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LobbyBrowserView extends BaseView {

    private AppController appController;
    private LobbyBrowserController lobbyBrowserController;

    private ListView<Long> lobbyListView;

    public LobbyBrowserView(AppController appController) {
        super();
        this.appController = appController;
        this.lobbyBrowserController = new LobbyBrowserController(appController);
    }

    @Override
    public void initialize() {
        lobbyListView = new ListView<>();

        // Fetch and observe the list of games
        lobbyBrowserController.startLobbyPolling(lobbyListView);

        // Header for joining lobby via IP
        Text joinLobbyHeader = new Text("Join a lobby");
        TextField lobbyIPDialog = new TextField("Enter lobby IP");
        Button joinLobbyButton = new Button("Join via IP");
        joinLobbyButton.setOnAction(event -> {
            String ip = lobbyIPDialog.getText();
            lobbyBrowserController.joinLobbyByIP(ip);
        });

        // Layout for IP based lobby joining
        VBox searchLobbyInput = new VBox(10, lobbyIPDialog, joinLobbyButton);

        // List view for local lobbies
        Text foundLobbiesHeader = new Text("Local lobbies found:");
        Button joinLocalLobbyButton = new Button("Join Selected Lobby");
        joinLocalLobbyButton.setOnAction(event -> {
            Long selectedGameId = lobbyListView.getSelectionModel().getSelectedItem();
            lobbyBrowserController.joinSelectedLobby(selectedGameId);
        });

        // Layout for selecting local lobby
        VBox localLobbies = new VBox(10, foundLobbiesHeader, lobbyListView, joinLocalLobbyButton);

        // Add a button to go back
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> appController.getRoboRally().createStartView(appController));
        HBox bottomContainer = new HBox(backButton);
        bottomContainer.setAlignment(Pos.BOTTOM_LEFT);
        bottomContainer.setPadding(new Insets(10));

        // Main layout
        VBox mainLayout = new VBox(20, joinLobbyHeader, searchLobbyInput, localLobbies, backButton);

        // Add the components to the VBox
        getChildren().addAll(mainLayout);
    }

    // Clean up resources when no longer in use
    public void dispose() {
        lobbyBrowserController.stopLobbyPolling();
    }
}
