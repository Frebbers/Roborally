package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.LobbyPlayer;
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
    private Scene scene;

    public LobbyController(AppController appController) {
        this.appController = appController;
        this.scene = appController.getRoboRally().getStage().getScene();
    }

    public void startLobbyPolling(Long gameId, ListView<String> playerListView) {
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            Game game = updateLobby(playerListView, gameId);
            if (game != null && allPlayersReady(game)) {
                stopLobbyPolling();
                appController.loadGameScene(game.boardId, game.maxPlayers);
            }
        }), 0, 500, TimeUnit.MILLISECONDS);
    }

    public void stopLobbyPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    public Game updateLobby(ListView<String> listView, Long gameId) {
        Game game = ApiServices.getGameById(gameId);
        List<LobbyPlayer> playerList = ApiServices.getPlayersInGame(game.id);
        ObservableList<String> items = FXCollections.observableArrayList();

        int readyPlayers = 0;
        for (LobbyPlayer player : playerList) {
            if (player.gameId.equals(game.id)) {
                items.add(player.id + " " + player.name + " " + player.state);
                if (player.state == PlayerState.READY) {
                    readyPlayers += 1;
                }
            }
        }

        listView.setItems(items);
        return readyPlayers == game.maxPlayers ? game : null;
    }

    private boolean allPlayersReady(Game game) {
        List<LobbyPlayer> playerList = ApiServices.getPlayersInGame(game.id);
        for (LobbyPlayer player : playerList) {
            if (player.state != PlayerState.READY) {
                return false;
            }
        }
        return true;
    }

    public Scene getScene() {
        return scene;
    }
}
