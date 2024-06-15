package dk.dtu.compute.se.pisd.roborally.controller;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.ListView;
import java.util.List;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

public class LobbyBrowserController {
    private ScheduledExecutorService scheduler;
    private AppController appController;

    public LobbyBrowserController(AppController appController) {
        this.appController = appController;
        this.scheduler = Executors.newSingleThreadScheduledExecutor();
    }

    public void startLobbyPolling(ListView lobbyListView) {
        scheduler.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            updateLobbies(lobbyListView);
        }), 0, 2000, TimeUnit.MILLISECONDS);
    }

    public void stopLobbyPolling() {
        if (scheduler != null && !scheduler.isShutdown()) {
            scheduler.shutdown();
        }
    }

    private void updateLobbies(ListView lobbyListView) {
        List<Long> listOfGames = appController.getApiServices().getAllGameIds();
        ObservableList<Long> items = FXCollections.observableArrayList(listOfGames);
        lobbyListView.setItems(items);
        if (listOfGames.isEmpty()) {
            lobbyListView.setPlaceholder(new javafx.scene.text.Text("No games available to join."));
        }
    }

    public void joinLobbyByIP(String ip) {
        System.out.println("Joining IP Address: " + ip);
        // Additional logic to join lobby by IP could be implemented here
    }

    public void joinSelectedLobby(Long gameId) {
        if (gameId != null) {
            appController.joinLobby(gameId);
        } else {
            System.out.println("No game selected.");
        }
    }
}
