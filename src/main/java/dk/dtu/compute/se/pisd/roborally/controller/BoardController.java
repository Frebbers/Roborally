package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;

/**
 * Handles the logic for the board, including player movement and collision detection.
 */
public class BoardController {
    GameController gameController;
    Board board;
    ConveyorBeltController conveyorBeltController;

    public BoardController(GameController gameController) {
        this.gameController = gameController;
        this.board = gameController.board;
        this.conveyorBeltController = gameController.beltCtrl;
    }

    /**
     * Method to handle the movement of a player on the board
     * @param origin there must be a player on this space or nothing will happen
     * @param destination the destination of the moving player
     * @param heading the direction the player is moving
     * @return true if the movement was successful, false otherwise
     * @author s224804
     */
    public boolean handleMovement(Space origin, Space destination, Heading heading) {
        // Check if the destination is null
        if (destination == null) {
            return false;
        }

        // Check if any wall on the origin is blocking the movement in the current heading
        if (isBlockedByWall(origin, heading)) {
            return false;
        }

        // Check if any wall on the destination is blocking the movement in the opposite heading
        if (isBlockedByWall(destination, heading.opposite())) {
            return false;
        }

        // Check if the destination has a player
        Player target = destination.getPlayer();
        Player pusher = origin.getPlayer();
        if (target == null) {
            destination.setPlayer(pusher);
            origin.setPlayer(null);
            return true;
        } else if (pusher != null) {
            // Collision
            if (handleMovement(destination, board.getNeighbour(destination, heading), heading)) {
                destination.setPlayer(pusher);
                origin.setPlayer(null);
                return true;
            }
        }
        return false;
    }

    /**
     * Helper method to check if a space is blocked by any wall with a different heading
     * @param space the space to check, if it has a wall that blocks
     * @param heading the heading
     * @return true if the blockage was found, false otherwise
     */
    private boolean isBlockedByWall(Space space, Heading heading) {
        for (Wall wall : space.getWalls()) {
            if (heading == wall.getOffset()) {
                return true; // Blockage found
            }
        }
        return false; // No blockage
    }

}
