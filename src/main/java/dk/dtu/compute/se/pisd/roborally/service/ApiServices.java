package dk.dtu.compute.se.pisd.roborally.service;

import dk.dtu.compute.se.pisd.roborally.model.Game;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class ApiServices {
    private static final String BASE_URL = "http://localhost:8080/api/games";

    private final RestTemplate restTemplate;

    public ApiServices() {
        this.restTemplate = new RestTemplate();
    }

    public List<Game> getAllGames() {
        ResponseEntity<Game[]> response = restTemplate.getForEntity(BASE_URL, Game[].class);
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

    public String createGame(int playerCount) {
        Game game = new Game();
        game.playerCount = playerCount;
        ResponseEntity<Game> response = restTemplate.postForEntity(BASE_URL, game, Game.class);
        if (response.getStatusCode() == HttpStatusCode.valueOf(201)) {
        return "Game created successfully";
        } else {
            return "Error creating game";
        }
    }

}
