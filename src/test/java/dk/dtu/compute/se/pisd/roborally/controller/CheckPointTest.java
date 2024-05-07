package dk.dtu.compute.se.pisd.roborally.controller;

import com.mysql.cj.util.TestUtils;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.view.CheckpointView;
import dk.dtu.compute.se.pisd.roborally.view.SpaceView;
import javafx.scene.image.ImageView;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.runner.RunWith;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@RunWith(TestUtils.class)
public class CheckPointTest {

    @Test
    public void TestGetCheckpointId() {
        // Arrange, acting and asserts ID, x and y. Testing data. Test for view are underneath.
        int checkpointId = 1;
        Checkpoint checkpoint = new Checkpoint(0, 0, checkpointId);
        int retrievedCheckpointId = checkpoint.getCheckpointId();
        assertEquals(checkpointId, retrievedCheckpointId);
    }

    @Test
    public void TestGetXCoordinate() {
        int x = 5;
        Checkpoint checkpoint = new Checkpoint(x, 0, 1);
        int retrievedX = checkpoint.x;
        assertEquals(x, retrievedX);
    }

    @Test
    public void TestGetYCoordinate() {
        int y = 10;
        Checkpoint checkpoint = new Checkpoint(0, y, 1);
        int retrievedY = checkpoint.y;
        assertEquals(y, retrievedY);
    }
}
    //Testing CheckPoint view **********

    /*    @Test
    public void testCheckpointViewAppearance(TestUtils utils) {
        // Create a mock Checkpoint object (you may need to adjust this based on your actual Checkpoint class)
        Checkpoint checkpoint = new Checkpoint(0, 0, 1);

        // Create a CheckpointView instance
        CheckpointView checkpointView = new CheckpointView(checkpoint);

        // Verify the appearance of the CheckpointView using assertions or visual inspection
        assertEquals(CheckpointView.SPACE_WIDTH, checkpointView.getWidth(), 0);
        assertEquals(CheckpointView.SPACE_HEIGHT, checkpointView.getHeight(), 0);

        // Save the appearance of the CheckpointView as a golden image
        TestUtils.goldenTest(checkpointView);
    }
}
*/
//Only setting up if group want it