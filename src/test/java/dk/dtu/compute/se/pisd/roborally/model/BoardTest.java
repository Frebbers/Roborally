package dk.dtu.compute.se.pisd.roborally.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

import java.util.ArrayList;

public class BoardTest {

    @Test
    void setCurrentPlayer() { // This doesn't work
        // Initialize board
        Board board = new Board(8, 8, new Checkpoint[3]);

        // Initialize players
        board.addPlayer(new Player(board, "blue", "p1"));
        board.addPlayer(new Player(board, "red", "p2"));
        board.addPlayer(new Player(board, "green", "p3"));
        board.addPlayer(new Player(board, "pink", "p4"));

        // Set player spaces
        board.getPlayer(0).setSpace(new Space(board, 2, 3));
        board.getPlayer(1).setSpace(new Space(board, 7, 7));
        board.getPlayer(2).setSpace(new Space(board, 1, 4));
        board.getPlayer(3).setSpace(new Space(board, 1, 2));

        // Set priority antenna space
        board.getPriorityAntenna().setSpace(new Space(board, 1, 1));

        // Test order
        board.setCurrentPlayer();
        Assertions.assertEquals(board.getCurrentPlayer().getName(), "p4");

        board.setCurrentPlayer();
        Assertions.assertEquals(board.getCurrentPlayer().getName(), "p1");

        board.setCurrentPlayer();
        Assertions.assertEquals(board.getCurrentPlayer().getName(), "p3");

        board.setCurrentPlayer();
        Assertions.assertEquals(board.getCurrentPlayer().getName(), "p2");
    }
}
