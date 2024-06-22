package dk.dtu.compute.se.pisd.roborally.service;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyBrowserController;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class ApiServicesTest {
    @Test
    public void testIsReachable() {
        // Test if the server is reachable
        AppController appController = new AppController(new RoboRally());
        assert new ApiServices(appController).isReachable();
    }

    @Test
    public void testUpdateURLs() {
        AppController appController = new AppController(new RoboRally());
        ApiServices apiServices = new ApiServices(appController);
        String apiType = AppConfig.getProperty("api.type");

        // Store current properties in application.properties (because this test changes them
        String resetApiType = AppConfig.getProperty("api.type");
        String resetBase = AppConfig.getProperty(apiType + ".base.url");
        String resetGames = AppConfig.getProperty(apiType + ".games.url");
        String resetMoves = AppConfig.getProperty(apiType + ".moves.url");
        String resetPlayers = AppConfig.getProperty(apiType + ".players.url");

        // Set test properties
        AppConfig.setProperty(apiType + ".base.url", "testBase");
        AppConfig.setProperty(apiType + ".games.url", "testGames");
        AppConfig.setProperty(apiType + ".moves.url", "testMoves");
        AppConfig.setProperty(apiType + ".players.url", "testPlayers");

        // Run method
        apiServices.updateURLs();

        // Test expected results
        Assertions.assertEquals("testBase", apiServices.getBASE_URL());
        Assertions.assertEquals("testGames", apiServices.getGAMES_URL());
        Assertions.assertEquals("testMoves", apiServices.getMOVES_URL());
        Assertions.assertEquals("testPlayers", apiServices.getPLAYERS_URL());

        // Restore previous properties
        AppConfig.setProperty(apiType + ".base.url", resetBase);
        AppConfig.setProperty(apiType + ".games.url", resetGames);
        AppConfig.setProperty(apiType + ".moves.url", resetMoves);
        AppConfig.setProperty(apiType + ".players.url", resetPlayers);
    }

    @Test
    public void testTestConnection() { // Must be running roboAPI for this test to pass
        AppController appController = new AppController(new RoboRally());
        ApiServices apiServices = new ApiServices(appController);

        Assertions.assertTrue(apiServices.testConnection("localhost"));
        Assertions.assertFalse(apiServices.testConnection("test123"));
    }

    @Test
    public void testConnectToServer() { // Must be running roboAPI for this test to pass
        AppController appController = new AppController(new RoboRally());
        ApiServices apiServices = new ApiServices(appController);
        String ip;

        // Test positive scenario
        ip = "localhost";
        Assertions.assertTrue(apiServices.connectToServer(ip));
        Assertions.assertEquals(AppConfig.getProperty("server.base.url"), "http://" + ip + ":8080/api");
        Assertions.assertEquals(AppConfig.getProperty("server.games.url"), "http://" + ip + ":8080/api/games");
        Assertions.assertEquals(AppConfig.getProperty("server.moves.url"), "http://" + ip + ":8080/api/moves");
        Assertions.assertEquals(AppConfig.getProperty("server.players.url"), "http://" + ip + ":8080/api/players");

        // Test negative scenario
        ip = "test123";
        Assertions.assertFalse(apiServices.connectToServer(ip));
        Assertions.assertNotEquals(AppConfig.getProperty("server.base.url"), "http://" + ip + ":8080/api");
        Assertions.assertNotEquals(AppConfig.getProperty("server.games.url"), "http://" + ip + ":8080/api/games");
        Assertions.assertNotEquals(AppConfig.getProperty("server.moves.url"), "http://" + ip + ":8080/api/moves");
        Assertions.assertNotEquals(AppConfig.getProperty("server.players.url"), "http://" + ip + ":8080/api/players");
    }
}
