package dk.dtu.compute.se.pisd.roborally.controller;
import dk.dtu.compute.se.pisd.roborally.model.JsonReader;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class JsonReaderTest {

    @Test
    public void testReadBoard1Json() {
        // Given
        int boardNumber = 1; // Assuming board1
        JsonReader jsonReader = new JsonReader(boardNumber);

        // When
        BoardData board = jsonReader.readBoardJson();

        // Then
        assertEquals(8, board.width);
        assertEquals(8, board.height);
    }
    @Test
    public void testReadBoard2Json() {
        // Given
        int boardNumber = 2; // Assuming board2
        JsonReader jsonReader = new JsonReader(boardNumber);

        // When
        BoardData board = jsonReader.readBoardJson();

        // Then
        assertEquals(12, board.height);
        assertEquals(12, board.width);


    }
}
