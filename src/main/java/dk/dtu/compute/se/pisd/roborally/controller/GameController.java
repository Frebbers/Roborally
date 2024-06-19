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
import dk.dtu.compute.se.pisd.roborally.model.DTO.MoveDTO;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.application.Platform;
import javafx.scene.control.*;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.ScheduledFuture;
import java.util.concurrent.TimeUnit;
import java.util.function.Consumer;

import static dk.dtu.compute.se.pisd.roborally.model.Phase.ACTIVATION;
import static dk.dtu.compute.se.pisd.roborally.model.Phase.PLAYER_INTERACTION;

/**
 * Class for controlling game phases and moving robots and cards.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class GameController {
    private AppController appController;

    final public Board board;
    public ConveyorBeltController beltCtrl;
    public BoardController boardController;
    private Command nextCommand;

    private final ScheduledExecutorService scheduler = Executors.newScheduledThreadPool(1);
    private List<ScheduledFuture<?>> scheduledTasks = new ArrayList<>();


    /**
     * Initialize a GameController object with a certain Board.
     *
     * @param board
     */
    public GameController(@NotNull AppController appController, @NotNull Board board) {
        this.appController = appController;
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
        int random = 1 + (int) (Math.random() * commands.length - 1); // Plus 1 to remove the Empty command card
        return new CommandCard(commands[random]);
    }

    /**
     * Hide all registers but register 0, and set the board's phase to ACTIVATION. Reset current player and step to 0.
     */
    // XXX: implemented in the current version
    public void finishProgrammingPhase() {
        // Get the API Services from the App Controller
        ApiServices apiServices = appController.getApiServices();

        // Get the local player from the board
        Player localPlayer = board.getLocalPlayer(AppController.localPlayer);

        // Get the game ID of the player
        Long gameId = localPlayer.getGameId();

        // Add the moves from the ProgramCard in the Registers of the local player
        List<String> localMoveTypes = new ArrayList<>();
        for(int i = 0; i < localPlayer.getProgramFieldCount(); i++){
            CommandCard programCard = localPlayer.getProgramField(i).getCard();

            if(programCard != null){
                localMoveTypes.add((programCard.getName()));
            }
            else {
                localMoveTypes.add("Empty");
            }
        }

        // Upload the moves to the server
        apiServices.createMove(gameId, localPlayer.getId(), board.getMoveCount(), localMoveTypes);

        // Make the cards invisible
        makeProgramFieldsInvisible();
        makeProgramFieldsVisible(0);

        // Poll to the server if all the players are ready
        startPollingForMoves(gameId, board.getMoveCount(), moves -> {
            // Ensure that any moves has been fetched
            if(moves != null && !moves.isEmpty()){
                // Loop through the players and set their
                for(Player player : board.getPlayers()){
                    // Return if this is the local player
                    if(player == localPlayer) continue;

                    // Get the move types object from the player
                    MoveDTO moveDTO = moves.stream().filter(m -> m.getPlayerId() == player.getId()).findFirst().orElse(null);

                    // Get the list of moves from the moveDTO
                    List<String> clientMoveTypes = moveDTO.getMoveTypes();

                    for(int i = 0; i < clientMoveTypes.size(); i++){
                        // Create a CommandCard from the String
                        Command command = Command.fromString(clientMoveTypes.get(i));
                        CommandCard commandCard = new CommandCard(command);

                        // Update the card for the programField
                        player.getProgramField(i).setCard(commandCard);
                    }
                }

                // Activate the robots on the board
                board.setPhase(Phase.ACTIVATION);
                board.setStep(0);

                // Execute the robot programs
                executePrograms();
            }
        });
    }

    /**
     * Starts polling the game state at fixed intervals and updates the game based on the responses.
     * Stores the ScheduledFuture for possible cancellation.
     */
    public void startPollingForMoves(Long gameId, Integer turnIndex, Consumer<List<MoveDTO>> callback) {
        ApiServices apiServices = appController.getApiServices();
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            Integer readyPlayers = apiServices.getPlayerReadyCount(gameId, turnIndex);
            if (readyPlayers == board.getPlayers().size()) {
                List<MoveDTO> moveTypes = apiServices.getAllMoves(gameId, turnIndex);
                stopPollingForMoves();
                callback.accept(moveTypes);
            }
        }), 0, 500, TimeUnit.MILLISECONDS);
        scheduledTasks.add(future);
    }

    public void startPollingForInteraction(Long gameId, Integer turnIndex, Player currentPlayer, Consumer<MoveDTO> callback) {
        ApiServices apiServices = appController.getApiServices();
        ScheduledFuture<?> future = scheduler.scheduleAtFixedRate(() -> Platform.runLater(() -> {
            PlayerDTO interactivePlayer = apiServices.getPlayerById(currentPlayer.getId());
            if (interactivePlayer.getState() == PlayerState.READY) {
                MoveDTO interaction = apiServices.getPlayerMoves(gameId, currentPlayer.getId(), turnIndex);
                if (interaction != null && interaction.getMoveTypes() != null && !interaction.getMoveTypes().isEmpty()) {
                    stopPollingForMoves();
                    callback.accept(interaction);
                    finishPollingInteractionPhase(interaction.getMoveTypes());
                }
            }
        }), 0, 500, TimeUnit.MILLISECONDS);
        scheduledTasks.add(future);
    }


    /**
     * Stops all polling tasks and clears the list of ScheduledFuture.
     */
    public void stopPollingForMoves() {
        for (ScheduledFuture<?> task : scheduledTasks) {
            if (!task.isDone()) {
                task.cancel(false); // Cancel if not already done
            }
        }
        scheduledTasks.clear();
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

            if(!Objects.equals(player.getId(), AppController.localPlayer.getId())){
                for (int j = 0; j < Player.NO_REGISTERS; j++) {
                    CommandCardField field = player.getProgramField(j);
                    field.setVisible(false);
                }
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

    private void continuePrograms() {
        scheduleNextStep();
    }

    /**
     * Schedules the next step in the game loop and adds the future to the list for management.
     */
    private void scheduleNextStep() {
        if (!scheduler.isShutdown()) {
            ScheduledFuture<?> future = scheduler.schedule(() -> {
                if (board.getPhase() == Phase.ACTIVATION && !board.isStepMode()) {
                    executeNextStep();
                    scheduleNextStep();
                }
            }, 1000, TimeUnit.MILLISECONDS);
            scheduledTasks.add(future);
        }
    }


    /**
     *   Shutdown the scheduler carefully
     */
    public void shutdownScheduler() {
        // Ensure all tasks are cancelled first
        stopPollingForMoves();
        scheduler.shutdown();
        try {
            if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                scheduler.shutdownNow();
            }
        } catch (InterruptedException e) {
            scheduler.shutdownNow();
        }
    }

    private void finishNextStep(Player currentPlayer) {
        int step = board.getStep(); // get current register number
        int nextPlayerNumber = board.getPlayerNumberByTurnOrder(currentPlayer) + 1; // iterate player number
        if (nextPlayerNumber < board.getPlayersNumber()) { // if not every player has played this register
            board.setCurrentPlayer(board.getPlayerByTurnOrder(nextPlayerNumber)); // set player to next number
        } else { // if every player HAS played this register
            step++; // iterate register number
            if (step < Player.NO_REGISTERS) { // if not all registers are done
                makeProgramFieldsVisible(step); // show next register's cards
                board.setStep(step); // set next register as current
                board.updatePlayerTurnOrder();  // update the priority antenna's player order
                board.setCurrentPlayer(board.getPlayerByTurnOrder(0)); // set first player as current player
            } else { // if all registers are done
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
                CommandCard card = currentPlayer.getProgramField(step).getCard();
                if (card != null) {
                    nextCommand = card.command; // get the card's command
                    if (nextCommand.isInteractive()) {
                        StartPlayerInteractionPhase(nextCommand.getOptions());
                    } else {
                        executeCommand(currentPlayer, nextCommand);
                        finishNextStep(currentPlayer);
                    } // execute the card's command
                } else {
                    finishNextStep(currentPlayer);
                }
            }
        }
    }



    /**
     * Function to call when at the end of the activation phase.
     * Here it checks increases the move counter, and checks for any win conditions.
     */
    private void onActivationPhaseEnd(){
        board.incrementMoveCount();
        Player winner;
        StringBuilder result = new StringBuilder();

        // Check if there is a winner
        winner = board.getPlayers().stream().filter(player -> player.getCheckpoints().size() == board.getData().checkpoints.size()).findFirst().orElse(null);

        // If there is a winner, prepare and display the results
        if (winner != null) {
            result.append(winner.getName()).append(" has won!\n\n");
            for (Player player : board.getPlayers()) {
                if (player != winner) {
                    result.append(player.getName())
                            .append(" has ")
                            .append(player.getCheckpoints().size())
                            .append(" checkpoints.\n");
                }
            }

            // Display the results in a popup window
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.NONE);
                alert.setTitle("Game Results");
                alert.setHeaderText("Congratulations, " + winner.getName() + "!");

                // Using a TextArea inside the alert to handle potentially large amounts of text
                TextArea textArea = new TextArea(result.toString());
                textArea.setEditable(false);
                textArea.setWrapText(true);
                textArea.setMaxWidth(Double.MAX_VALUE);
                textArea.setMaxHeight(Double.MAX_VALUE);
                alert.getDialogPane().setContent(textArea);

                // Add a leave button manually
                ButtonType leaveButton = new ButtonType("Leave Game", ButtonBar.ButtonData.OK_DONE);
                alert.getDialogPane().getButtonTypes().add(leaveButton);

                // Show the alert and wait for the user to close it
                alert.showAndWait().ifPresent(response -> {
                    if (response == leaveButton) {
                        appController.leave(true);
                    }
                });
            });

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
                    player.setPreviousCommand(command);
                    break;
                case RIGHT:
                    this.turnRight(player);
                    player.setPreviousCommand(command);
                    break;
                case LEFT:
                    this.turnLeft(player);
                    player.setPreviousCommand(command);
                    break;
                case FAST_FORWARD:
                    this.fastForward(player);
                    player.setPreviousCommand(command);
                    break;
                case BACK:
                    this.moveBack(player);
                    player.setPreviousCommand(command);
                    break;
                case U_TURN:
                    this.turnAround(player);
                    player.setPreviousCommand(command);
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
        Player currentPlayer = board.getCurrentPlayer();
        currentPlayer.setState(PlayerState.INTERACTING);
        ApiServices apiServices = appController.getApiServices();
        apiServices.updatePlayerInteractionState(currentPlayer.getId());

        for (Player player : board.getPlayers()) {
            if (player != currentPlayer) {
                startPollingForInteraction(currentPlayer.getGameId(), board.getMoveCount(), currentPlayer, move -> {
                    if (move != null && move.getMoveTypes() != null && !move.getMoveTypes().isEmpty()) {
                        if (currentPlayer.getState() == PlayerState.READY)
                            finishPollingInteractionPhase(move.getMoveTypes());
                    }
                });
            }
        }
    }



    public void finishPlayerInteractionPhase(Command command) {
        stopPollingForMoves();
        Player currentPlayer = board.getCurrentPlayer();
        List<String> moveTypes = currentPlayer.getAllProgramCardsFromCurrentPlayer();
        int step = board.getStep();
        moveTypes.set(step, command.displayName);
        Command cmd = Command.fromString(command.displayName);
        CommandCard commandCard = new CommandCard(cmd);
        currentPlayer.getProgramField(step).setCard(commandCard);

        ApiServices apiServices = appController.getApiServices();
        apiServices.updateMoves(currentPlayer.getGameId(), currentPlayer.getId(), board.getMoveCount(), moveTypes);
        apiServices.updatePlayerState(currentPlayer.getId());
        finishPollingInteractionPhase(moveTypes);
    }


    public void finishPollingInteractionPhase(List<String> moveTypes) {
        Player currentPlayer = board.getCurrentPlayer();
        for (int i = 0; i < moveTypes.size(); i++) {
            Command command = Command.fromString(moveTypes.get(i));
            CommandCard commandCard = new CommandCard(command);
            currentPlayer.getProgramField(i).setCard(commandCard);
        }
        stopPollingForMoves();
        board.setPhase(Phase.ACTIVATION);
        continuePrograms();
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
        turnAround(player);
        boardController.handleMovement(player.getSpace(), neighbour, player.getHeading());
        turnAround(player);
    }

    public void repeatPrevRegister(@NotNull Player player) {
        Command previousCommand = player.getPreviousCommand();
        executeCommand(player, previousCommand);
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

    public CommandCardField createCommandCardFieldFromString(String s, Player player){
        // Create a CommandCardField from the String
        Command command = Command.fromString(s);
        CommandCard commandCard = new CommandCard(command);
        CommandCardField commandCardField = new CommandCardField(player);
        commandCardField.setCard(commandCard);

        return commandCardField;
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

    /**
     * Returns the AppController used by this class
     *
     * @return the AppController
     */
    public AppController getAppController() {
        return appController;
    }

    /**
     * Retrieves the count of players who are currently marked as ready.
     *
     * @return the count of ready players
     */
    public int getReadyPlayersCount() {
        int readyPlayersCount = 0;

        try {
            readyPlayersCount = appController.getApiServices().getPlayerReadyCount(this.board.getGameId(), this.board.getMoveCount());
        } catch (Exception e) {
            // Handle exceptions or errors in fetching the data
            System.err.println("Error fetching ready players count: " + e.getMessage());
        }

        return readyPlayersCount;
    }

}
