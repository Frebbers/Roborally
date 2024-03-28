package Assignment2test;


import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;


public class Task1Test {

    public void testmoveCurrentPlayerToSpace() {
        // Test if the player is moved to the correct space
        // Test if the space is empty
        // Test if the current player is changed
          GameController gameController = createTestEnvironment();
        assertNull(gameController.board.getSpace(0, 0).getPlayer());
          gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(0, 0));
        assertNotNull(gameController.board.getSpace(0, 0).getPlayer());

    }

    private GameController createTestEnvironment() {
        Board board = new Board(8, 8);
        Player player = new Player(board, "Black", "Player 1");
        board.addPlayer(player);
        return new GameController(board);
    }
}
