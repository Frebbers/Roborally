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
package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.SOUTH;

/**
 * For player objects, keeping track of their name, color, position and heading on a board, as well as their command cards.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Player extends Subject {

    final public static int NO_REGISTERS = 5;
    final public static int NO_CARDS = 8;

    final public Board board;

    private String name;
    private String color;

    private Space space;
    private Heading heading = SOUTH;

    private CommandCardField[] program;
    private CommandCardField[] cards;
    private Checkpoint[] checkpoints;

    /**
     * Create a player object with a name and a color, on a board.
     * 
     * @param board the board the player is on
     * @param color the color of the player's robot
     * @param name the name of the player
     */
    public Player(@NotNull Board board, String color, @NotNull String name) {
        this.board = board;
        this.name = name;
        this.color = color;

        this.space = null;
        this.checkpoints = board.getCheckpoints();

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }

        checkpoints = new Checkpoint[board.getCheckpoints().length];
    }

    /**
     * Return name of player.
     * 
     * @return name of player
     */
    public String getName() {
        return name;
    }

    /**
     * Set the name of this player as a non-null string.
     * 
     * @param name new name of player
     */
    public void setName(String name) {
        if (name != null && !name.equals(this.name)) {
            this.name = name;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Return color of player.
     * 
     * @return color of player as a string
     */
    public String getColor() {
        return color;
    }

    /**
     * Set color of player.
     * 
     * @param color new color of player
     */
    public void setColor(String color) {
        this.color = color;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    /**
     * Return space where the player's robot is located.
     * 
     * @return space of the player's robot.
     */
    public Space getSpace() {
        return space;
    }

    /**
     * Set the space of this player.
     * 
     * @param space new space of player
     */
    public void setSpace(Space space) {
        Space oldSpace = this.space;
        if (space != oldSpace &&
                (space == null || space.board == this.board)) {
            this.space = space;
            if (oldSpace != null) {
                oldSpace.setPlayer(null);
            }
            if (space != null) {
                space.setPlayer(this);
            }
            notifyChange();
        }
    }

    /**
     * Return the direction the player's robot is heading.
     * 
     * @return heading of player's robot
     */
    public Heading getHeading() {
        return heading;
    }

    /**
     * Set direction for the player's robot to be heading.
     * 
     * @param heading new heading of the player's robot
     */
    public void setHeading(@NotNull Heading heading) {
        if (heading != this.heading) {
            this.heading = heading;
            notifyChange();
            if (space != null) {
                space.playerChanged();
            }
        }
    }

    /**
     * Return the command card field at the given index of the player's register.
     * 
     * @param i index of the register
     * 
     * @return command card field at the given index of the register
     */
    public CommandCardField getProgramField(int i) {
        return program[i];
    }

    /**
     * Return the command card field at the given index of the player's cards.
     * 
     * @param i index of the cards
     * 
     * @return command card field at the given index of the cards
     */
    public CommandCardField getCardField(int i) {
        return cards[i];
    }

    /**
     * Returns the checkpoint at a specified index.
     *
     * @param index the index of the checkpoint
     * @return the checkpoint at the specified index
     */
    public Checkpoint getCheckpoint(int index) {
        if (index >= 0 && index < checkpoints.length) {
            return checkpoints[index];
        } else {
            return null;
        }
    }

    /**
     * Sets a checkpoint at a specified index.
     *
     * @param checkpoint the checkpoint to set
     */
    public void setCheckpoint(Checkpoint checkpoint) {
        int id = checkpoint.getCheckpointId();
        if (id >= 0 && id < checkpoints.length) {
            checkpoints[id] = checkpoint;
            notifyChange();

            System.out.println("Checkpoint: " + id + " has been reached!");
        }
    }

    /**
     * Returns all checkpoints associated with the player.
     *
     * @return an array of all checkpoints
     */
    public Checkpoint[] getCheckpoints() {
        return checkpoints;
    }
}
