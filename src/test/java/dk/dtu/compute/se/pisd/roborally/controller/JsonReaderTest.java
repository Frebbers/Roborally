package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.JsonReader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonReaderTest {

    @Test
    public void testReadBoardJson() {
        // Given
        int boardNumber = 1; // Assuming board1
        JsonReader jsonReader = new JsonReader(boardNumber);

        // When
        Board board = jsonReader.readBoardJson();

        // Then
        assertEquals(8, board.getHeight());
        assertEquals(8, board.getWidth());
    }
}
