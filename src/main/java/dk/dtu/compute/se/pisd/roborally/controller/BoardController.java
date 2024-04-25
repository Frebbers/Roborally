package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;

/**
 * ...
 *
 * @author s224804, Frederik Bode Hendrichsen
 **/
public class BoardController {
    GameController gameController;
    Board board;
    ConveyorBeltController conveyorBeltController;

    public BoardController(GameController gameController){
        this.gameController = gameController;
        this.board = gameController.board;
        this.conveyorBeltController = gameController.beltCtrl;
    }

    /**
     * Method to handle the movement of a player on the board
     * @param origin there must be a player on this space or nothing will happen
     * @param destination
     * @param heading
     * @author s224804, Frederik Bode Hendrichsen
     * @return true if the movement was successful, false otherwise
     */
    public boolean handleMovement(Space origin, Space destination, Heading heading){
        //check if the destination is null
        if(destination == null){return false;}

        // Check if any wall on the origin or destination is blocking this heading
        if (isBlockedByWall(origin, heading) || isBlockedByWall(destination, heading)) {
            return false;
        }

        //check if the destination has a player
        Player target = destination.getPlayer();
        Player pusher = origin.getPlayer();
        if(target == null){
            destination.setPlayer(pusher);
            origin.setPlayer(null);
            return true;
        }
        else if (pusher != null){
            //collision
             if (handleMovement(destination, board.getNeighbour(destination,heading), heading)) {
                 destination.setPlayer(pusher);
                 origin.setPlayer(null);
                 return true;
             }
        }
        return false;
    }
    /**
     *     // Helper method to check if a space is blocked by any wall with a different heading
     * @param space the space to check, if it has a wall that blocks
     * @param heading the heading
     * @author s213364
     * @return true if the blockage was found, false otherwise
     */
    private boolean isBlockedByWall(Space space, Heading heading) {
        for (Wall wall : space.getWalls()) {
            if (wall.getHeading() != heading) {
                // Blockage found
                return true;
            }
        }
        // No blockage
        return false;
    }

}
