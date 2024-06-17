package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Game;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LobbyBrowserController {
    private ScheduledExecutorService scheduler;
    private AppController appController;
    private Map<String, Long> lobbyMap;

    public LobbyBrowserController(AppController appController) {
        this.appController = appController;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
        this.lobbyMap = new HashMap<>();
    }

    public void startLobbyPolling(ListView<String> lobbyListView) {
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            updateLobbies(lobbyListView);
        }), 0, 2000, TimeUnit.MILLISECONDS);
    }

    public void stopLobbyPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void updateLobbies(ListView<String> lobbyListView) {
        List<Game> games = appController.getApiServices().getAllGames();
        ObservableList<String> items = FXCollections.observableArrayList();

        // Clear the previous entries in the map
        lobbyMap.clear();

        for (Game game : games) {
            items.add(game.name);
            lobbyMap.put(game.name, game.id);
        }

        lobbyListView.setItems(items);

        if (games.isEmpty()) {
            lobbyListView.setPlaceholder(new javafx.scene.text.Text("No games available to join."));
        }
    }

    public void joinLobbyByIP(String ip) {
        System.out.println("Joining IP Address: " + ip);
        // Additional logic to join lobby by IP could be implemented here
    }

    public void joinSelectedLobby(String gameName) {
        Long gameId = lobbyMap.get(gameName);
        if (gameId != null) {
            appController.joinLobby(gameId);
        } else {
            System.out.println("No game selected.");
        }
    }
}
