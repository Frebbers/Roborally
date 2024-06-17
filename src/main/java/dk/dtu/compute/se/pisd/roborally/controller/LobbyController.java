package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.PlayerState;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.ListView;

import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LobbyController {

    private ScheduledExecutorService scheduler;
    private AppController appController;

    public LobbyController(AppController appController) {
        this.appController = appController;
    }

    public void startLobbyPolling(Long gameId, ListView<String> playerListView) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            Game game = updateLobby(playerListView, gameId);
            if (game != null && allPlayersReady(game)) {
                stopLobbyPolling();
                appController.loadGameScene(gameId, game.boardId);
            }
        }), 0, 1000, TimeUnit.MILLISECONDS);
    }

    public void stopLobbyPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    public Game updateLobby(ListView<String> listView, Long gameId) {
        // Get the API Services from the AppController
        ApiServices apiServices = appController.getApiServices();

        Game game = apiServices.getGameById(gameId);
        List<PlayerDTO> playerList = apiServices.getPlayersInGame(game.id);
        ObservableList<String> items = FXCollections.observableArrayList();

        int readyPlayers = 0;
        for (PlayerDTO player : playerList) {
            if (player.getGameId().equals(game.id)) {
                items.add(player.getId() + " " + player.getName() + " " + player.getState());

                if (player.getState() == PlayerState.READY) {
                    readyPlayers += 1;
                }
            }
        }

        listView.setItems(items);
        return readyPlayers == game.maxPlayers ? game : null;
    }

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
