package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;

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
        //check if the destination is null or if the origin has a wall in the direction of the heading
        if(destination == null || origin.getWalls().contains(heading)){return false;}
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

}
