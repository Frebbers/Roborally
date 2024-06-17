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
import dk.dtu.compute.se.pisd.roborally.controller.BoardData;
import dk.dtu.compute.se.pisd.roborally.controller.FieldAction;

import java.util.ArrayList;
import java.util.List;

/**
 * ...
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class Space extends Subject {

    private Player player;
    private Spawn spawn;
    private Checkpoint checkpoint;
    private PriorityAntenna priorityAntenna;

    ConveyorBelt belt;
    private List<Wall> walls = new ArrayList<>();
    private List<FieldAction> actions = new ArrayList<>();

    public Board board;

    public final int x;
    public final int y;


    /**
     * Create a space object in a certain position on a board.
     *
     * @param x the x-coordinate of the space on the board
     * @param y the y-coordinate of the space on the board
     */
    public Space(int x, int y) {
        this.x = x;
        this.y = y;
        player = null;
    }

    /**
     * Return player object which is on this space.
     *
     * @return player on this space
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Set a player object to be on this space.
     *
     * @param player player to be on this space
     */
    public void setPlayer(Player player) {
        Player oldPlayer = this.player;
        if (player != oldPlayer &&
                (player == null || board == player.board)) {
            this.player = player;
            if (oldPlayer != null) {
                // this should actually not happen
                oldPlayer.setSpace(null);
            }
            if (player != null) {
                player.setSpace(this);

                if(checkpoint != null){
                    player.setCheckpoint(checkpoint);
                }

            }
            notifyChange();
        }
    }

    /**
     * Return spawn object which is on this space.
     *
     * @return Spawn on this space
     */
    public Spawn getSpawn() {
        return spawn;
    }

    /**
     * Set a spawn object to be on this space.
     *
     * @param spawn spawn to be on this space
     */
    public void setSpawn(Spawn spawn) {
        this.spawn = spawn;
    }

    /**
     * Return checkpoint object which is on this space.
     *
     * @return Checkpoint on this space
     */
    public Checkpoint getCheckpoint() {
        return checkpoint;
    }

    /**
     * Set a checkpoint object to be on this space.
     *
     * @param checkpoint checkpoint to be on this space
     */
    public void setCheckpoint(Checkpoint checkpoint) {
        this.checkpoint = checkpoint;
    }

    public void setBoard(Board board){
        this.board = board;
    }

    public void setConveyorBelt(ConveyorBelt belt) {
        this.belt = belt;
        var ctrl = belt.getBeltCtrl();
        this.actions.add(ctrl);
    }

    /**
     * Set the priority antenna, if one is on this space.
     *
     * @param priorityAntenna Priority Antenna on this space
     *
     * @author s214972@dtu.dk
     */
    public void setPriorityAntenna(PriorityAntenna priorityAntenna) {
        this.priorityAntenna = priorityAntenna;
    }

    /**
     * Set the space as a wall, if one is on this space.
     *
     * @param wall Wall on this space
     *
     * @author s214972@dtu.dk
     */
    public void setWall(Wall wall){
        if(!walls.contains(wall)){
            walls.add(wall);
        }
    }

    public List<Wall> getWalls() {
        return walls;
    }

    public List<FieldAction> getActions() {
        return actions;
    }

    public ConveyorBelt getBelt() {
        return belt;
    }

    void playerChanged() {
        // This is a minor hack; since some views that are registered with the space
        // also need to update when some player attributes change, the player can
        // notify the space of these changes by calling this method.
        notifyChange();
    }

}
