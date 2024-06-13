package dk.dtu.compute.se.pisd.roborally.service;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.model.ApiType;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.LobbyPlayer;
import dk.dtu.compute.se.pisd.roborally.model.PlayerState;
import dk.dtu.compute.se.pisd.roborally.util.Utilities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public class ApiServices {
    private String BASE_URL;
    private String GAMES_URL;
    private String PLAYERS_URL;

    public LobbyPlayer localPlayer;

    private final RestTemplate restTemplate = new RestTemplate();

    public ApiServices(){
        ApiType type = Utilities.toEnum(ApiType.class, AppConfig.getProperty("api.type"));

        if(type == ApiType.LOCAL){
            BASE_URL = AppConfig.getProperty("local.base.url");
            GAMES_URL = AppConfig.getProperty("local.games.url");
            PLAYERS_URL = AppConfig.getProperty("local.players.url");
        }
        else if(type == ApiType.SERVER){
            BASE_URL = AppConfig.getProperty("server.base.url");
            GAMES_URL = AppConfig.getProperty("server.games.url");
            PLAYERS_URL = AppConfig.getProperty("server.players.url");
        }
    }

    public List<Game> getAllGames() {
        ResponseEntity<Game[]> response = restTemplate.getForEntity(GAMES_URL, Game[].class);
        return Arrays.asList(response.getBody());
    }

    public List<LobbyPlayer> getAllPlayers() {
        ResponseEntity<LobbyPlayer[]> response = restTemplate.getForEntity(PLAYERS_URL, LobbyPlayer[].class);
        return Arrays.asList(response.getBody());
    }

    public List<Integer> getAllPlayerIds() {
        List<LobbyPlayer> playerList = getAllPlayers();
        List<Integer> playerIds = new ArrayList<>();
        for (LobbyPlayer player : playerList) {
            playerIds.add(player.id.intValue());
        }
        return playerIds;
    }

    public List<Integer> getAllGameIds() {
        List<Game> gameList = getAllGames();
        List<Integer> gameIds = new ArrayList<>();
        for (Game game : gameList) {
            gameIds.add(game.id.intValue());
        }
        return gameIds;
    }

    public Game getGameById(Long gameId) {
        String url = GAMES_URL + "/" + gameId;
        ResponseEntity<Game> response = restTemplate.getForEntity(url, Game.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public Game createGame(Long boardId, int maxPlayers) {
        Game game = new Game();
        game.boardId = boardId;
        game.maxPlayers = maxPlayers;

        ResponseEntity<Game> response = restTemplate.postForEntity(GAMES_URL, game, Game.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public String joinGame(Long gameId, Long playerId) {
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

    public List<LobbyPlayer> getPlayersInGame(Long gameid){
        String lobbyUrl = GAMES_URL + "/" + gameid + "/players";
        ResponseEntity<LobbyPlayer[]> response = restTemplate.getForEntity(lobbyUrl, LobbyPlayer[].class);
        return response.getStatusCode() == HttpStatus.OK ? Arrays.asList(Objects.requireNonNull(response.getBody())) : null;
    }

    public LobbyPlayer createPlayer(String name){
        // Create a new player on the client and set the name
        LobbyPlayer player = new LobbyPlayer();
        player.name = name;

        // Upload the player to the server
        ResponseEntity<LobbyPlayer> response = restTemplate.postForEntity(PLAYERS_URL, player, LobbyPlayer.class);

        // Update the ID of the client player with the ID from the server
        localPlayer = response.getBody();
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public String updatePlayerState(Long id){
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

    public LobbyPlayer getPlayerById(Long playerId) {
        String url = PLAYERS_URL + "/" + playerId;
        ResponseEntity<LobbyPlayer> response = restTemplate.getForEntity(url, LobbyPlayer.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }
}
