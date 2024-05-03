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

import dk.dtu.compute.se.pisd.roborally.model.*;
import org.jetbrains.annotations.NotNull;

/**
 * Class for controlling game phases and moving robots and cards.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;

    /**
     * Initialize a GameController object with a certain Board.
     *
     * @param board
     */
    public GameController(@NotNull Board board) {
        this.board = board;
    }

    /**
     * This is just some dummy controller operation to make a simple move to see something
     * happening on the board. This method should eventually be deleted
     * @author s224308, s213364
     * @param space the space to which the current player should move
     */
    public void moveCurrentPlayerToSpace(@NotNull Space space)  {
        // Get the current player
        Player currentPlayer = board.getCurrentPlayer();

        // Check if the space is free
        if (space.getPlayer() == null) {
            // Move the current player to the new space
            currentPlayer.setSpace(space);

            // Find the index of the current player
            int currentPlayerIndex = board.getPlayerNumber(currentPlayer);

            // Determine the next player's index
            int nextPlayerIndex = (currentPlayerIndex + 1) % board.getPlayersNumber();

            // Set the next player as the current player
            Player nextPlayer = board.getPlayer(nextPlayerIndex);
            board.setCurrentPlayer(nextPlayer);

            // Increment the move counter on the board
            board.incrementMoveCount();
        }
    }

    /**
     * Set the board's phase to PROGRAMMING. Empty all players' registers and give them eight random command cards.
     */
    // XXX: implemented in the current version
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);

        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            if (player != null) {
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setCard(null);
                    field.setVisible(true);
                }
                for (int j = 0; j < Player.NO_CARDS; j++) {
                    CommandCardField field = player.getCardField(j);
                    field.setCard(generateRandomCommandCard());
                    field.setVisible(true);
                }
            }
        }
    }

    // XXX: implemented in the current version
    private CommandCard generateRandomCommandCard() {
        Command[] commands = Command.values();
        int random = (int) (Math.random() * commands.length);
        return new CommandCard(commands[random]);
    }

    /**
     * Hide all registers but register 0, and set the board's phase to ACTIVATION. Reset current player and step to 0.
     */
    // XXX: implemented in the current version
    public void finishProgrammingPhase() {
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);
        board.setPhase(Phase.ACTIVATION);
        board.setCurrentPlayer(board.getPlayer(0));
        board.setStep(0);
    }

    // XXX: implemented in the current version
    private void makeProgramFieldsVisible(int register) {
        if (register >= 0 && register < Player.NO_REGISTERS) {
            for (int i = 0; i < board.getPlayersNumber(); i++) {
                Player player = board.getPlayer(i);
                CommandCardField field = player.getProgramField(register);
                field.setVisible(true);
            }
        }
    }

    // XXX: implemented in the current version
    private void makeProgramFieldsInvisible() {
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            Player player = board.getPlayer(i);
            for (int j = 0; j < Player.NO_REGISTERS; j++) {
                CommandCardField field = player.getProgramField(j);
                field.setVisible(false);
            }
        }
    }

    /**
     * Set stepMode to false and call continuePrograms().
     */
    // XXX: implemented in the current version
    public void executePrograms() {
        board.setStepMode(false);
        continuePrograms();
    }

    /**
     * Set stepMode to true and call continuePrograms().
     */
    // XXX: implemented in the current version
    public void executeStep() {
        board.setStepMode(true);
        continuePrograms();
    }

    // XXX: implemented in the current version
    private void continuePrograms() {
        do {
            executeNextStep();
        } while (board.getPhase() == Phase.ACTIVATION && !board.isStepMode());
    }

    // XXX: implemented in the current version
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer();
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep();
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    Command command = card.command;
                    executeCommand(currentPlayer, command);
                }
                int nextPlayerNumber = board.getPlayerNumber(currentPlayer) + 1;
                if (nextPlayerNumber < board.getPlayersNumber()) {
                    board.setCurrentPlayer(board.getPlayer(nextPlayerNumber));
                } else {
                    step++;
                    if (step < Player.NO_REGISTERS) {
                        makeProgramFieldsVisible(step);
                        board.setStep(step);
                        board.setCurrentPlayer(board.getPlayer(0));
                    } else {
                        startProgrammingPhase();
                    }
                }
            } else {
                // this should not happen
                assert false;
            }
        } else {
            // this should not happen
            assert false;
        }
    }

    // XXX: implemented in the current version
    public void executeCommand(@NotNull Player player, Command command) {
        if (player.board == board && command != null) {
            // XXX This is a very simplistic way of dealing with some basic cards and
            //     their execution. This should eventually be done in a more elegant way
            //     (this concerns the way cards are modelled as well as the way they are executed).

            switch (command) {
                case FORWARD:
                    this.moveForward(player);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    //Private method added for reuseability in moveForward and fastForward
    /**
     * @author Adrian and Mathias
     * @param player
     */
    private void movePlayerForward(@NotNull Player player) {
        var neighbour = this.board.getNeighbour(player.getSpace(), player.getHeading());
        neighbour.setPlayer(player);
        //Collision logic to be added...

    }

    // Task2
    /**
     * @author Adrian and Mathias
     * @param player
     */
    public void moveForward(@NotNull Player player) {
        movePlayerForward(player);
        //Collision logic to be added...

    }

    // Task2
    /**
     * @author Adrian and Mathias
     * @param player
     */
    public void fastForward(@NotNull Player player) {
        movePlayerForward(player);
        movePlayerForward(player);
        //Collision logic to be added...

    }

    // Task2
    /**
     * @author Adrian and Mathias
     * @param player
     */
    public void turnRight(@NotNull Player player) {
        var currentHeading = player.getHeading();
        player.setHeading(currentHeading.next());
    }

    // Task2
    /**
     * @author Adrian and Mathias
     * @param player
     */
    public void turnLeft(@NotNull Player player) {
        var currentHeading = player.getHeading();
        player.setHeading(currentHeading.prev());
    }

    /**
     * Attempt to move a card from one field to another.
     * This is only done if the target card field is empty.
     *
     * @param source card field of the card being moved
     * @param target card field of the destination
     * @return true if the card was succesfully moved, false otherwise
     */
    public boolean moveCards(@NotNull CommandCardField source, @NotNull CommandCardField target) {
        CommandCard sourceCard = source.getCard();
        CommandCard targetCard = target.getCard();
        if (sourceCard != null && targetCard == null) {
            target.setCard(sourceCard);
            source.setCard(null);
            return true;
        } else {
            return false;
        }
    }

    /**
     * A method called when no corresponding controller operation is implemented yet. This
     * should eventually be removed.
     */
    public void notImplemented() {
        // XXX just for now to indicate that the actual method is not yet implemented
        assert false;
    }



    class ImpossibleMoveException extends Exception {

        private Player player;
        private Space space;
        private Heading heading;

        public ImpossibleMoveException(Player player, Space space, Heading heading) {
            super("Move impossible");
            this.player = player;
            this.space = space;
            this.heading = heading;
        }
    }

}
