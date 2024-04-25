package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.BoardData;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

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

    public BoardData readBoardJson() {
        String currentBoard = getBoardPath();
        try {
            String jsonBoardContent = new String(Files.readAllBytes(Paths.get(currentBoard)));
            JsonParser parser = new JsonParser();
            JsonObject jsonObject = parser.parse(jsonBoardContent).getAsJsonObject();

            String name = jsonObject.get("name").getAsString();
            int height = jsonObject.get("height").getAsInt();
            int width = jsonObject.get("width").getAsInt();
            JsonArray spaces = jsonObject.getAsJsonArray("spaces");

            List<Wall> walls = new ArrayList<>();
            List<Checkpoint> checkpoints = new ArrayList<>();
            List<ConveyorBelt> conveyorBelts = new ArrayList<>();

            for (JsonElement spaceElement : spaces) {
                JsonObject space = spaceElement.getAsJsonObject();

                // Handling walls
                if (space.has("walls")) {
                    JsonObject wallsObject = space.getAsJsonObject("walls");
                    JsonArray wallsArray = wallsObject.getAsJsonArray("instances");
                    for (JsonElement wallElement : wallsArray) {
                        JsonObject wallO = wallElement.getAsJsonObject();
                        int x = wallO.get("x").getAsInt() - 1;
                        int y = wallO.get("y").getAsInt() - 1;
                        String heading = wallO.get("heading").getAsString();
                        String offset = wallO.get("offset").getAsString();

                        // Create a wall and add it to the walls
                        Wall wall = new Wall(x, y, heading, offset);
                        walls.add(wall);
                    }
                }

                // Handling CheckPoints
                if (space.has("checkpoints")) {
                    JsonObject checkpointsObject = space.getAsJsonObject("checkpoints");
                    JsonArray checkpointA = checkpointsObject.getAsJsonArray("instances");
                    for (JsonElement checkpointElement : checkpointA) {
                        JsonObject checkpointO = checkpointElement.getAsJsonObject();
                        int x = checkpointO.get("x").getAsInt() - 1;
                        int y = checkpointO.get("y").getAsInt() - 1;
                        int id = checkpointO.get("id").getAsInt();

                        // Create a checkpoint and add it to the checkpoints
                        Checkpoint checkpoint = new Checkpoint(x, y, id);
                        checkpoints.add(checkpoint);
                    }
                }

                // Handling conveyor belts
                if (space.has("conveyorBelts")) {
                    JsonObject conveyorBeltsObject = space.getAsJsonObject("conveyorBelts");
                    JsonArray conveyorBeltsArray = conveyorBeltsObject.getAsJsonArray("instances");
                    for (JsonElement beltElement : conveyorBeltsArray) {
                        JsonObject belt = beltElement.getAsJsonObject();
                        int x = belt.get("x").getAsInt() - 1;
                        int y = belt.get("y").getAsInt() - 1;
                        String heading = belt.get("heading").getAsString();

                        // Create a conveyor belt and add it to the conveyor belts
                        ConveyorBelt conveyorBelt = new ConveyorBelt(x, y, heading);
                        conveyorBelts.add(conveyorBelt);
                    }
                }
            }

            return new BoardData(name, width, height, walls, checkpoints, conveyorBelts);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
