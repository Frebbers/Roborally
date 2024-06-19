package dk.dtu.compute.se.pisd.roborally.service;
import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class ApiServicesTest {
    @Test
    public void testIsReachable() {
        // Test if the server is reachable
        assert new ApiServices().isReachable();
    }

    @Test
    public void testUpdateURLs() {
        ApiServices apiServices = new ApiServices();
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
        ApiServices apiServices = new ApiServices();

        Assertions.assertTrue(apiServices.testConnection("localhost"));
        Assertions.assertFalse(apiServices.testConnection("test123"));
    }
}
