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

        List<Long> listOfGames = apiServices.getAllGameIds();
        Text joinLobbyHeader = new Text("Join a lobby");

        Text searchForLobby  = new Text("Search for a lobby");
        TextField lobbyIPDialog = new TextField();
        Button joinLobbyButton = new Button("Join lobby");
        VBox searchLobbyInput = new VBox(10, lobbyIPDialog, joinLobbyButton);

        // Event handler on the button to get IPDialog value
        joinLobbyButton.setOnAction(event -> {
            String IPCall = lobbyIPDialog.getText();
            System.out.println("IP Address: " + IPCall);
        });

        VBox searchLobby = new VBox (joinLobbyHeader, searchForLobby, searchLobbyInput);

        Text foundLobbiesHeader = new Text("Local lobbies found:");
        ListView<Long> listView = new ListView<>();

        if (listOfGames == null || listOfGames.isEmpty()) {
            Text noGames = new Text("Searching..");
            System.out.println("No games available to join.");
            return;
        } else {
            listView.getItems().addAll(listOfGames);
        }
        listView.setOnMouseClicked(event -> {
            Long selectedGameId = listView.getSelectionModel().getSelectedItem();
            if (selectedGameId != null) {
                appController.joinLobby(selectedGameId);
            }
        });

        Button joinLocalLobbyButton = new Button("Join lobby");
        joinLocalLobbyButton.setOnAction(event -> {


            // Populate the ListView with the game IDs
            listView.getItems().clear();
        });
    }

}
