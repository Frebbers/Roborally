package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
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
                //updateLobbies(lobbyListView);
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

    /**
     * Test connection to and, if reachable, set API IP to the given IP address.
     *
     * @param ip IP-address to connect to
     * @return true if successfully connected, false otherwise
     */
    public boolean connectToServer(String ip) {
        System.out.println("Joining IP Address: " + ip);

        if (apiServices.testConnection(ip)) {
            AppConfig.setProperty("server.base.url", "http://" + ip + ":8080/api");
            AppConfig.setProperty("server.games.url", "http://" + ip + ":8080/api/games");
            AppConfig.setProperty("server.moves.url", "http://" + ip + ":8080/api/moves");
            AppConfig.setProperty("server.players.url", "http://" + ip + ":8080/api/players");

            apiServices.updateURLs();

            System.out.println("Successfully connected to server " + ip);
            return true;
        }
        else {
            System.out.println("Failed to connect to server " + ip);
            return false;
        }
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
