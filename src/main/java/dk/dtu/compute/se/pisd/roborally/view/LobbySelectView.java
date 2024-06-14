package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.TextField;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;

import java.util.List;

public class LobbySelectView extends VBox {
    private AppController appController;
    private ApiServices apiServices;

    public LobbySelectView(AppController appController) {
        this.appController = appController;
        this.apiServices = appController.getApiServices();

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
                System.out.println("Joining Game ID: " + selectedGameId);
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

        // Main layout
        VBox mainLayout = new VBox(20, joinLobbyHeader, searchLobbyInput, localLobbies);

        // Add the components to the VBox
        getChildren().addAll(mainLayout);
    }
}