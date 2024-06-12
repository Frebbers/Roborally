package dk.dtu.compute.se.pisd.roborally.service;

import dk.dtu.compute.se.pisd.roborally.model.APIObjects.ApiObject;
import dk.dtu.compute.se.pisd.roborally.model.APIObjects.Game;
import dk.dtu.compute.se.pisd.roborally.model.APIObjects.LobbyPlayer;
import dk.dtu.compute.se.pisd.roborally.model.PlayerState;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

public abstract class ApiService {
    private final String GAMES_URL = "/api/games";
    private final String serverURL;
    private final String PLAYERS_URL = "/api/players";
    //private static final String PLAYERS_URL = "http://localhost:8080/api/players";
    //private static final String PLAYERS_URL = "http://localhost:8080/api/players";

    /**
     * Constructor for ApiService connecting to a server
     * @author s224804
     * @param IP IP address of the server
     * @param port Port of the server
     */
    public ApiService(String IP, String port) {
        this.serverURL = "http://" + IP + ":" + port;
    }
    /**
     * Constructor for ApiService running on localhost
     * @author s224804
     * @param endpointURL URL of the API endpoint, beginning with "/"
     */
    public ApiService(String endpointURL) {
        this.serverURL = "http://localhost:8080" + endpointURL;
    }
    public static LobbyPlayer localPlayer;

    private static final RestTemplate restTemplate = new RestTemplate();

    public <T extends ApiObject> List<T> getAllObjects(String url, Class<T[]> object) {
        String responseMessage = "No response received from server.";
        try {
            ResponseEntity<T[]> response = restTemplate.getForEntity((serverURL+url), object);
            if (response.getStatusCode()==HttpStatus.OK) {
                return Arrays.asList(response.getBody());
            }
            responseMessage = response.getStatusCode().toString();
        } catch (Exception e) {
            System.out.println("Error getting objects: " + e.getMessage());
            return null;
        }
        System.out.println("Error getting objects: " + responseMessage);
        return null;
    }
    public void postObject(ApiObject object){
        try {
            ResponseEntity<ApiObject> response = restTemplate.postForEntity(serverURL + object.getPath(), object, object.getClass());
        } catch (Exception e) {
            System.out.println("Error posting object: " + e.getMessage());
        }
    }

    public List<Game> getAllGames() {
        //ResponseEntity<Game[]> response = restTemplate.getForEntity(GAMES_URL, Game[].class);
        return getAllObjects(GAMES_URL, Game[].class);
    }

    public List<LobbyPlayer> getAllPlayers() {
        //ResponseEntity<LobbyPlayer[]> response = restTemplate.getForEntity(PLAYERS_URL, LobbyPlayer[].class);
        return getAllObjects(PLAYERS_URL, LobbyPlayer[].class);
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

    public Game createGame(Long gameId, Long boardId, int maxPlayers) {
        Game game = new Game(gameId, boardId,maxPlayers, GAMES_URL);
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
