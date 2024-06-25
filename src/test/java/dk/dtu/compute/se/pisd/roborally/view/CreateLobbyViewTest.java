package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.scene.Scene;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.testfx.framework.junit5.ApplicationTest;

import static org.testfx.api.FxAssert.verifyThat;
import static org.testfx.matcher.base.NodeMatchers.isVisible;

public class CreateLobbyViewTest extends ApplicationTest {

    private CreateLobbyView createLobbyView;
    private AppController appController;
    private Stage testStage;

    @Override
    public void start(Stage stage) throws Exception {
        testStage = stage;

        RoboRally roboRally = new RoboRally();
        appController = new AppController(roboRally);

        roboRally.start(stage);
        createLobbyView = new CreateLobbyView(appController);
        createLobbyView.initialize();

        // Set up scene
        Scene scene = new Scene(new StackPane(createLobbyView), 600, 600);
        stage.setScene(scene);
        stage.show();
    }

    @BeforeEach
    void setUp() {
    }

    @Test
    public void uiVisibility(){
        verifyThat("#lobbyTypeText", isVisible());
        verifyThat("#connectionTypeDropdown", isVisible());
        verifyThat("#lobbyNameText", isVisible());
        verifyThat("#lobbyNameField", isVisible());
        verifyThat("#numberOfPlayersText", isVisible());
        verifyThat("#boardText", isVisible());
        verifyThat("#startButton", isVisible());
        verifyThat("#backButton", isVisible());
    }

    @AfterEach
    void tearDown() {
    }

    @Test
    void initialize() {
        // Add assertions to verify initialization
    }
}

