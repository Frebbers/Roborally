package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.model.Game;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static javafx.scene.input.KeyCode.L;

public class LobbyViewTest extends ApplicationTest {

    private SettingsView settingsView;
    private LobbyView lobbyView;
    private AppController appController;
    private Stage testStage;

    @Override
    public void start(Stage stage) throws Exception {
        testStage = stage;

        RoboRally roboRally = new RoboRally();
        appController = new AppController(roboRally);

        roboRally.start(stage);
        Long gameId = createGameId();
        lobbyView = new LobbyView(appController, gameId);
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
    void initialize() {
        // Add assertions to verify initialization
    }
    
    private Long createGameId() {
        Game game = new Game();
        Long gameId = game.id;
        return gameId;
    }
}
