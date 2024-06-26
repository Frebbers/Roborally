package dk.dtu.compute.se.pisd.roborally.model;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import dk.dtu.compute.se.pisd.roborally.controller.BoardData;

import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

/**
 * JsonReader class for reading the board data from a JSON file.
 * Stores the data in a BoardData.java file.
 *
 *  @author Frederik Andersen, s224308 and Nicolai D. Madsen, s213364@dtu.dk
 */
public class JsonReader {

    private final int boardNumber;

    public JsonReader(int boardNumber) {
        this.boardNumber = boardNumber;
    }

    /**
     * Gets the path to the JSON file for the specified board.
     *
     * @return the path to the JSON file as a String.
     */
    private String getBoardPath() {
        final String boardDirectory = "src/main/resources/boards/board";
        String jsonBoardPath = boardDirectory + boardNumber + ".json";
        return jsonBoardPath;
    }

    /**
     * Reads the board data from the JSON file and returns a BoardData object.
     *
     * @return a BoardData object containing the board's details, or null if an error occurs.
     */
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

            List<Spawn> spawns = new ArrayList<>();
            List<Wall> walls = new ArrayList<>();
            List<Checkpoint> checkpoints = new ArrayList<>();
            List<ConveyorBelt> conveyorBelts = new ArrayList<>();
            List<PriorityAntenna> priorityAntennas = new ArrayList<>();

            for (JsonElement spaceElement : spaces) {
                JsonObject space = spaceElement.getAsJsonObject();

                // Handling spawns
                if (space.has("spawns")) {
                    JsonObject spawnObject = space.getAsJsonObject("spawns");
                    JsonArray spawnsArray = spawnObject.getAsJsonArray("instances");
                    for (JsonElement spawnElement : spawnsArray) {
                        JsonObject spawnO = spawnElement.getAsJsonObject();
                        int x = spawnO.get("x").getAsInt() - 1;
                        int y = spawnO.get("y").getAsInt() - 1;

                        // Create a wall and add it to the walls
                        Spawn spawn = new Spawn(x, y);
                        spawns.add(spawn);
                    }
                }

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

                // Handling checkpoints
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

                // Handling priority antennas
                if (space.has("priorityAntennas")) {
                    JsonObject priorityAntennasObject = space.getAsJsonObject("priorityAntennas");
                    JsonArray priorityAntennasArray = priorityAntennasObject.getAsJsonArray("instances");
                    for (JsonElement antennaElement : priorityAntennasArray) {
                        JsonObject antenna = antennaElement.getAsJsonObject();
                        int x = antenna.get("x").getAsInt() - 1;
                        int y = antenna.get("y").getAsInt() - 1;

                        // Create a conveyor belt and add it to the conveyor belts
                        PriorityAntenna priorityAntenna = new PriorityAntenna(x, y);
                        priorityAntennas.add(priorityAntenna);
                        for (Heading heading : Heading.values()) {
                            Wall wall = new Wall(x, y, heading.name(), heading.next().name());
                            walls.add(wall);
                        }
                    }
                }
            }

            return new BoardData(name, width, height, spawns, walls, checkpoints, conveyorBelts, priorityAntennas);

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
