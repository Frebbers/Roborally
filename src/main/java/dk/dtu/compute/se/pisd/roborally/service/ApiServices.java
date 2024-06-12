package dk.dtu.compute.se.pisd.roborally.service;

import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.LobbyPlayer;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiServices {
    private static final String GAMES_URL = "http://localhost:8080/api/games";
    private static final String PLAYERS_URL = "http://localhost:8080/api/players";

    private final RestTemplate restTemplate;

    public ApiServices() {
        this.restTemplate = new RestTemplate();
    }

    public List<Game> getAllGames() {
        ResponseEntity<Game[]> response = restTemplate.getForEntity(GAMES_URL, Game[].class);
        return Arrays.asList(response.getBody());
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

    public LobbyPlayer createPlayer(String name){
        LobbyPlayer player = new LobbyPlayer();
        player.name = name;

        ResponseEntity<LobbyPlayer> response = restTemplate.postForEntity(PLAYERS_URL, player, LobbyPlayer.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public LobbyPlayer getPlayerById(Long playerId) {
        String url = PLAYERS_URL + "/" + playerId;
        ResponseEntity<LobbyPlayer> response = restTemplate.getForEntity(url, LobbyPlayer.class);
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
                player.state = "not_ready";
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

}
