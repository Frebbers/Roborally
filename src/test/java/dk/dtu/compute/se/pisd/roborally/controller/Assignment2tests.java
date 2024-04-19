package dk.dtu.compute.se.pisd.roborally.controller;


import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;


public class Assignment2tests {
/**
 * Test for task 1
    * @author Frederik Bode Hendrichsen s224804@dtu.dk
 */
@Test
    public void testmoveCurrentPlayerToSpace() {
        // Set up testing environment
        GameController gameController = configureTestenvironment();
        // Test the moveCurrentPlayerToSpace method
        assertNull(gameController.board.getSpace(0, 0).getPlayer());
        gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(0, 0));
        assertNotNull(gameController.board.getSpace(0, 0).getPlayer());
    }
@Test
    public void testMoveCounter() {
        // Set up testing environment
        GameController gameController = configureTestenvironment();
        // Test the moveCurrentPlayerToSpace method
        assertEquals(0, gameController.board.getMoveCount());
        gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(0, 0));
        assertEquals(1, gameController.board.getMoveCount());
    }

    @Test
    public void testGetStatusMessage() {
        // Set up testing environment
        GameController gameController = configureTestenvironment();
        // Test the getStatusMessage method
        assertEquals("Phase: INITIALISATION, Player = Player 1, Step: 0, Move: 0", gameController.board.getStatusMessage());
        gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(0, 0));
        assertEquals("Phase: INITIALISATION, Player = Player 1, Step: 0, Move: 1", gameController.board.getStatusMessage());
    }
@Test
    public void testExecuteCommand() {
        // Set up testing environment
        GameController gameController = configureTestenvironment();
        Player player = new Player(gameController.board, "Black","Player 1");
        gameController.board.setCurrentPlayer(player);
        // Test the executeCommand method
        gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(0, 0));
        assertNull(gameController.board.getSpace(0, 1).getPlayer());
        gameController.executeCommand(player, Command.FORWARD);
        assertNotNull(gameController.board.getSpace(0, 1).getPlayer());
        assertNull(gameController.board.getSpace(0, 0).getPlayer());
    }
    public static GameController configureTestenvironment() {
        // Set up testing environment
        Board board = new Board(8, 8);
        Player player = new Player(board, "Black", "Player 1");
        board.setCurrentPlayer(player);
        return new GameController(board);
    }


}
