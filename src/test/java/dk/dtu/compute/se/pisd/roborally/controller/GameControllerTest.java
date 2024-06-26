package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.*;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GameControllerTest {

    private final int TEST_WIDTH = 8;
    private final int TEST_HEIGHT = 8;

    private GameController gameController;



    @BeforeEach public void setUp() {
       List <PriorityAntenna> antennaList = new ArrayList<PriorityAntenna>();
       antennaList.add (new PriorityAntenna(2,2));
       BoardData data = new BoardData("Testboard", TEST_WIDTH, TEST_HEIGHT, null,null, null, null, antennaList);
        Board board = new Board(1L, data);
        RoboRally roboRally = new RoboRally();
        AppController appController = new AppController(roboRally);
        gameController = new GameController(appController, board);
        for (long i = 0; i < 6; i++) {
            Player player = new Player(i, "Player " + i,RobotType.Boltz ,PlayerState.READY, null);
            player.setBoard(board);
            board.addPlayer(player);
            player.setSpace(board.getSpace((int) i, (int) i));
            assertNotNull(player.getSpace());
            player.setHeading(Heading.values()[(int) (i % Heading.values().length)]);
        }
        board.setCurrentPlayer(board.getPlayer(0));
    }

    @AfterEach
    void tearDown() {
        gameController = null;
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
           /* @Test method no longer exists
        @Test
        public void testmoveCurrentPlayerToSpace() {
            // Set up testing environment
            GameController gameController = configureTestEnvironment();
            // Test the moveCurrentPlayerToSpace method
            assertNull(gameController.board.getSpace(0, 0).getPlayer());
            gameController.moveCurrentPlayerToSpace(gameController.board.getSpace(0, 0));
            assertNotNull(gameController.board.getSpace(0, 0).getPlayer());
        }
        /**
            */
        @Test
        public void testMoveCounter() {
            // Set up testing environment
            //GameController gameController = configureTestEnvironment();
            // Test the moveCurrentPlayerToSpace method
            assertEquals(0, gameController.board.getMoveCount());
            gameController.startProgrammingPhase();
            CommandCard card = new CommandCard(Command.FORWARD);
            gameController.board.setPhase(Phase.ACTIVATION);
            gameController.board.getCurrentPlayer().getCardField(0).setCard(card);
            gameController.executeStep();
            assertEquals(1, gameController.board.getMoveCount());
        }

        @Test
        public void testGetStatusMessage() {
            // Set up testing environment
            //GameController gameController = configureTestEnvironment();
            // Test the getStatusMessage method
            assertEquals("Phase: INITIALISATION, Player = Player 1, Step: 0, Move: 0", gameController.board.getStatusMessage());
            gameController.moveForward(gameController.board.getCurrentPlayer());
            assertEquals("Phase: INITIALISATION, Player = Player 1, Step: 0, Move: 1", gameController.board.getStatusMessage());
        }
        //@Test TODO FIX THIS
     /*   public void testExecuteCommand() {
            // Set up testing environment
           // GameController gameController = configureTestEnvironment();
            Player player = new Player(gameController.board, "Player 1" , 1, "Black");
            gameController.board.setCurrentPlayer(player);
            // Test the executeCommand method
            gameController.moveForward(gameController.board.getCurrentPlayer());
            assertNull(gameController.board.getSpace(0, 1).getPlayer());
            gameController.executeCommand(player, Command.FORWARD);
            assertNotNull(gameController.board.getSpace(0, 1).getPlayer());
            assertNull(gameController.board.getSpace(0, 0).getPlayer());
        }
        /*public static GameController configureTestEnvironment() {
            // Set up testing environment
            BoardData data = new BoardData("Testboard", 8, 8, null, null, null, null);
            Board board = new Board(data);
            Player player = new Player(board, "Player 1" , 1, "Black");
            board.setCurrentPlayer(player);
            return new GameController(board);
        }
*/

        @Test
        public void testExecuteFieldActions(){
            Space Startspace = gameController.board.getCurrentPlayer().getSpace();

            ConveyorBelt conveyorBelt = new ConveyorBelt(0,1,"SOUTH");

            gameController.board.getSpace(1,0).setConveyorBelt(conveyorBelt);

            gameController.boardController.handleMovement(Startspace,gameController.board.getSpace(1,0), Heading.valueOf("EAST"));

            gameController.executeFieldActions(gameController.board.getSpace(1,0));

            assertEquals (gameController.board.getSpace(1,1),(gameController.board.getCurrentPlayer().getSpace()));
        }
        public GameController getGameController() {return gameController;}
}
