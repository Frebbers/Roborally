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
import javafx.scene.text.Text;

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

    /**
     * Starts polling for lobbies at a fixed interval.
     *
     * @param lobbyListView the ListView to display the lobbies
     */
    public void startLobbyPolling(ListView<String> lobbyListView) {
        if (apiServices.isReachable()) {
            scheduler.scheduleAtFixedRate(() -> Platform.runLater(() -> {
                updateLobbies(lobbyListView, true);

            }), 0, 2000, TimeUnit.MILLISECONDS);
        }
    }

    /**
     * Stops the lobby polling.
     */
    public void stopLobbyPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /**
     * Updates the lobbies displayed in the ListView.
     *
     * @param lobbyListView the ListView to update
     * @param serverIsReachable whether the server is reachable
     */
    public void updateLobbies(ListView<String> lobbyListView, boolean serverIsReachable) {
        List<Game> games = null;
        if (serverIsReachable) {
            games = appController.getApiServices().getAllGames();
            ObservableList<String> items = FXCollections.observableArrayList();

            // Clear the previous entries in the map
            lobbyMap.clear();

            for (Game game : games) {
                if (game.gameState == GameState.IN_PROGRESS || game.gameState == GameState.FINISHED) {
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
        }
        else{
            lobbyListView.setPlaceholder(new Text("You are not connected to a server."));
        }
        if ((games == null) || games.isEmpty()) {
            lobbyListView.setPlaceholder(new Text("No games available to join."));
        }
    }

    /**
     * Retrieves a game by its name.
     *
     * @param gameName the name of the game
     * @return the game with the specified name
     */
    public Game getGameByName(String gameName){
        // Get the gameID from the map
        Long gameId = lobbyMap.get(gameName);
        // Get the lobby from the API
        return apiServices.getGameById(gameId);
    }

    /**
     * Checks if a lobby is full.
     *
     * @param gameName the name of the game
     * @return true if the lobby is full, false otherwise
     */
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

    /**
     * Joins the selected lobby.
     *
     * @param gameName the name of the game to join
     */
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
