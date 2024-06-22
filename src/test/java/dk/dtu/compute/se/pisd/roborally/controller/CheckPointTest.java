package dk.dtu.compute.se.pisd.roborally.controller;


import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.RobotType;
import dk.dtu.compute.se.pisd.roborally.model.PlayerState;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

//@RunWith(TestUtils.class)
public class CheckPointTest {


    private Player player;

    @BeforeEach
    public void setUp() {
        player = new Player(1L, "Test Player", RobotType.Circuito, PlayerState.READY, 100L);
    }

    @Test
    public void testAddAndGetCheckpoint() {
        Checkpoint cp1 = new Checkpoint(0, 0, 1);
        player.setCheckpoint(cp1);
        assertNotNull(player.getCheckpoint(0), "Checkpoint should be added to the list");
        assertEquals(cp1, player.getCheckpoint(0), "Retrieved checkpoint should match the added checkpoint");
    }

    @Test
    public void testAddCheckpointInOrder() {
        Checkpoint cp1 = new Checkpoint(0, 0, 1);
        Checkpoint cp2 = new Checkpoint(1, 1, 2);
        player.setCheckpoint(cp1);
        player.setCheckpoint(cp2);
        assertEquals(2, player.getCheckpoints().size(), "Both checkpoints should be added in order");
    }

    @Test
    public void testAddCheckpointOutOfOrder() {
        Checkpoint cp1 = new Checkpoint(0, 0, 2);
        player.setCheckpoint(cp1);
        assertTrue(player.getCheckpoints().isEmpty(), "Checkpoint should not be added if it does not follow the order");
    }

    @Test
    public void testAddDuplicateCheckpoint() {
        Checkpoint cp1 = new Checkpoint(0, 0, 1);
        player.setCheckpoint(cp1);
        player.setCheckpoint(cp1);
        assertEquals(1, player.getCheckpoints().size(), "Duplicate checkpoints should not be added");
    }

    @Test
    public void testGetAllCheckpoints() {
        Checkpoint cp1 = new Checkpoint(0, 0, 1);
        Checkpoint cp2 = new Checkpoint(1, 1, 2);
        player.setCheckpoint(cp1);
        player.setCheckpoint(cp2);
        List<Checkpoint> checkpoints = player.getCheckpoints();
        assertEquals(2, checkpoints.size(), "Should retrieve all checkpoints");
        assertTrue(checkpoints.contains(cp1) && checkpoints.contains(cp2), "All checkpoints should be correct");
    }

    @Test
    public void testGetCheckpointId() {
        // Arrange, acting and asserts ID, x and y. Testing data. Test for view are underneath.
        int checkpointId = 1;
        Checkpoint checkpoint = new Checkpoint(0, 0, checkpointId);
        int retrievedCheckpointId = checkpoint.getCheckpointId();
        assertEquals(checkpointId, retrievedCheckpointId);
    }

    @Test
    public void testGetXCoordinate() {
        int x = 5;
        Checkpoint checkpoint = new Checkpoint(x, 0, 1);
        int retrievedX = checkpoint.x;
        assertEquals(x, retrievedX);
    }

    @Test
    public void testGetYCoordinate() {
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