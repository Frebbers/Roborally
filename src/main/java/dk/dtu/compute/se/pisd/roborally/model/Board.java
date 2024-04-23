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

import java.util.ArrayList;
import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.INITIALISATION;

/**
 * For making board objects with spaces and whatever is inside them.
 * Also keeps game info such as the current player, game phase, steps
 * and move counter.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Board extends Subject {

    public final int width;

    public final int height;

    public final String boardName;

    private Integer gameId;

    private final Space[][] spaces;

    private final List<Player> players = new ArrayList<>();

    private Player current;

    private Phase phase = INITIALISATION;

    private int step = 0;

    private int moveCounter = 0;

    private boolean stepMode;

    /**
     * Initialize a Board object with certain dimensions of empty spaces as well as a name.
     * 
     * @param width   width of the board
     * @param height   height of the board
     * @param boardName name of board
     */
    public Board(int width, int height, @NotNull String boardName) {
        this.boardName = boardName;
        this.width = width;
        this.height = height;
        spaces = new Space[width][height];
        for (int x = 0; x < width; x++) {
            for(int y = 0; y < height; y++) {
                Space space = new Space(this, x, y);
                spaces[x][y] = space;
            }
        }
        this.stepMode = false;
    }

    /**
     * Initialize a Board object with certain dimensions of empty spaces and a default name.
     *
     * @param width
     * @param height
     */
    public Board(int width, int height) {
        this(width, height, "defaultBoard");
    }

    /**
     * Return this Board's gameId.
     * 
     * @return gameId
     */
    public Integer getGameId() {
        return gameId;
    }

    /**
     * Attempts to set the gameId. Throws an exception if the game already has an ID.
     * 
     * @param gameId new gameId
     */
    public void setGameId(int gameId) {
        if (this.gameId == null) {
            this.gameId = gameId;
        } else {
            if (!this.gameId.equals(gameId)) {
                throw new IllegalStateException("A game with a set id may not be assigned a new id!");
            }
        }
    }

    /**
     * Return the Space object of the given coordinates.
     * 
     * @param x
     * @param y
     * @return Space object of the given coordinates
     */
    public Space getSpace(int x, int y) {
        if (x >= 0 && x < width &&
                y >= 0 && y < height) {
            return spaces[x][y];
        } else {
            return null;
        }
    }

    /**
     * Return the number of players.
     * 
     * @return number of players
     */
    public int getPlayersNumber() {
        return players.size();
    }

    /**
     * Add the given player to the Board's list of players, if they are not currently in it.
     * 
     * @param player to be added to the list
     */
    public void addPlayer(@NotNull Player player) {
        if (player.board == this && !players.contains(player)) {
            players.add(player);
            notifyChange();
        }
    }

    /**
     * Return the Player object based on their index in the Board's Player list.
     * 
     * @param i index of the Player in the list
     * @return Player object of the player
     */
    public Player getPlayer(int i) {
        if (i >= 0 && i < players.size()) {
            return players.get(i);
        } else {
            return null;
        }
    }

    /**
     * Return current player.
     * 
     * @return Player object of current player
     */
    public Player getCurrentPlayer() {
        return current;
    }

    /**
     * Set current player to the given Player if they are in the list of players.
     * 
     * @param player 
     */
    public void setCurrentPlayer(Player player) {
        if (!players.contains(player)) this.addPlayer(player);
        if (player != this.current) {
            this.current = player;
            notifyChange();
        }
    }

    /**
     * Return the board's current phase.
     * 
     * @return current phase of the board
     */
    public Phase getPhase() {
        return phase;
    }

    /**
     * Set the phase of the board.
     * 
     * @param phase the phase to become active
     */
    public void setPhase(Phase phase) {
        if (phase != this.phase) {
            this.phase = phase;
            notifyChange();
        }
    }

    /**
     * Return the step of the board.
     * 
     * @return the current step of the board
     */
    public int getStep() {
        return step;
    }

    /**
     * Set the step of the board.
     * 
     * @param step the step to be set
     */
    public void setStep(int step) {
        if (step != this.step) {
            this.step = step;
            notifyChange();
        }
    }

    /**
     * Check whether the board is in step mode.
     * 
     * @return true if the board is in step mode, false otherwise
     */
    public boolean isStepMode() {
        return stepMode;
    }

    /**
     * Set step mode on or off.
     * 
     * @param stepMode true if step mode is to be on, false otherwise
     */
    public void setStepMode(boolean stepMode) {
        if (stepMode != this.stepMode) {
            this.stepMode = stepMode;
            notifyChange();
        }
    }

    /**
     * Return the index of a player in the board's player list.
     * 
     * @param player the player to return the index of
     * @return the index of the player in the board's player list
     */
    public int getPlayerNumber(@NotNull Player player) {
        if (player.board == this) {
            return players.indexOf(player);
        } else {
            return -1;
        }
    }

    /**
     * Returns the neighbour of the given space of the board in the given heading.
     * The neighbour is returned only, if it can be reached from the given space
     * (no walls or obstacles in either of the involved spaces); otherwise,
     * null will be returned.
     *
     * @param space the space for which the neighbour should be computed
     * @param heading the heading of the neighbour
     * @return the space in the given direction; null if there is no (reachable) neighbour
     */
    public Space getNeighbour(@NotNull Space space, @NotNull Heading heading) {
        int x = space.x;
        int y = space.y;
        switch (heading) {
            case SOUTH:
                y = (y + 1) % height;
                break;
            case WEST:
                x = (x + width - 1) % width;
                break;
            case NORTH:
                y = (y + height - 1) % height;
                break;
            case EAST:
                x = (x + 1) % width;
                break;
        }

        return getSpace(x, y);
    }

    /**
     * @author s224308, s213364
     */
    public void incrementMoveCount() {
        moveCounter++;
        notifyChange();
    }

    /**
     * @author s224804
     * @param moveCount the amount of moves to set the move count to
     */

    public void setMoveCount(int moveCount) {
        this.moveCounter = moveCount;
    }

    /**
     * @author s224308, s213364
     * @return int the amount of moves that have been made on the board
     */
    public int getMoveCount() {
        return moveCounter;
    }

    /**
     * Return a string describing the current status of the game with the phase,
     * current player, current step and the move count.
     * 
     * @return the status message
     */
    public String getStatusMessage() {
        // This is actually a view aspect, but for making the first task easy for
        // the students, this method gives a string representation of the current
        // status of the game (specifically, it shows the phase, the player and the step)

        return "Phase: " + getPhase().name() +
                ", Player = " + getCurrentPlayer().getName() +
                ", Step: " + getStep() +
                ", Move: " + getMoveCount();
    }

    public int getHeight() {
        return height;
    }
    public int getWidth() {
        return width;
    }
}
