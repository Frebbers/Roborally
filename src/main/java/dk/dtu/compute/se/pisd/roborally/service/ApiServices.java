package dk.dtu.compute.se.pisd.roborally.service;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.model.ApiType;
import dk.dtu.compute.se.pisd.roborally.model.DTO.MoveDTO;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import dk.dtu.compute.se.pisd.roborally.model.PlayerState;
import dk.dtu.compute.se.pisd.roborally.model.RobotType;
import dk.dtu.compute.se.pisd.roborally.util.Utilities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Objects;

import static dk.dtu.compute.se.pisd.roborally.config.AppConfig.getProperty;

public class ApiServices {
    private String BASE_URL;
    private String GAMES_URL;
    private String PLAYERS_URL;
    private String MOVES_URL;
    private AppController appcontroller;

    private final RestTemplate restTemplate = new RestTemplate();
    //LocalPlayer is stored in AppController now
   // private PlayerDTO localPlayer;

    public ApiServices(AppController appcontroller) {
        this.appcontroller = appcontroller;
        updateURLs();
    }

    /**
     * Update the local private fields {@link #BASE_URL}, {@link #GAMES_URL},
     * {@link #PLAYERS_URL} and {@link #MOVES_URL} to the values of their
     * respective properties in {@code resources/application.properties}.
     */
    public void updateURLs() {
        ApiType type = Utilities.toEnum(ApiType.class, getProperty("api.type"));

        if(type == ApiType.LOCAL){
            BASE_URL = getProperty("local.base.url");
            GAMES_URL = getProperty("local.games.url");
            PLAYERS_URL = getProperty("local.players.url");
            MOVES_URL = getProperty("local.moves.url");
        }
        else if(type == ApiType.SERVER){
            BASE_URL = getProperty("server.base.url");
            GAMES_URL = getProperty("server.games.url");
            PLAYERS_URL = getProperty("server.players.url");
            MOVES_URL = getProperty("server.moves.url");
        }
    }

    public List<Game> getAllGames() {
        ResponseEntity<Game[]> response = restTemplate.getForEntity(GAMES_URL, Game[].class);
        return Arrays.asList(response.getBody());
    }

    public List<PlayerDTO> getAllPlayers() {
        ResponseEntity<PlayerDTO[]> response = restTemplate.getForEntity(PLAYERS_URL, PlayerDTO[].class);
        return Arrays.asList(response.getBody());
    }

    public List<Long> getAllPlayerIds() {
        List<PlayerDTO> playerList = getAllPlayers();
        List<Long> playerIds = new ArrayList<>();
        for (PlayerDTO player : playerList) {
            playerIds.add(player.getId());
        }
        return playerIds;
    }

    public List<Long> getAllGameIds() {
        List<Game> gameList = getAllGames();
        List<Long> gameIds = new ArrayList<>();
        for (Game game : gameList) {
            gameIds.add(game.id);
        }
        return gameIds;
    }

    public Game getGameById(Long gameId) {
        String url = GAMES_URL + "/" + gameId;
        ResponseEntity<Game> response = restTemplate.getForEntity(url, Game.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public Game createGame(String name, Long boardId, int maxPlayers) {
        Game game = new Game();
        game.name = name;
        game.boardId = boardId;
        game.maxPlayers = maxPlayers;

        ResponseEntity<Game> response = restTemplate.postForEntity(GAMES_URL, game, Game.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public Game joinGame(Long gameId, Long playerId) {
        Game game = getGameById(gameId);
        if (game != null) {
            game.playerIds.add(playerId);

            String gameUrl = GAMES_URL + "/" + gameId;
            try {
                restTemplate.put(gameUrl, game);
            } catch (Exception e) {
                throw new RuntimeException("Error updating game: " + e.getMessage());
            }

            PlayerDTO player = getPlayerById(playerId);

            if (player != null) {
                player.setGameId(gameId);
                player.setState(PlayerState.NOT_READY);
                String playerUrl = PLAYERS_URL + "/" + playerId;
                try {
                    restTemplate.put(playerUrl, player); // Update player with new gameId
                } catch (Exception e) {
                    throw new RuntimeException("Error updating player: " + e.getMessage());
                }

                return game; // Return the game object after successfully joining the player
            } else {
                throw new RuntimeException("Player not found");
            }
        } else {
            throw new RuntimeException("Error joining game");
        }
    }

    public List<PlayerDTO> getPlayersInGame(Long gameId){
        String lobbyUrl = GAMES_URL + "/" + gameId + "/players";
        ResponseEntity<PlayerDTO[]> response = restTemplate.getForEntity(lobbyUrl, PlayerDTO[].class);
        return response.getStatusCode() == HttpStatus.OK ? Arrays.asList(Objects.requireNonNull(response.getBody())) : null;
    }

    public PlayerDTO createPlayer(String name){
        // Create a new player on the client and set the name (Do not create a constructor for this)
        PlayerDTO player = new PlayerDTO();
        player.setName(name);
        player.setState(PlayerState.NOT_IN_LOBBY);
        player.setGameId(0L);

        // Set the Robot Type of the Player
        appcontroller.loadPlayerProperties();

        // Upload the player to the server
        ResponseEntity<PlayerDTO> response;
        try {
            response = restTemplate.postForEntity(PLAYERS_URL, player, PlayerDTO.class);
        } catch (Exception e) {
            return null;
        }
        return response.getStatusCode() == HttpStatus.OK ? response.getBody(): null;
    }

    //public boolean createPlayerOnServer(PlayerDTO playerDTO){
     //   return ((restTemplate.postForEntity(PLAYERS_URL, playerDTO, PlayerDTO.class)).getStatusCode() == HttpStatus.OK);
    //}
    /**
     * Check if a player with the given name and ID already exists and return a new PlayerDTO object if it does
     * @param name     name of the player
     * @param playerID ID of the player
     * @return null if the player does not exist, otherwise a new PlayerDTO object
     */
    public PlayerDTO playerExists(String name, String playerID){
        List<PlayerDTO> players = getAllPlayers();
        for (PlayerDTO player : players) {
            if (player.getName().equals(name)) {
                return new PlayerDTO(Long.parseLong(playerID));
            }
        }
        return null;
    }

    public void updatePlayerInteractionState(Long id) {
        PlayerDTO player = getPlayerById(id);
        if (player != null) {
                player.setState(PlayerState.INTERACTING);
                String playerUrl = PLAYERS_URL + "/" + player.getId();
                try {
                    restTemplate.put(playerUrl, player);
                } catch (Exception e) {

                }
            }
        }

    public void updatePlayerState(Long id) {
        PlayerDTO player = getPlayerById(id);
        if (player != null) {
            if (player.getState() == PlayerState.READY) {
                player.setState(PlayerState.INTERACTING);
                String playerUrl = PLAYERS_URL + "/" + player.getId();
                try {
                    restTemplate.put(playerUrl, player);
                } catch (Exception e) {

                }
            } else if (player.getState() == PlayerState.NOT_IN_LOBBY) {
                player.setState(PlayerState.NOT_READY);
            } else {
                player.setState(player.getState() == PlayerState.READY ? PlayerState.NOT_READY : PlayerState.READY);
                String playerUrl = PLAYERS_URL + "/" + player.getId();
                try {
                    restTemplate.put(playerUrl, player);
                } catch (Exception e) {
                    e.getMessage();
                }
            }
        }
    }

    public PlayerDTO getPlayerById(Long playerId) {
        String url = PLAYERS_URL + "/" + playerId;
        ResponseEntity<PlayerDTO> response = restTemplate.getForEntity(url, PlayerDTO.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public void onPlayerLeave(Long playerId) {
        ResponseEntity<Void> response = null;
        String url = PLAYERS_URL + "/" + playerId + "/leave";
        try {
            response = restTemplate.getForEntity(url, Void.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                AppController.localPlayer = null;
        }
        } catch (Exception e) {
            System.err.println("Failed to leave the game. " + e.getMessage());
            if (response != null) System.out.println("HTTP response code: " + response.getStatusCode());
        }
    }

    public Integer getPlayerReadyCount(Long gameId, Integer turnIndex){
        String url = MOVES_URL + "/game/" + gameId + "/turn/" + turnIndex + "/player-count";
        ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public MoveDTO createMove(Long gameId, Long playerId, Integer turnIndex, List<String> moveTypes){
        // Create a new move on the client and fill the information (Do not create a constructor for this)
        MoveDTO move = new MoveDTO();
        move.setGameId(gameId);
        move.setPlayerId(playerId);
        move.setMoveTypes(moveTypes);
        move.setTurnIndex(turnIndex);

        // Upload the move to the server
        ResponseEntity<MoveDTO> response = restTemplate.postForEntity(MOVES_URL, move, MoveDTO.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public List<MoveDTO> getAllMoves(Long gameId, Integer turnIndex){
        String url = MOVES_URL + "/game/" + gameId + "/turn/" + turnIndex;
        ResponseEntity<MoveDTO[]> response = restTemplate.getForEntity(url, MoveDTO[].class);
        return response.getStatusCode() == HttpStatus.OK ? Arrays.asList(Objects.requireNonNull(response.getBody())) : null;
    }

    public MoveDTO getPlayerMoves(Long gameId, Long playerId, int turnIndex){
        String url = MOVES_URL + "/game/" + gameId + "/player/" + playerId + "/turn/ " + turnIndex;
        ResponseEntity<MoveDTO> response = restTemplate.getForEntity(url, MoveDTO.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public void updateMoves(Long gameId, Long playerId, int turnIndex, List<String> moveTypes) {
        String url = MOVES_URL + "/update";
        MoveDTO oldMove = getPlayerMoves(gameId, playerId, turnIndex);
        if (oldMove == null) {
            throw new RuntimeException("Moves not found");
        }
        oldMove.setMoveTypes(moveTypes);
        restTemplate.put(url, oldMove);
    }

    public boolean isReachable(){
        try {
            getAllGames();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Determine whether the given IP address is responsive with a
     * working API.
     *
     * @param ip IP-address to be tested
     * @return true if connection succeeded, false otherwise
     */
    public boolean testConnection(String ip) {
        GAMES_URL = "http://" + ip + ":8080/api/games";

        boolean verdict = isReachable();
        updateURLs();
        return verdict;
    }

    /**
     * @return private field {@link #BASE_URL}
     */
    public String getBASE_URL() {
        return BASE_URL;
    }

    /**
     * @return private field {@link #BASE_URL}
     */
    public String getGAMES_URL() {
        return GAMES_URL;
    }

    /**
     * @return private field {@link #BASE_URL}
     */
    public String getPLAYERS_URL() {
        return PLAYERS_URL;
    }

    /**
     * @return private field {@link #BASE_URL}
     */
    public String getMOVES_URL() {
        return MOVES_URL;
    }
}
