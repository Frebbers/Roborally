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

        // Initialize and set up the SettingsView
        settingsView = new SettingsView(appController);
        settingsView.initialize();

        // Set up the scene and show the stage
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
    void initialize() {
        // Add assertions to verify initialization
    }
}
