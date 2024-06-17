package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyBrowserController;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

public class LobbyBrowserView extends BaseView {

    private AppController appController;
    private LobbyBrowserController lobbyBrowserController;

    private ListView<String> lobbyListView;

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

        // List view for lobbies
        Text foundLobbiesHeader = new Text("Lobbies:");
        Button joinLobbyButton = new Button("Join");
        joinLobbyButton.setOnAction(event -> {
            String selectedGameName = lobbyListView.getSelectionModel().getSelectedItem();
            lobbyBrowserController.joinSelectedLobby(selectedGameName);
        });

        // Layout for selecting local lobby
        VBox localLobbies = new VBox(10, foundLobbiesHeader, lobbyListView, joinLobbyButton);

        // Add a button to go back
        Button backButton = new Button("Back");
        backButton.setOnAction(event -> appController.getRoboRally().createStartView(appController));

        // Spacer to push buttons to each side
        Region spacer = new Region();
        HBox.setHgrow(spacer, Priority.ALWAYS);

        // Add the buttons and spacer to a container
        HBox buttonContainer = new HBox(backButton, spacer, joinLobbyButton);
        buttonContainer.setAlignment(Pos.CENTER);

        // Main layout
        VBox mainLayout = new VBox(20, localLobbies, buttonContainer);

        // Add the components to the VBox
        getChildren().addAll(mainLayout);
    }

    // Clean up resources when no longer in use
    public void dispose() {
        lobbyBrowserController.stopLobbyPolling();
    }
}
