/*
 *  This file is part of the initial project provided for the
 *  course "Project in Software Development (02362)" held at
 *  DTU Compute at the Technical University of Denmark.
 *
 *  Copyright (C) 2019, 2020: Ekkart Kindler, ekki@dtu.dk
 *
 *  This software is free software; you can redistribute it and/or modify
 *  it under the terms of the GNU General Public License as published by
 *  the Free Software Foundation; version 2 of the License.
 *
 *  This project is distributed in the hope that it will be useful,
 *  but WITHOUT ANY WARRANTY; without even the implied warranty of
 *  MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 *  GNU General Public License for more details.
 *
 *  You should have received a copy of the GNU General Public License
 *  along with this project; if not, write to the Free Software
 *  Foundation, Inc., 59 Temple Place, Suite 330, Boston, MA  02111-1307  USA
 *
 */
package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import org.jetbrains.annotations.NotNull;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class ConveyorBeltController extends FieldAction {

    private Heading heading;

    public Heading getHeading() {
        return heading;
    }

    public void setHeading(Heading heading) {
        this.heading = heading;
    }

    /**
     * Perform the action related to the conveyor belt on the given space
     *
     * @param gameController the game controller
     * @param space          the space with the conveyor belt
     * @return true if the action was successfully performed, false otherwise
     * @author s224804 Frederik Bode Hendrichsen
     */
    @Override
    public boolean doAction(@NotNull GameController gameController, @NotNull Space space) {
        // Perform the action related to the conveyor belt on the given space
        // For example, you might move the player on the conveyor belt space

        // You can access the board from the game controller
        Board board = gameController.board;

        // You might also need to access the player on the space
        Player player = space.getPlayer();

        // Attempt to the player in the direction of the conveyor belt
        if (player != null) {
            if (gameController.boardController.handleMovement
                    (space, board.getNeighbour(space, this.heading), this.heading)) {
                return true;
            }

        }
        //No player on the conveyor belt
        return false;

    }
}