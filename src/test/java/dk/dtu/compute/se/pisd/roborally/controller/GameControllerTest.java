package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;

   @BeforeEach
    void setUp() {
       BoardData data = new BoardData("Testboard", TEST_WIDTH, TEST_HEIGHT, null, null, null);
        Board board = new Board(data);
        gameController = new GameController(board);
        for (int i = 0; i < 6; i++) {
            Player player = new Player(board, "Player " + i, i, null);
            board.addPlayer(player);
            player.setSpace(board.getSpace(i, i));
            player.setHeading(Heading.values()[i % Heading.values().length]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
    }

    @Test
    void moveCurrentPlayerToSpace() {
        Board board = gameController.board;
        Player player1 = board.getPlayer(0);
        Player player2 = board.getPlayer(1);

        gameController.moveCurrentPlayerToSpace(board.getSpace(0, 4));

        Assertions.assertEquals(player1, board.getSpace(0, 4).getPlayer(), "Player " + player1.getName() + " should beSpace (0,4)!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
        Assertions.assertEquals(player2, board.getCurrentPlayer(), "Current player should be " + player2.getName() +"!");
    }

    @Test
    void moveForward() {
        Board board = gameController.board;
        Player current = board.getCurrentPlayer();

        gameController.moveForward(current);

        Assertions.assertEquals(current, board.getSpace(0, 1).getPlayer(), "Player " + current.getName() + " should beSpace (0,1)!");
        Assertions.assertEquals(Heading.SOUTH, current.getHeading(), "Player 0 should be heading SOUTH!");
        Assertions.assertNull(board.getSpace(0, 0).getPlayer(), "Space (0,0) should be empty!");
    }
        /**
         * Test for task 1
         * @author Frederik Bode Hendrichsen s224804@dtu.dk
         */
        @Test
        public void testmoveCurrentPlayerToSpace() {
            // Set up testing environment
            GameController gameController = configureTestEnvironment();
            // Test the moveCurrentPlayerToSpace method
            assertNull(gameController.board.getSpace(0, 0).getPlayer());
            gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(0, 0));
            assertNotNull(gameController.board.getSpace(0, 0).getPlayer());
        }
        @Test
        public void testMoveCounter() {
            // Set up testing environment
            GameController gameController = configureTestEnvironment();
            // Test the moveCurrentPlayerToSpace method
            assertEquals(0, gameController.board.getMoveCount());
            gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(0, 0));
            assertEquals(1, gameController.board.getMoveCount());
        }

        @Test
        public void testGetStatusMessage() {
            // Set up testing environment
            GameController gameController = configureTestEnvironment();
            // Test the getStatusMessage method
            assertEquals("Phase: INITIALISATION, Player = Player 1, Step: 0, Move: 0", gameController.board.getStatusMessage());
            gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(0, 0));
            assertEquals("Phase: INITIALISATION, Player = Player 1, Step: 0, Move: 1", gameController.board.getStatusMessage());
        }
        @Test
        public void testExecuteCommand() {
            // Set up testing environment
            GameController gameController = configureTestEnvironment();
            Player player = new Player(gameController.board, "Player 1" , 1, "Black");
            gameController.board.setCurrentPlayer(player);
            // Test the executeCommand method
            gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(0, 0));
            assertNull(gameController.board.getSpace(0, 1).getPlayer());
            gameController.executeCommand(player, Command.FORWARD);
            assertNotNull(gameController.board.getSpace(0, 1).getPlayer());
            assertNull(gameController.board.getSpace(0, 0).getPlayer());
        }
        public static GameController configureTestEnvironment() {
            // Set up testing environment
            BoardData data = new BoardData("Testboard", 8, 8, null, null, null);
            Board board = new Board(data);
            Player player = new Player(board, "Player 1" , 1, "Black");
            board.setCurrentPlayer(player);
            return new GameController(board);
        }


}
