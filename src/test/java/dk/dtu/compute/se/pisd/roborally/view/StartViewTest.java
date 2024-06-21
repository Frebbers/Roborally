package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.application.Platform;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import javafx.scene.text.Text;
import org.junit.jupiter.api.*;
import org.testfx.framework.junit5.ApplicationTest;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class StartViewTest extends ApplicationTest {

    private StartView startView;
    private AppController appController;
    private Stage testStage;

    @Override
    public void start(Stage stage) {
        testStage = stage;
        RoboRally roboRally = new RoboRally();
        appController = new AppController(roboRally);
        roboRally.start(stage); // Initialize the stage within RoboRally

        // Create and initialize StartView
        startView = new StartView(appController);
        startView.initialize();

        // Set up scene with StartView
        Scene scene = new Scene(new StackPane(startView), 600, 600);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setUp() {
        // Set up any needed configurations or states before each test
    }

    @Test
    @DisplayName("Verify UI Initialization")
    void testInitialize() {
        // Verify initial state of UI components
        verifyThat("#startLobbyButton", isVisible()); // Example assertion for a button
        verifyThat("#joinLobbyButton", isVisible()); // Example assertion for another button
        verifyThat("#nameLabel", isVisible()); // Verify that nameLabel is visible
    }

    @Test
    @DisplayName("Update Player Name")
    void testUpdatePlayerName() {
        // Simulate setting a player name in AppConfig
        AppConfig.setProperty("local.player.name", "TestPlayer");

        // Call updatePlayerName() method in StartView
        startView.updatePlayerName();

        // Verify the updated nameLabel text
        Text nameLabel = lookup("#nameLabel").query();
        assertEquals("Character name: TestPlayer", nameLabel.getText());
    }

    @Test
    @DisplayName("Interaction with Start Lobby Button")
    void testStartLobbyButton() {
        // Click on the Start Lobby button
        clickOn("#startLobbyButton");

        // Ensure the UI updates are complete by running assertions on the JavaFX Application Thread
        Platform.runLater(() -> {
            // Verify that the LobbyView or its identifier is visible
            verifyThat("#lobbyView", isVisible());
        });
    }


    @AfterEach
    void tearDown() {
        // Clean up after each test if needed
    }

    @AfterAll
    static void tearDownAll() {
        // Clean up any resources after all tests if needed
    }
}
