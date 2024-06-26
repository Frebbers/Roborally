package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.GameState;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.PlayerState;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;
import javafx.scene.text.Text;

import java.awt.*;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LobbyController {

    private ScheduledExecutorService scheduler;
    private AppController appController;
    private ApiServices apiServices;

    public LobbyController(AppController appController) {
        this.appController = appController;
        this.apiServices = appController.getApiServices();
    }

    /**
     * Starts polling the lobby for updates at a fixed rate.
     * Updates the lobby view and transitions to the game scene if all players are ready.
     *
     * @param gameId the ID of the game.
     * @param players a Text object to display the number of players.
     * @param playerListView a ListView to display the list of players.
     */
    public void startLobbyPolling(Long gameId, Text players, ListView<String> playerListView) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            Game game = updateLobby(playerListView, players, gameId);
            if (game != null && allPlayersReady(game)) {
                // Stop polling for lobby updates
                stopLobbyPolling();

                // Get the local player
                Long localPlayerId = AppController.localPlayer.getId();

                // Update the game state if the local player is the host
                if(apiServices.isPlayerHost(gameId, localPlayerId)){
                    apiServices.updateGameState(gameId, GameState.IN_PROGRESS);
                }

                // Load the Game Scene
                appController.loadGameScene(gameId, game.boardId);
            }
        }), 0, 1000, TimeUnit.MILLISECONDS);
    }

    /**
     * Stops polling the lobby for updates.
     */
    public void stopLobbyPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    /**
     * Updates the lobby view with the current players and their statuses.
     *
     * @param listView the ListView to display the list of players.
     * @param players a Text object to display the number of players.
     * @param gameId the ID of the game.
     * @return the Game object if all players are ready, otherwise null.
     */
    public Game updateLobby(ListView<String> listView, Text players, Long gameId) {
        // Get the API Services from the AppController
        ApiServices apiServices = appController.getApiServices();

        // Get the Game and a player list from the API Services
        Game game = apiServices.getGameById(gameId);
        List<PlayerDTO> playerList = apiServices.getPlayersInGame(game.id);
        ObservableList<String> items = FXCollections.observableArrayList();

        // Update the players in game
        players.setText(playerList.size() + " / " + game.maxPlayers + " players");

        int readyPlayers = 0;
        for (PlayerDTO player : playerList) {
            if (player.getGameId().equals(game.id)) {
                items.add(player.getName() + " (" + player.getState().toString() + ")");

                if (player.getState() == PlayerState.READY) {
                    readyPlayers += 1;
                }
            }
        }

        listView.setItems(items);
        return readyPlayers == game.maxPlayers ? game : null;
    }

    /**
     * Checks if all players in the specified game are ready.
     *
     * @param game the Game object to check.
     * @return true if all players are ready, otherwise false.
     */
    private boolean allPlayersReady(Game game) {
        // Get the API Services from the AppController
        ApiServices apiServices = appController.getApiServices();

        List<PlayerDTO> playerList = apiServices.getPlayersInGame(game.id);
        for (PlayerDTO player : playerList) {
            if (player.getState() != PlayerState.READY) {
                return false;
            }
        }
        return true;
    }
}
