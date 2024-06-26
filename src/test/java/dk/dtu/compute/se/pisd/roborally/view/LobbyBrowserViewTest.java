package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class LobbyBrowserViewTest extends ApplicationTest {

    LobbyBrowserView lobbyBrowserView;
    private AppController appController;
    private Stage testStage;
    ApiServices apiServices;
    RoboRally roboRally;

    public LobbyBrowserViewTest(AppController appController, RoboRally roboRally) {
        this.appController = appController;
        this.roboRally = roboRally;
        Stage stage = new Stage();
        start(stage);

    }
    /**
     * Test if the connection to the server is displayed on the screen as successful or not
     * @return true if the view shows "Connected successfully to Local", false otherwise
     * @author s224804
     */
    public boolean isConnectedStatus() {
        return this.lobbyBrowserView.getConnectToServerFeedback().getText().equals("Connected successfully to Local");
    }
    public boolean isJoinLobbyButtonDisabled() {
        return this.lobbyBrowserView.getJoinLobbyButton().isDisabled();
    }

    @Override
    public void start(Stage stage) {
        testStage = stage;
        RoboRally roboRally = new RoboRally();
        appController = new AppController(roboRally);
        roboRally.createLobbyBrowserView(appController);// Initialize your AppController
        LobbyBrowserView lobbyBrowserView = new LobbyBrowserView(appController);

        StackPane root = new StackPane(lobbyBrowserView);
        Scene scene = new Scene(root, 600, 600);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    public void setUp() {
        String connectionTypeDropdown = "local";
    }

    @Test
    public void uiVisibility(){
        verifyThat("#joinLobbyButton", isVisible());
        verifyThat("#backButton", isVisible());

    }
/*
    @Test
    public void checkNoConnectionView() {
        isConnected = false;
        testInitializeConnection() = LobbyBrowserView.initializeConnection;

        FxAssert.verifyThat("Failed to connect to Local", NodeMatchers.isNotNull());
    }

    @Test
    public void testInitialize() {
        // Verify the connection type dropdown is present
        FxAssert.verifyThat("#connectionTypeDropdown", NodeMatchers.isNotNull());

        // Verify the lobby list view is present
        FxAssert.verifyThat("#lobbyListView", NodeMatchers.isNotNull());

        // Verify the join lobby button is present
        FxAssert.verifyThat("#joinLobbyButton", NodeMatchers.isNotNull());
    }

    @Test
    public void testChangeConnectionType() {
        // Select "Local" connection type
        clickOn("#connectionTypeDropdown").clickOn("Local");

        // Verify the connection feedback text updates
        FxAssert.verifyThat("#connectToServerFeedback", NodeMatchers.hasText("Connected successfully to Local"));
    }

    @Test
    public void testSelectLobby() {

    }

    @Test
    public void testJoinLobbyButton() {
        waitForFxEvents();
        // Verify the join lobby button is enabled
        FxAssert.verifyThat("#joinLobbyButton", NodeMatchers.isEnabled());

        // Click the join lobby button
        clickOn("#joinLobbyButton");

        // Additional assertions to verify joining logic can be added here
        waitForFxEvents();
    }

    // Utility method to wait for JavaFX events to be processed
    private void waitForFxEvents() {
        try {
            Thread.sleep(100); // Adjust the sleep duration if necessary
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

 */
}
