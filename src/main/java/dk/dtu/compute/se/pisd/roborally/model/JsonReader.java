package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;

public class JsonReader {

    private final int boardNumber;

    public JsonReader(int boardNumber) {
        System.out.println("Initializing JSON Reader: " + boardNumber);
        this.boardNumber = boardNumber;
    }

    private String getBoardPath() {
        final String boardDirectory = "src/main/java/dk/dtu/compute/se/pisd/roborally/json/boards/board";
        String jsonBoardPath = boardDirectory + boardNumber + ".json";
        System.out.println(jsonBoardPath);
        return jsonBoardPath;
    }

    public Board readBoardJson() {
        String currentBoard = getBoardPath();
        try {
            String jsonBoardContent = new String(Files.readAllBytes(Paths.get(currentBoard)));
            System.out.println(jsonBoardContent); //Correct here so far.

            JsonParser parser = new JsonParser();

            JsonObject o = parser.parse(jsonBoardContent).getAsJsonObject();
            int height = o.get("Height").getAsInt();
            int width = o.get("Width").getAsInt();
            JsonArray walls = o.getAsJsonArray("Walls");

            System.out.println("Height and Width from json: " + height + " and " + width + " and Walls: " + walls);

            return new Board(width, height, "Empty 8x8 board..");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
}
