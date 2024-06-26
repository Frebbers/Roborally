package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.roborally.controller.BoardData;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

public class PriorityAntennaTest {
    PriorityAntenna priorityAntenna;
    BoardData boardData;
    Board board;

    @BeforeEach
    public void setUp() {
        JsonReader jsonReader = new JsonReader(1);
        boardData = jsonReader.readBoardJson();
        priorityAntenna = boardData.priorityAntennas.get(0);
    }

    @AfterEach
    public void tearDown() {
        priorityAntenna = null;
        boardData = null;
        board = null;
    }

    @Test
    public void testGetX() {
        // Board1's priority antenna is at (3,3)
        Assertions.assertEquals(2, priorityAntenna.getX());
    }

    @Test
    public void testGetY() {
        // Board1's priority antenna is at (3,3)
        Assertions.assertEquals(2, priorityAntenna.getY());
    }

    @Test
    public void testSetBoard() {
        board = new Board((long)1, boardData);
        priorityAntenna.setBoard(board);
    }

    @Test
    public void testOrderPlayers() {
        // Set up board
        testSetBoard();

        // Initialize players
        Player[] players = initializeThreePlayers();

        // Order players
        priorityAntenna.orderPlayers();

        // Test outcome
        Assertions.assertEquals(players[1], priorityAntenna.getPlayer(0));
        Assertions.assertEquals(players[2], priorityAntenna.getPlayer(1));
        Assertions.assertEquals(players[0], priorityAntenna.getPlayer(2));
    }

    @Test
    public void testUpdatePlayers() {
        // Set up board
        testSetBoard();

        // Initialize players
        Player[] players = initializeThreePlayers();

        // Update players list
        priorityAntenna.updatePlayers();

        // Test outcome
        Assertions.assertEquals(board.getPlayer(0), priorityAntenna.getPlayer(0));
        Assertions.assertEquals(board.getPlayer(1), priorityAntenna.getPlayer(1));
        Assertions.assertEquals(board.getPlayer(2), priorityAntenna.getPlayer(2));
    }

    @Test
    public void testGetPlayer() { // Kinda redundant
        // Set up board
        testSetBoard();

        // Initialize player
        Player player1 = new Player((long)1, "Player1", RobotType.Circuito, PlayerState.READY, (long)1);
        player1.setBoard(board);
        player1.setSpace(board.getSpace(6,6));
        board.addPlayer(player1);

        // Get player
        Player gottenPlayer = priorityAntenna.getPlayer(0);

        // Test outcome
        Assertions.assertEquals(player1, gottenPlayer);
    }

    @Test
    public void testGetPlayerNumber() {
        // Set up board
        testSetBoard();

        // Initialize players
        Player[] players = initializeThreePlayers();

        // Order players
        priorityAntenna.orderPlayers();

        // Test outcome
        Assertions.assertEquals(0, priorityAntenna.getPlayerNumber(players[1]));
        Assertions.assertEquals(1, priorityAntenna.getPlayerNumber(players[2]));
        Assertions.assertEquals(2, priorityAntenna.getPlayerNumber(players[0]));
    }

    private Player[] initializeThreePlayers() {

        Player player1 = new Player((long)1, "Player1", RobotType.Circuito, PlayerState.READY, (long)1);
        Player player2 = new Player((long)2, "Player2", RobotType.HexaByte, PlayerState.READY, (long)1);
        Player player3 = new Player((long)3, "Player3", RobotType.Quantix, PlayerState.READY, (long)1);

        player1.setBoard(board);
        player2.setBoard(board);
        player3.setBoard(board);

        player1.setSpace(board.getSpace(6,6));
        player2.setSpace(board.getSpace(3,2));
        player3.setSpace(board.getSpace(4,5));

        board.addPlayer(player1);
        board.addPlayer(player2);
        board.addPlayer(player3);

        return new Player[] {player1, player2, player3};
    }
}
