package dk.dtu.compute.se.pisd.roborally.model;

import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.Assertions;

public class BoardTest {

    @Test
    void updatePlayerOrder() {
        Board board = new Board(8, 8);
        board.addPlayer(new Player(board, "blue", "p1"));
        board.addPlayer(new Player(board, "red", "p2"));
        board.addPlayer(new Player(board, "green", "p3"));
        board.addPlayer(new Player(board, "pink", "p4"));

        board.getPlayer(0).setSpace(new Space(board, 2, 3));
        board.getPlayer(1).setSpace(new Space(board, 7, 7));
        board.getPlayer(2).setSpace(new Space(board, 1, 4));
        board.getPlayer(3).setSpace(new Space(board, 1, 1));

        board.updatePlayerOrder();

        Assertions.assertEquals(board.getPlayer(0).getName(), "p");
        Assertions.assertEquals(board.getPlayer(1).getName(), "p");
        Assertions.assertEquals(board.getPlayer(2).getName(), "p");
        Assertions.assertEquals(board.getPlayer(3).getName(), "p");
    }
}
