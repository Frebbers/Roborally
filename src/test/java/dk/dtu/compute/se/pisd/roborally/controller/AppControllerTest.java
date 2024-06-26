package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;


class AppControllerTest {
    private RoboRally roboRally;
    private AppController appController;
    ApiServices apiServices;

@Test
    public void onLobbyJoinTestEmptyProperties() {
        AppConfig.setProperty("local.player.id", "");
        AppConfig.setProperty("local.player.robotType", "");
        assert apiServices.isReachable();
        appController.onLobbyJoin();
    }

    @Test
    //Scenario: On lobby join with the local player properties not existing on the server
    public void onLobbyJoinTestNotOnServer() {
        assert apiServices.isReachable();
        //assert that the player does not exist on the server before testing. If it does, restart the server.
        assert (apiServices.playerExists("testPlayer", "9999")==null);
        AppConfig.setProperty("local.player.name","testPlayer");
        AppConfig.setProperty("local.player.id", "5000");
        assert apiServices.isReachable();
        appController.onLobbyJoin();
        assert (apiServices.playerExists("testPlayer", AppConfig.getProperty("local.player.id"))!=null);
    }

    public
    @BeforeEach
    void setUp() {
        this.roboRally = new RoboRally(true);
        AppConfig.setProperty("local.player.name", "TestPlayer");
        appController = new AppController(roboRally);
        apiServices = new ApiServices(appController);
    }
}

