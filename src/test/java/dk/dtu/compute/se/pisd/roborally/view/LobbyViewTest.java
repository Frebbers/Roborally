package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class LobbyViewTest extends ApplicationTest {

    private LobbyView lobbyView;
    private AppController appController;
    private ApiServices apiServices;
    private Stage testStage;

    @Override
    public void start(Stage stage) throws Exception {
        testStage = stage;

        RoboRally roboRally = new RoboRally();
        appController = new AppController(roboRally);
        apiServices = appController.getApiServices();

        apiServices.createGame("testGame", 1L, 1);
        apiServices.joinGame(1L, 0L);

        roboRally.start(stage);
        lobbyView = new LobbyView(appController, 1L);
        lobbyView.initialize();

        // Set up scene
        Scene scene = new Scene(new StackPane(lobbyView), 600, 600);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setUp() {
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    @DisplayName("Verify UI Initialization")
    void isUIVisible() {
        verifyThat("#leaveButton", isVisible());
        verifyThat("#readyButton", isVisible());
    }
}
