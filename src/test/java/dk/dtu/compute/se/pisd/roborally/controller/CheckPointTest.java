package dk.dtu.compute.se.pisd.roborally.controller;


import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

public class CheckPointTest {

    private Player player;

    @BeforeEach public void setUp() {
        PlayerDTO playerDTO = new PlayerDTO();
        playerDTO.setId(1L);
        playerDTO.setName("Test Player");
        playerDTO.setRobotType(RobotType.Circuito);
        playerDTO.setState(PlayerState.READY);
        playerDTO.setGameId(1L);

        AppController.localPlayer = playerDTO;
        player = new Player(playerDTO.getId(), playerDTO.getName(), playerDTO.getRobotType(), PlayerState.READY, playerDTO.getGameId());
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