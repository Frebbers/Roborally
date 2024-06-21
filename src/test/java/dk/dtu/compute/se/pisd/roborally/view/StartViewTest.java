package dk.dtu.compute.se.pisd.roborally.view;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import javafx.scene.text.Text;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class StartViewTest extends BaseView {

    private StartView startView;
    private AppController appController;
    private RobotSettingsView robotSettingsView;
    private PlayerDTO playerDTO;
    RoboRally roboRally;

    public StartViewTest(AppController appController) {
        super(appController);
    }

    @BeforeEach
    void setUp() {
        appController = new AppController(roboRally);  // Initialize your AppController here
        startView = new StartView(appController);
        initialize();
    }

    @Test
    void testUpdatePlayerName() {
        // Simulate setting a player name in AppConfig
        playerDTO.setName("TestPlayer");


        // Call updatePlayerName() method
        startView.updatePlayerName();

        // Verify the nameLabel text
        Text nameLabel = (Text) lookup(".text");
        assertEquals("Character name: TestPlayer", nameLabel.getText());
    }
    @Override
    public void initialize() {

    }
}
