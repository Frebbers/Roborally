package dk.dtu.compute.se.pisd.roborally.model;

import org.junit.jupiter.api.Test;

public class BoardTest {

    @Test
    void updatePlayerOrder() {
        Board board = new Board(8, 8);
        board.addPlayer(new Player(board, "blue", "player 1"));
        board.addPlayer(new Player(board, "red", "player 2"));
        board.addPlayer(new Player(board, "green", "player 3"));
        board.addPlayer(new Player(board, "pink", "player 4"));

        for (int i = 0; i < 4; i++) {
            
        }
    }
}
