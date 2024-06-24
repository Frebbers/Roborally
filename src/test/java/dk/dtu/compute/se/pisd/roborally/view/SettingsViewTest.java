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

public class SettingsViewTest extends ApplicationTest {

    private SettingsView settingsView;
    private AppController appController;
    private Stage testStage;

    @Override
    public void start(Stage stage) throws Exception {
        testStage = stage;

        RoboRally roboRally = new RoboRally();
        appController = new AppController(roboRally);

        roboRally.start(stage);
        settingsView = new SettingsView(appController);
        settingsView.initialize();

        // Set up scene
        Scene scene = new Scene(new StackPane(settingsView), 600, 600);
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
    public void uiVisibility(){
        verifyThat("settings-header", isVisible());
        verifyThat("settings-ip-dialog", isVisible());
        verifyThat("settings-connect-button", isVisible());
        verifyThat("settings-name-header", isVisible());
        verifyThat("settings-name-field", isVisible());
        verifyThat("settings-robot-header", isVisible());
        verifyThat("settings-back-button", isVisible());
    }

    @Test
    void initialize() {
        // Add assertions to verify initialization
    }
}
