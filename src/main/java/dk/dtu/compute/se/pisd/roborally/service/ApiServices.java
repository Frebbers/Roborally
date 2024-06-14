package dk.dtu.compute.se.pisd.roborally.service;

import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.model.ApiType;
import dk.dtu.compute.se.pisd.roborally.model.DTO.MoveDTO;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.model.Game;
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
    private String MOVES_URL;

    private final RestTemplate restTemplate = new RestTemplate();

    private PlayerDTO localPlayer;

    public ApiServices(){
        ApiType type = Utilities.toEnum(ApiType.class, AppConfig.getProperty("api.type"));

        if(type == ApiType.LOCAL){
            BASE_URL = AppConfig.getProperty("local.base.url");
            GAMES_URL = AppConfig.getProperty("local.games.url");
            PLAYERS_URL = AppConfig.getProperty("local.players.url");
            MOVES_URL = AppConfig.getProperty("local.moves.url");
        }
        else if(type == ApiType.SERVER){
            BASE_URL = AppConfig.getProperty("server.base.url");
            GAMES_URL = AppConfig.getProperty("server.games.url");
            PLAYERS_URL = AppConfig.getProperty("server.players.url");
            MOVES_URL = AppConfig.getProperty("server.moves.url");
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

    public Game createGame(Long boardId, int maxPlayers) {
        Game game = new Game();
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
        player.setState(PlayerState.NOT_READY);
        player.setGameId(0L);

        // Upload the player to the server
        ResponseEntity<PlayerDTO> response = null;
        try {
            response = restTemplate.postForEntity(PLAYERS_URL, player, PlayerDTO.class);
            localPlayer = response.getBody();
        } catch (Exception e) {
            return null;
        }
        //ResponseEntity<PlayerDTO> response = restTemplate.postForEntity(PLAYERS_URL, player, PlayerDTO.class);

        // Set the local player to the response from the server with its corresponding ID

        return response.getStatusCode() == HttpStatus.OK ? localPlayer : null;
    }

    public void updatePlayerState(Long id){
        PlayerDTO player = getPlayerById(id);
        if (player != null) {
            player.setState(player.getState() == PlayerState.READY ? PlayerState.NOT_READY : PlayerState.READY);

            String playerUrl = PLAYERS_URL + "/" + player.getId();
            try {
                restTemplate.put(playerUrl, player);
            } catch (Exception e) {
                e.getMessage();
            }
        }
    }

    public PlayerDTO getPlayerById(Long playerId) {
        String url = PLAYERS_URL + "/" + playerId;
        ResponseEntity<PlayerDTO> response = restTemplate.getForEntity(url, PlayerDTO.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }

    public void onPlayerLeave(Long playerId) {
        PlayerDTO player = getPlayerById(playerId);
        if (player != null) {
            player.setGameId(0L);
            player.setState(PlayerState.NOT_READY);

            String playerUrl = PLAYERS_URL + "/" + playerId;
            try {
                restTemplate.put(playerUrl, player);
            } catch (Exception e) {
                throw new RuntimeException("Error updating player: " + e.getMessage());
            }
        } else {
            throw new RuntimeException("Player not found");
        }

        // Clear the local player
        localPlayer = null;
    }

    public PlayerDTO getLocalPlayer(){
        return localPlayer;
    }

    public MoveDTO createMove(Long gameId, Long playerId, Integer turnIndex, List<String> moves){
        // Create a new move on the client and fill the information (Do not create a constructor for this)
        MoveDTO move = new MoveDTO();
        move.setGameId(gameId);
        move.setPlayerId(playerId);
        move.setMoves(moves);
        move.setTurnIndex(turnIndex);

        // Upload the move to the server
        ResponseEntity<MoveDTO> response = restTemplate.postForEntity(MOVES_URL, move, MoveDTO.class);
        return response.getStatusCode() == HttpStatus.OK ? response.getBody() : null;
    }
}
