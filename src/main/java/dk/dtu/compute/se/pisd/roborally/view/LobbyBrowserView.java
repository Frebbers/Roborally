package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class LobbyBrowserView extends BaseView {
    private AppController appController;
    private ApiServices apiServices;

    public LobbyBrowserView(AppController appController) {
        super();
        this.appController = appController;
        this.apiServices = appController.getApiServices();
    }

    @Override
    public void initialize(){
        // Fetch list of games
        List<Long> listOfGames = apiServices.getAllGameIds();

        // Header for joining lobby via IP
        Text joinLobbyHeader = new Text("Join a lobby");
        TextField lobbyIPDialog = new TextField("Enter lobby IP");
        Button joinLobbyButton = new Button("Join via IP");
        joinLobbyButton.setOnAction(event -> {
            String ip = lobbyIPDialog.getText();
            //appController.joinLobbyByIP(ip);
            System.out.println("Joining IP Address: " + ip);
        });

        // Layout for IP based lobby joining
        VBox searchLobbyInput = new VBox(10, lobbyIPDialog, joinLobbyButton);

        // List view for local lobbies
        Text foundLobbiesHeader = new Text("Local lobbies found:");
        ListView<Long> listView = new ListView<>();
        Button joinLocalLobbyButton = new Button("Join Selected Lobby");
        joinLocalLobbyButton.setOnAction(event -> {
            Long selectedGameId = listView.getSelectionModel().getSelectedItem();
            if (selectedGameId != null) {
                appController.joinLobby(selectedGameId);
            } else {
                System.out.println("No game selected.");
            }
        });

        // Populate the list view
        if (listOfGames == null || listOfGames.isEmpty()) {
            Text noGames = new Text("No games available to join.");
            listView.setPlaceholder(noGames);
        } else {
            listView.getItems().addAll(listOfGames);
        }

        // Layout for selecting local lobby
        VBox localLobbies = new VBox(10, foundLobbiesHeader, listView, joinLocalLobbyButton);

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
}
