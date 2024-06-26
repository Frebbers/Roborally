package dk.dtu.compute.se.pisd.roborally.service;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.DTO.MoveDTO;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.util.Utilities;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.net.MalformedURLException;
import java.net.URL;
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

    /**
     * Fetches a list of all games from the API.
     *
     * @return a list of Game objects.
     */
    public List<Game> getAllGames() {
        ResponseEntity<Game[]> response = restTemplate.getForEntity(GAMES_URL, Game[].class);
        return Arrays.asList(response.getBody());
    }

    /**
     * Fetches a list of all players from the API.
     *
     * @return a list of PlayerDTO objects.
     */
    public List<PlayerDTO> getAllPlayers() {
        ResponseEntity<PlayerDTO[]> response = restTemplate.getForEntity(PLAYERS_URL, PlayerDTO[].class);
        return Arrays.asList(response.getBody());
    }

    /**
     * Fetches a game by its ID from the API.
     *
     * @param gameId the ID of the game to fetch.
     * @return the Game object if found, otherwise null.
     */
    public Game getGameById(Long gameId) {
        String url = GAMES_URL + "/" + gameId;
        ResponseEntity<Game> response = restTemplate.getForEntity(url, Game.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    /**
     * Creates a new game on the server.
     *
     * @param name       the name of the game.
     * @param boardId    the ID of the board to use.
     * @param maxPlayers the maximum number of players.
     * @return the created Game object if successful, otherwise null.
     */
    public Game createGame(String name, Long boardId, int maxPlayers) {
        Game game = new Game();
        game.name = name;
        game.gameState = GameState.LOBBY;
        game.boardId = boardId;
        game.maxPlayers = maxPlayers;

        ResponseEntity<Game> response = restTemplate.postForEntity(GAMES_URL, game, Game.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    /**
     * Adds a player to a game.
     *
     * @param gameId   the ID of the game to join.
     * @param playerId the ID of the player joining the game.
     * @return the updated Game object if successful.
     * @throws RuntimeException if the game or player is not found or an error occurs.
     */
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

    /**
     * Updates the state of a game.
     *
     * @param gameId   the ID of the game to update.
     * @param newState the new state to set.
     * @return true if the game state was successfully updated, otherwise false.
     */
    public boolean updateGameState(Long gameId, GameState newState) {
        String url = GAMES_URL + "/" + gameId;
        Game game = getGameById(gameId);
        if (game == null) {
            // Game not found, return false
            return false;
        }
        // Set the new state to the game object
        game.gameState = newState;

        try {
            // Send the updated game object to the server
            restTemplate.put(url, game);
            return true;
        } catch (Exception e) {
            System.err.println("Failed to update game state: " + e.getMessage());
            return false;
        }
    }

    /**
     * Fetches a list of players in a specific game.
     *
     * @param gameId the ID of the game.
     * @return a list of PlayerDTO objects.
     */
    public List<PlayerDTO> getPlayersInGame(Long gameId){
        String lobbyUrl = GAMES_URL + "/" + gameId + "/players";
        ResponseEntity<PlayerDTO[]> response = restTemplate.getForEntity(lobbyUrl, PlayerDTO[].class);
        return response.getStatusCode() == HttpStatus.OK ? Arrays.asList(Objects.requireNonNull(response.getBody())) : null;
    }

    /**
     * Creates a new player on the server.
     *
     * @param name the name of the player.
     * @return the created PlayerDTO object if successful, otherwise null.
     */
    public PlayerDTO createPlayer(String name){
        // Create a new player on the client and set the name
        PlayerDTO player = appcontroller.loadPlayerProperties();
        if (player == null) {
            player = new PlayerDTO();
            player.setName(name);
            player.setState(PlayerState.NOT_IN_LOBBY);
            player.setGameId(0L);
            player.setRobotType(RobotType.Circuito);
        }

        // Upload the player to the server
        ResponseEntity<PlayerDTO> response;
        try {
            response = restTemplate.postForEntity(PLAYERS_URL, player, PlayerDTO.class);
        } catch (Exception e) {
            return null;
        }
        return response.getStatusCode() == HttpStatus.OK ? response.getBody(): null;
    }

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

    /**
     * Updates the interaction state of a player to INTERACTING.
     *
     * @param id the ID of the player to update.
     */
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

    /**
     * Toggles the state of a player between READY and NOT_READY.
     *
     * @param id the ID of the player to update.
     */
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

    /**
     * Fetches a player by their ID from the API.
     *
     * @param playerId the ID of the player to fetch.
     * @return the PlayerDTO object if found, otherwise null.
     */
    public PlayerDTO getPlayerById(Long playerId) {
        String url = PLAYERS_URL + "/" + playerId;
        ResponseEntity<PlayerDTO> response = restTemplate.getForEntity(url, PlayerDTO.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    /**
     * Handles the player leaving a game.
     *
     * @param playerId the ID of the player leaving the game.
     */
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

    /**
     * Fetches the count of players ready for a specific turn in a game.
     *
     * @param gameId    the ID of the game.
     * @param turnIndex the turn index.
     * @return the count of ready players if successful, otherwise null.
     */
    public Integer getPlayerReadyCount(Long gameId, Integer turnIndex){
        String url = MOVES_URL + "/game/" + gameId + "/turn/" + turnIndex + "/player-count";
        ResponseEntity<Integer> response = restTemplate.getForEntity(url, Integer.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    /**
     * Creates a new move for a player in a game.
     *
     * @param gameId    the ID of the game.
     * @param playerId  the ID of the player.
     * @param turnIndex the turn index.
     * @param moveTypes the list of move types.
     * @return the created MoveDTO object if successful, otherwise null.
     */
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

    /**
     * Fetches all moves for a specific turn in a game.
     *
     * @param gameId    the ID of the game.
     * @param turnIndex the turn index.
     * @return a list of MoveDTO objects.
     */
    public List<MoveDTO> getAllMoves(Long gameId, Integer turnIndex){
        String url = MOVES_URL + "/game/" + gameId + "/turn/" + turnIndex;
        ResponseEntity<MoveDTO[]> response = restTemplate.getForEntity(url, MoveDTO[].class);
        return response.getStatusCode() == HttpStatus.OK ? Arrays.asList(Objects.requireNonNull(response.getBody())) : null;
    }

    /**
     * Fetches the moves of a specific player for a specific turn in a game.
     *
     * @param gameId    the ID of the game.
     * @param playerId  the ID of the player.
     * @param turnIndex the turn index.
     * @return the MoveDTO object if found, otherwise null.
     */
    public MoveDTO getPlayerMoves(Long gameId, Long playerId, int turnIndex){
        String url = MOVES_URL + "/game/" + gameId + "/player/" + playerId + "/turn/ " + turnIndex;
        ResponseEntity<MoveDTO> response = restTemplate.getForEntity(url, MoveDTO.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    /**
     * Updates the moves of a player for a specific turn in a game.
     *
     * @param gameId    the ID of the game.
     * @param playerId  the ID of the player.
     * @param turnIndex the turn index.
     * @param moveTypes the new list of move types.
     * @throws RuntimeException if the moves could not be found.
     */
    public void updateMoves(Long gameId, Long playerId, int turnIndex, List<String> moveTypes) {
        String url = MOVES_URL + "/update";
        MoveDTO oldMove = getPlayerMoves(gameId, playerId, turnIndex);
        if (oldMove == null) {
            throw new RuntimeException("Moves not found");
        }
        oldMove.setMoveTypes(moveTypes);
        restTemplate.put(url, oldMove);
    }

    /**
     * Sets the API type in the application configuration.
     *
     * @param apiType the API type to set.
     */
    public void setApiType(ApiType apiType){
        AppConfig.setProperty("api.type", apiType.toString());
    }

    /**
     * Retrieves the server IP address from the application configuration.
     *
     * @return the server IP address as a String.
     */
    public String getServerIP(){
        String s = AppConfig.getProperty("server.base.url");
        try {
            URL url = new URL(s);
            return url.getHost();
        } catch (MalformedURLException e) {
            throw new RuntimeException(e);
        }
    }

    /**
     * Checks if the server is reachable.
     *
     * @return true if the server is reachable, otherwise false.
     */
    public boolean isReachable(){
        try {
            getAllGames();
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    /**
     * Test connection to and, if reachable, set API IP to the given IP address.
     *
     * @param ip IP-address to connect to
     * @return true if successfully connected, false otherwise
     */
    public boolean connectToServer(String ip) {

        if (testConnection(ip)) {
            AppConfig.setProperty("server.base.url", "http://" + ip + ":8080/api");
            AppConfig.setProperty("server.games.url", "http://" + ip + ":8080/api/games");
            AppConfig.setProperty("server.moves.url", "http://" + ip + ":8080/api/moves");
            AppConfig.setProperty("server.players.url", "http://" + ip + ":8080/api/players");

            updateURLs();

            return true;
        }
        else {
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
     * Checks if the specified player is the host of the specified game.
     *
     * @param gameId the ID of the game to check
     * @param playerId the ID of the player to check
     * @return true if the player is the host, false otherwise
     */
    public boolean isPlayerHost(Long gameId, Long playerId) {
        Game game = getGameById(gameId);
        if (game != null && !getPlayersInGame(gameId).isEmpty()) {
            // Check if the first player in the list is the one who is checking
            Long hostId = getPlayersInGame(gameId).get(0).getId();
            return Objects.equals(hostId, playerId);
        }
        // Return false if the game is not found or there are no players
        return false;
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
