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

import java.util.List;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.PLAYER_INTERACTION;

/**
 * Class for controlling game phases and moving robots and cards.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {

    final public Board board;
    public ConveyorBeltController beltCtrl;
    public BoardController boardController;
    private Command nextCommand;
    /**
     * Initialize a GameController object with a certain Board.
     *
     * @param board
     */
    public GameController(@NotNull Board board) {
        this.board = board;
        this.boardController = new BoardController(this);
        this.beltCtrl = new ConveyorBeltController();
    }

    /**
     * Set the board's phase to PROGRAMMING. Empty all players' registers and give them eight random command cards.
     */
    // XXX: implemented in the current version
    public void startProgrammingPhase() {
        board.setPhase(Phase.PROGRAMMING);
        board.updatePlayerTurnOrder();
        board.setCurrentPlayer(board.getPlayerByTurnOrder(0));
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
        //board.setCurrentPlayer(board.getPlayer(0));
        // this line has been commented because it caused problems in the execution of the first register
        board.setPhase(Phase.ACTIVATION);
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
    private void finishNextStep(Player currentPlayer) {
        int step = board.getStep(); // get current register number
        int nextPlayerNumber = board.getPlayerNumberByTurnOrder(currentPlayer) + 1; // iterate player number
        if (nextPlayerNumber < board.getPlayersNumber()) { // if not every player has played this register
            board.setCurrentPlayer(board.getPlayerByTurnOrder(nextPlayerNumber)); // set player to next number
        }
        else { // if every player HAS played this register
            step++; // iterate register number
            if (step < Player.NO_REGISTERS) { // if not all registers are done
                makeProgramFieldsVisible(step); // show next register's cards
                board.setStep(step); // set next register as current
                board.updatePlayerTurnOrder();  // update the priority antenna's player order
                board.setCurrentPlayer(board.getPlayerByTurnOrder(0)); // set first player as current player
            } else { // if all registers are done
                //TO-DO after players have had their turn logic
                for (Player player : board.getPlayers()) {
                    executeFieldActions(player.getSpace()); // execute all field actions (traps etc.)
                }
                onActivationPhaseEnd(); // call method for end of phase
                startProgrammingPhase(); // call method for start of next phase
            }
        }

    }
    // XXX: implemented in the current version
    private void executeNextStep() {
        Player currentPlayer = board.getCurrentPlayer(); // get board's current player
        if (board.getPhase() == Phase.ACTIVATION && currentPlayer != null) {
            int step = board.getStep(); // Get current register number
            if (step >= 0 && step < Player.NO_REGISTERS) {
                CommandCard card = currentPlayer.getProgramField(step).getCard(); // get the card in the current players program field at position for this register
                if (card != null) {
                    nextCommand = card.command; // get the card's command
                    if (nextCommand.isInteractive()) {
                        StartPlayerInteractionPhase(nextCommand.getOptions());
                    }
                    else {executeCommand(currentPlayer, nextCommand);
                    finishNextStep(currentPlayer);
                    } // execute the card's command
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

    /**
     * Function to call when at the end of the activation phase.
     * Here it checks increases the move counter, and checks for any win conditions.
     */
    private void onActivationPhaseEnd(){
        board.incrementMoveCount();

        for(Player player : board.getPlayers()){
            if(player.getCheckpoints().size() == board.getData().checkpoints.size()){
                System.out.println(player.getName() + " has won!");
            }
        }
    }

    /**
     * Execute all field actions on a space.
     * @author Frederik Bode Hendrichsen s224804
     * @param space the space on which the field actions should be executed
     */
    public void executeFieldActions(Space space) {
        space.getActions().forEach(action -> action.doAction(this, space));
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
                case BACK:
                    this.moveBack(player);
                    break;
                case U_TURN:
                    this.turnAround(player);
                    break;
                case AGAIN:
                    this.repeatPrevRegister(player);
                    break;
                default:
                    // DO NOTHING (for now)
            }
        }
    }

    //Private method added for reuseability in moveForward and fastForward

    // Task2
    /**
     * @author Adrian, Mathias and Frederik
     * @param player
     */
    public void moveForward(@NotNull Player player) {
        Space neighbour = this.board.getNeighbour(player.getSpace(), player.getHeading());
        boardController.handleMovement(player.getSpace(), neighbour, player.getHeading());

    }

    // Task2
    /**
     * @author Adrian and Mathias
     * @param player
     */
    public void fastForward(@NotNull Player player) {
        moveForward(player);
        moveForward(player);


    }
    public void StartPlayerInteractionPhase(List<Command> options) {
        board.setPhase(PLAYER_INTERACTION);

        //return Command;
    }
    public void finishPlayerInteractionPhase(Command command){
        System.out.println("Executing command for Player:" + board.getCurrentPlayer().getName());
        executeCommand(board.getCurrentPlayer(),command);
        board.setPhase(Phase.ACTIVATION);
        System.out.println("Activation phase continues, Current player is: " + board.getCurrentPlayer().getName());
        finishNextStep(board.getCurrentPlayer());
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
     * @author Adrian
     * @param player
     * This method makes a player move backwards.
     */

    public void moveBack(@NotNull Player player) {
        var neighbour = this.board.getNeighbour(player.getSpace(), player.getHeading().opposite());
        boardController.handleMovement(player.getSpace(), neighbour, player.getHeading());
    }

    public void repeatPrevRegister(@NotNull Player player) {
        player.getPreviousCardField();
    }

    /**
     * @author Adrian
     * @param player
     */

    public void turnAround(@NotNull Player player) {
        player.setHeading(player.getHeading().opposite());
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

    public SaveGameState saveGameState() {
       return null;
    }

    /**
     * This method returns the ConveyorBeltController.
     *
     * @return the ConveyorBeltController instance
     */
    public ConveyorBeltController getBeltCtrl() {
        return beltCtrl;
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

    public Command getNextCommand() {
        return nextCommand;
    }
}
