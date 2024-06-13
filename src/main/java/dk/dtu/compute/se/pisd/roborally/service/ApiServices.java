package dk.dtu.compute.se.pisd.roborally.service;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.LobbyPlayer;
import dk.dtu.compute.se.pisd.roborally.model.PlayerState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ApiServices {
    //private static final String GAMES_URL = "http://localhost:8080/api/games";
    //private static final String PLAYERS_URL = "http://localhost:8080/api/players";
    private static final String BASE_URL = AppConfig.getProperty("base.url");
    private static final String GAMES_URL = BASE_URL + "/games";
    private static final String PLAYERS_URL = BASE_URL + "/players";

    public static LobbyPlayer localPlayer;

    private static final RestTemplate restTemplate = new RestTemplate();

    public static List<Game> getAllGames() {
        ResponseEntity<Game[]> response = restTemplate.getForEntity(GAMES_URL, Game[].class);
        return Arrays.asList(response.getBody());
    }

    public static List<LobbyPlayer> getAllPlayers() {
        ResponseEntity<LobbyPlayer[]> response = restTemplate.getForEntity(PLAYERS_URL, LobbyPlayer[].class);
        return Arrays.asList(response.getBody());
    }

    public static List<Integer> getAllPlayerIds() {
        List<LobbyPlayer> playerList = getAllPlayers();
        List<Integer> playerIds = new ArrayList<>();
        for (LobbyPlayer player : playerList) {
            playerIds.add(player.id.intValue());
        }
        return playerIds;
    }

    public static List<Integer> getAllGameIds() {
        List<Game> gameList = getAllGames();
        List<Integer> gameIds = new ArrayList<>();
        for (Game game : gameList) {
            gameIds.add(game.id.intValue());
        }
        return gameIds;
    }

    public static Game getGameById(Long gameId) {
        String url = GAMES_URL + "/" + gameId;
        ResponseEntity<Game> response = restTemplate.getForEntity(url, Game.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public static Game createGame(Long boardId, int maxPlayers) {
        Game game = new Game();
        game.boardId = boardId;
        game.maxPlayers = maxPlayers;

        ResponseEntity<Game> response = restTemplate.postForEntity(GAMES_URL, game, Game.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public static String joinGame(Long gameId, Long playerId) {
        Game game = getGameById(gameId);
        if (game != null) {
            game.playerIds.add(playerId);

            String gameUrl = GAMES_URL + "/" + gameId;
            try {
                restTemplate.put(gameUrl, game);
            } catch (Exception e) {
                return "Error updating game: " + e.getMessage();
            }

            LobbyPlayer player = getPlayerById(playerId);
            if (player != null) {
                player.gameId = gameId;
                player.state = PlayerState.NOT_READY;
                String playerUrl = PLAYERS_URL + "/" + playerId;
                try {
                    restTemplate.put(playerUrl, player); // Update player with new gameId
                } catch (Exception e) {
                    return "Error updating player: " + e.getMessage();
                }

                return "Player joined game successfully";
            } else {
                return "Player not found";
            }
        } else {
            return "Error joining game";
        }
    }

    public static List<LobbyPlayer> getPlayersInGame(Long gameid){
        String lobbyUrl = GAMES_URL + "/" + gameid + "/players";
        ResponseEntity<LobbyPlayer[]> response = restTemplate.getForEntity(lobbyUrl, LobbyPlayer[].class);
        return response.getStatusCode() == HttpStatus.OK ? Arrays.asList(Objects.requireNonNull(response.getBody())) : null;
    }

    public static LobbyPlayer createPlayer(String name){
        LobbyPlayer player = new LobbyPlayer();
        player.name = name;
        ResponseEntity<LobbyPlayer> response = restTemplate.postForEntity(PLAYERS_URL, player, LobbyPlayer.class);
        localPlayer = response.getBody();
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public static String updatePlayerState(Long id){
        LobbyPlayer player = getPlayerById(id);
        if (player != null) {
            player.state = player.state == PlayerState.READY ? PlayerState.NOT_READY : PlayerState.READY;

            String playerUrl = PLAYERS_URL + "/" + player.id;
            try {
                restTemplate.put(playerUrl, player); // Update player with new gameId
            } catch (Exception e) {
                return "Error updating player: " + e.getMessage();
            }
            return "Player state updated successfully.";
        }
        return "Player does not exist.";
    }

    public static LobbyPlayer getPlayerById(Long playerId) {
        String url = PLAYERS_URL + "/" + playerId;
        ResponseEntity<LobbyPlayer> response = restTemplate.getForEntity(url, LobbyPlayer.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }
}
