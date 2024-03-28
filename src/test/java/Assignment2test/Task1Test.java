package Assignment2test;


import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.testng.AssertJUnit.assertEquals;
import static org.testng.AssertJUnit.assertNull;


public class Task1Test {
@Test
    public void testmoveCurrentPlayerToSpace() {
        // Set up testing environment
        Board board = new Board(8, 8);
        Player player = new Player(board, "Black", "Player 1");
        board.addPlayer(player);
        board.setCurrentPlayer(player);
        GameController gameController = new GameController(board);

        // Test the moveCurrentPlayerToSpace method
        assertNull(gameController.board.getSpace(0, 0).getPlayer());
        gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(0, 0));
        assertNotNull(gameController.board.getSpace(0, 0).getPlayer());
    }


}
