package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class LobbyBrowserControllerTest {
    @Test
    public void testConnectToServer() { // Must be running roboAPI for this test to pass
        LobbyBrowserController lobbyBrowserController = new LobbyBrowserController(new AppController(new RoboRally()));
        String ip;

        // Test positive scenario
        ip = "localhost";
        Assertions.assertTrue(lobbyBrowserController.connectToServer(ip));
        Assertions.assertEquals(AppConfig.getProperty("server.base.url"), "http://" + ip + ":8080/api");
        Assertions.assertEquals(AppConfig.getProperty("server.games.url"), "http://" + ip + ":8080/api/games");
        Assertions.assertEquals(AppConfig.getProperty("server.moves.url"), "http://" + ip + ":8080/api/moves");
        Assertions.assertEquals(AppConfig.getProperty("server.players.url"), "http://" + ip + ":8080/api/players");

        // Test negative scenario
        ip = "test123";
        Assertions.assertFalse(lobbyBrowserController.connectToServer(ip));
        Assertions.assertNotEquals(AppConfig.getProperty("server.base.url"), "http://" + ip + ":8080/api");
        Assertions.assertNotEquals(AppConfig.getProperty("server.games.url"), "http://" + ip + ":8080/api/games");
        Assertions.assertNotEquals(AppConfig.getProperty("server.moves.url"), "http://" + ip + ":8080/api/moves");
        Assertions.assertNotEquals(AppConfig.getProperty("server.players.url"), "http://" + ip + ":8080/api/players");
    }
}
