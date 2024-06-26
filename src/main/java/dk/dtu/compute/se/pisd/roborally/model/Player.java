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
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.view.CheckpointView;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import static dk.dtu.compute.se.pisd.roborally.controller.AppController.localPlayer;
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

    public Board board;

    private RobotType robotType;


    private Space space;
    private Heading heading = SOUTH;

    private Command previousCommand;

    //The programming cards added during the programming phase
    private CommandCardField[] program;

    //The command cards that the player has in their hand
    private CommandCardField[] cards;
    private List<Checkpoint> checkpoints = new ArrayList<>();

    // API References
    private String name;
    private Long id;
    private PlayerState state;
    private Long gameId;

    public Player(Long id, String name, RobotType robotType, PlayerState state, Long gameId){
        // Set the information sent by the PlayerDTO from the API
        this.id = id;
        this.name = name;
        this.robotType = robotType;
        this.state = state;
        this.gameId = gameId;

        // Set initial player variables
        this.space = null;

        program = new CommandCardField[NO_REGISTERS];
        for (int i = 0; i < program.length; i++) {
            program[i] = new CommandCardField(this);
        }

        cards = new CommandCardField[NO_CARDS];
        for (int i = 0; i < cards.length; i++) {
            cards[i] = new CommandCardField(this);
        }
    }

    /**
     * Set the board of the player
     *
     * @return the board the player is on
     */
    public Board getBoard(){
        return board;
    }

    /**
     * Set the active board of player.
     *
     * @param board the board to play on
     */
    public void setBoard(Board board){
        this.board = board;
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
     * Return the amount of program fields the player has
     *
     * @return integer of the amount of program fields
     */
    public int getProgramFieldCount(){
        return program.length;
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
    public List<String> getAllProgramCardsFromCurrentPlayer() {
        List<String> moveTypes = new ArrayList<>();
        Player currentPlayer = board.getCurrentPlayer();
        for (int i = 0; i < currentPlayer.getProgramFieldCount(); i++) {
            CommandCard programCard = currentPlayer.getProgramField(i).getCard();

            if (programCard != null) {
                moveTypes.add((programCard.getName()));
            } else {
                moveTypes.add("Empty");
            }
        }
            return moveTypes;
    }
    /**
     * Set the command card field at the given index of the player's register.
     *
     * @param i index of the register
     *
     */
    public void setProgramField(int i, CommandCardField cardField) {
        if (i >= 0 && i < program.length) {
            program[i] = cardField;
            notifyChange();
        }
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
     *
     * @param  command the command the player last used
     */
    public void setPreviousCommand(Command command){
        this.previousCommand = command;
    }

    /**
     *
     * @return command card field at the previous index of cards
     */
    public Command getPreviousCommand(){
        return previousCommand;
    }
    /**
     * Returns the checkpoint at a specified index.
     *
     * @param index the index of the checkpoint
     * @return the checkpoint at the specified index
     */
    public Checkpoint getCheckpoint(int index) {
        if (index >= 0 && index < checkpoints.size()) {
            return checkpoints.get(index);
        } else {
            return null;
        }
    }

    /**
     * Sets a checkpoint at a specified index only if it is the next checkpoint in the order.
     *
     * @param checkpoint the checkpoint to set
     */
    public void setCheckpoint(Checkpoint checkpoint) {
        // Check if the checkpoint already exists in the list
        if (checkpoints.contains(checkpoint)) return;

        // Determine the ID of the next expected checkpoint in sequence
        int nextCheckpointId = checkpoints.isEmpty() ? 1 : checkpoints.get(checkpoints.size() - 1).getCheckpointId() + 1;

        // Add the checkpoint only if its ID matches the next expected ID
        if (checkpoint.getCheckpointId() == nextCheckpointId) {
            // Add the checkpoint
            checkpoints.add(checkpoint);

            // Remove the CheckpointView on the Board if we are the local player
            if (checkpoint.getCheckpointView() != null && Objects.equals(this.id, localPlayer.getId())) {
                {
                    checkpoint.getCheckpointView().setCheckpointAsReached();
                    notifyChange();
                }
            }
        }
    }
    /**
     * Returns all checkpoints associated with the player.
     *
     * @return an array of all checkpoints
     */
    public List<Checkpoint> getCheckpoints() {
        return checkpoints;
    }

    /**
     * Get id of player.
     *
     * @return id of the player
     */
    public Long getId() {
        return id;
    }

    /**
     * Set id of player
     *
     * @param id id of the player
     */
    public void setId(Long id) {
        this.id = id;
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
     * Return the robot type of player.
     *
     * @return color of player as a string
     */
    public RobotType getRobotType() {
        return robotType;
    }

    /**
     * Set the robot type of player.
     *
     * @param robotType new color of player
     */
    public void setRobotType(RobotType robotType) {
        this.robotType = robotType;
        notifyChange();
        if (space != null) {
            space.playerChanged();
        }
    }

    /**
     * Get player state of player.
     *
     * @return the player state of the player
     */
    public PlayerState getState(){
        return state;
    }

    /**
     * Set player state of the player
     *
     * @param state new player state of the player
     */
    public void setState(PlayerState state){
        this.state = state;
    }

    /**
     * Set color of player.
     *
     * @return Get the gameID of the player
     */
    public Long getGameId(){
        return gameId;
    }

    /**
     * Set color of player.
     *
     * @param gameId new game ID of the player
     */
    public void setGameId(Long gameId){
        this.gameId = gameId;
    }
}
