package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.model.ApiType;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.GameState;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.Alert;
import javafx.scene.control.ListView;

import javax.swing.*;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LobbyBrowserController {
    private ScheduledExecutorService scheduler;
    private AppController appController;
    private ApiServices apiServices;
    private Map<String, Long> lobbyMap;

    public LobbyBrowserController(AppController appController) {
        this.appController = appController;
        this.apiServices = appController.getApiServices();
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.lobbyMap = new HashMap<>();
    }

    public void startLobbyPolling(ListView<String> lobbyListView) {

            scheduler.scheduleAtFixedRate(() -> Platform.runLater(() -> {
                if (apiServices.isReachable()) {
                    updateLobbies(lobbyListView);
                }
            }), 0, 2000, TimeUnit.MILLISECONDS);

    }

    public void stopLobbyPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    public void updateLobbies(ListView<String> lobbyListView) {
        List<Game> games = appController.getApiServices().getAllGames();
        ObservableList<String> items = FXCollections.observableArrayList();

        // Clear the previous entries in the map
        lobbyMap.clear();

        for (Game game : games) {
            if(game.gameState == GameState.IN_PROGRESS || game.gameState == GameState.FINISHED) {
                continue;
            }

            // Get the list of players for each lobby
            List<PlayerDTO> players = apiServices.getPlayersInGame(game.id);

            // Display the name, players in the lobby and the max players
            String displayText = String.format("%s (%d / %d players)", game.name, players.size(), game.maxPlayers);
            items.add(displayText);
            lobbyMap.put(displayText, game.id);
        }

        lobbyListView.setItems(items);

        if (games.isEmpty()) {
            lobbyListView.setPlaceholder(new javafx.scene.text.Text("No games available to join."));
        }
    }

    public Game getGameByName(String gameName){
        // Get the gameID from the map
        Long gameId = lobbyMap.get(gameName);
        // Get the lobby from the API
        return apiServices.getGameById(gameId);
    }

    public boolean isLobbyFull(String gameName){
        // Get the gameID from the map
        Long gameId = lobbyMap.get(gameName);

        // Get the lobby from the API
        Game game = apiServices.getGameById(gameId);

        if(game != null){
            // Get the list of players for the lobby
            List<PlayerDTO> players = apiServices.getPlayersInGame(game.id);

            return players.size() >= game.maxPlayers;
        }
        return false;
    }

    public void joinSelectedLobby(String gameName) {
        appController.onLobbyJoin();
        Long gameId = lobbyMap.get(gameName);
        if (gameId != null) {
            PlayerDTO localPlayer = AppController.localPlayer;

            // Get the list of players for the lobby
            List<PlayerDTO> players = apiServices.getPlayersInGame(gameId);

            // Check if localPlayer is already in the list of players
            for (PlayerDTO player : players) {
                if (player.getId().equals(localPlayer.getId())) {
                    // Use Alert class to display that localPlayer is already in the game
                    Alert alert = new Alert(Alert.AlertType.INFORMATION, "You are already within this game");
                    alert.showAndWait();
                    return;
                }
            }

            // If localPlayer is not found in the list, join the lobby
            appController.joinLobby(gameId);
        } else {
            System.out.println("No game selected.");
        }
    }

}
