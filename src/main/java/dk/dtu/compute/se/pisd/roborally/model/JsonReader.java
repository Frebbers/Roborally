package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.sql.Array;
import java.util.ArrayList;
import java.util.Arrays;

public class JsonReader {

    private final int boardNumber;

    public JsonReader(int boardNumber) {
        this.boardNumber = boardNumber;
    }

    private String getBoardPath() {
        final String boardDirectory = "src/main/resources/boards/board";
        String jsonBoardPath = boardDirectory + boardNumber + ".json";
        return jsonBoardPath;
    }

    /*
    public Board readBoardJson() {
        String currentBoard = getBoardPath();
        try {
            String jsonBoardContent = new String(Files.readAllBytes(Paths.get(currentBoard)));
            System.out.println(jsonBoardContent); //Correct here so far.

            JsonParser parser = new JsonParser();

            JsonObject o = parser.parse(jsonBoardContent).getAsJsonObject();
            int height = o.get("height").getAsInt();
            int width = o.get("width").getAsInt();
            JsonArray walls = o.getAsJsonArray("walls");

            System.out.println("Height and Width from json: " + height + " and " + width + " and Walls: " + walls);


            return new Board(width, height, "Empty 8x8 board..");

        } catch (IOException e) {
            e.printStackTrace();
            return null;
        }
    }
     */

    public Board readBoardJson() {
        String currentBoard = getBoardPath();
        try {
            String jsonBoardContent = new String(Files.readAllBytes(Paths.get(currentBoard)));

            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(jsonBoardContent).getAsJsonObject();

            int height = jsonObject.get("height").getAsInt();
            int width = jsonObject.get("width").getAsInt();
            JsonArray spaces = jsonObject.getAsJsonArray("spaces");

            // Create a list of checkpoints
            ArrayList<Checkpoint> checkpoints = new ArrayList<Checkpoint>();

            //Create a list of coveyorbelt cords
            ArrayList<ConveyorBelt> belts = new ArrayList<>();

            //System.out.println("Board Width: " + width + ", Height: " + height);

            for (JsonElement spaceElement : spaces) {
                JsonObject space = spaceElement.getAsJsonObject();

                // Handling conveyor belts
                if (space.has("conveyorBelts")) {
                    JsonArray conveyorBelts = space.getAsJsonArray("conveyorBelts");
                    for (JsonElement beltElement : conveyorBelts) {
                        JsonObject belt = beltElement.getAsJsonObject();
                        if (belt.has("x") && belt.has("y")) {
                            int x = belt.get("x").getAsInt() - 1;
                            int y = belt.get("y").getAsInt() - 1;
                            String heading = belt.get("heading").getAsString();

                            ConveyorBelt conveyorBelt = new ConveyorBelt(x,y,heading);
                            belts.add(conveyorBelt);
                            //System.out.println("Conveyor Belt at (" + x + ", " + y + ") heading " + heading);
                        }
                    }
                }

                // Handling walls
                if (space.has("walls")) {
                    JsonArray walls = space.getAsJsonArray("walls");
                    for (JsonElement wallElement : walls) {
                        JsonObject wall = wallElement.getAsJsonObject();
                        int x = wall.get("x").getAsInt() - 1;
                        int y = wall.get("y").getAsInt() - 1;
                        String heading = wall.get("heading").getAsString();
                        //System.out.println("Wall at (" + x + ", " + y + ") heading " + heading);
                    }
                }

                // Handling CheckPoints
                if (space.has("CheckPoints")) {
                    JsonArray checkpointA = space.getAsJsonArray("CheckPoints");
                    for (JsonElement checkpointElement : checkpointA) {
                        JsonObject checkpointO = checkpointElement.getAsJsonObject();
                        int x = checkpointO.get("x").getAsInt() - 1;
                        int y = checkpointO.get("y").getAsInt() - 1;
                        int id = checkpointO.get("id").getAsInt();

                        Checkpoint checkpoint = new Checkpoint(x, y, id);
                        checkpoints.add(checkpoint);
                        //System.out.println("Checkpoint at (" + x + ", " + y + ") with ID: " + id);
                    }
                }
            }

            return new Board(width, height, "Empty 8x8 board..", checkpoints.toArray(new Checkpoint[0]), belts);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
