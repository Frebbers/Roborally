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
     */
    public void handleMovement(Space origin, Space destination, Heading heading){
        Player target = destination.getPlayer();
        Player pusher = origin.getPlayer();
        if(target == null){
            destination.setPlayer(pusher);
            origin.setPlayer(null);
        }
        else if (pusher != null){
            //collision
            handleMovement(destination, board.getNeighbour(destination,heading), heading);
            destination.setPlayer(pusher);
            origin.setPlayer(null);
        }
    }

}
