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
import dk.dtu.compute.se.pisd.designpatterns.observer.Observer;
import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.model.*;

import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.*;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

/**
 * Controls the outermost functions of the game such as creating a new game, saving, loading, and stopping the game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey");
    final private List<Integer> BOARD_NUMBER_OPTIONS = Arrays.asList(1);

    final private RoboRally roboRally;

    private GameController gameController;
    private LobbyController lobbyController;

    /**
     * Create an AppController object tied to the given RoboRally object.
     * 
     * @param roboRally
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
    }

    /**
     * Ask the user for a number of players, thereafter which map they want and initializes the board with the given amount of players.
     * The programming phase is then initialized.
     */
    public void createLobby() {
        ChoiceDialog<Integer> playerDialog = new ChoiceDialog<>(PLAYER_NUMBER_OPTIONS.get(0), PLAYER_NUMBER_OPTIONS);
        playerDialog.setTitle("Select number of players and map");
        playerDialog.setHeaderText("Select number of players");

        ChoiceDialog<Integer> boardDialog = new ChoiceDialog<>(BOARD_NUMBER_OPTIONS.get(0), BOARD_NUMBER_OPTIONS);
        boardDialog.setTitle("Select map");
        boardDialog.setTitle("Choose board to play");

        Stage boardStage = (Stage) boardDialog.getDialogPane().getScene().getWindow();
        boardStage.setAlwaysOnTop(true);

        Optional<Integer> playerResult = playerDialog.showAndWait();

        if (playerResult.isPresent()) {
            Optional<Integer> boardResult = boardDialog.showAndWait();

            if (boardResult.isPresent()) {
                int playerCount = playerResult.get();
                int boardNumber = boardResult.get();

                // Create the lobby controller
                lobbyController = new LobbyController(this);

                // Create the player
                LobbyPlayer lobbyPlayer = ApiServices.createPlayer("Host");

                // Create the lobby
                Game game = ApiServices.createGame((long) boardNumber, playerCount);

                // Join the lobby that was just created
                ApiServices.joinGame(game.id, lobbyPlayer.id);

                // Display the Lobby Window
                roboRally.createLobbyView(lobbyController, game.id);
            }
        }
    }

    public void joinLobby() {
        List<Integer> listOfGames = ApiServices.getAllGameIds();
        if (listOfGames == null || listOfGames.isEmpty()) {
            System.out.println("No games available to join.");
            return;
        }
            ChoiceDialog<Integer> playerDialog = new ChoiceDialog<>(listOfGames.get(0), listOfGames);
            playerDialog.setTitle("Game Lobbies");
            playerDialog.setHeaderText("Select lobby you would like to join");
            playerDialog.setContentText("Choose your game:");

            // Show dialog and capture result
            playerDialog.showAndWait().ifPresent(gameId -> {
                // Create the lobby controller
                lobbyController = new LobbyController(this);

                // Generate a long based on the id selected
                Long id = Long.valueOf(gameId);

                // Create the player
                LobbyPlayer lobbyPlayer = ApiServices.createPlayer("Client");

                // Join the game
                ApiServices.joinGame(id, lobbyPlayer.id);

                // Display the Lobby Window
                roboRally.createLobbyView(lobbyController, id);
            });
    }

    public void loadGameScene(Long boardNumber, int playerCount){
        if (gameController != null && !leave()) {
            return;
        }

        // Load the board data
        BoardData data = loadJsonBoardFromNumber(Math.toIntExact(boardNumber));

        // Create a board from the data
        Board board = new Board(data);

        gameController = new GameController(board);

        for (int i = 0; i < playerCount; i++) {
            Player player = new Player(board, "Player " + (i + 1),i,  PLAYER_COLORS.get(i));
            board.addPlayer(player);
            player.setSpace(board.getSpace(i % board.width, i));
        }

        // XXX: V2
        // board.setCurrentPlayer(board.getPlayer(0));
        gameController.startProgrammingPhase();

        roboRally.createBoardView(gameController);
    }

    private BoardData loadJsonBoardFromNumber(int boardNumber) {
        JsonReader jsonReader = new JsonReader(boardNumber);
        return jsonReader.readBoardJson();
    }

    /**
     * NOT IMPLEMENTED
     * Save the current game state to be loaded later.
     */
    public void saveGame() {
        // XXX needs to be implemented eventually
    }

    /** NOT IMPLEMENTED
     * Load a previously saved game state.
     */
    public void loadGame() {
        // XXX needs to be implemented eventually
        // for now, we just create a new game
        if (gameController == null) {
            createLobby();
        }
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     *
     * @return true if the current game was stopped, false otherwise
     */
    public boolean leave() {
        if (gameController != null) {

            // here we save the game (without asking the user).
            saveGame();

            gameController = null;
            roboRally.createBoardView(null);
            return true;
        }
        return false;
    }

    /**
     * Ask the user for confirmation to exit the game. Upon no response
     * or a negative response, return with no action. Upon a positive
     * response, stopGame() is called. If the game is successfully stopped,
     * close the game using Platform.exit().
     */
    public void exit() {
        if (gameController != null) {
            Alert alert = new Alert(AlertType.CONFIRMATION);
            alert.setTitle("Exit RoboRally?");
            alert.setContentText("Are you sure you want to exit RoboRally?");
            Optional<ButtonType> result = alert.showAndWait();

            if (!result.isPresent() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || leave()) {
            Platform.exit();
        }
    }


    /**
     * Check whether this object's lobbyController is not null.
     *
     * @return true if lobbyController is not null, false otherwise.
     */
    public boolean isInLobby(){
        return lobbyController != null;
    }

    /**
     * Check whether this object's gameController is not null.
     * 
     * @return true if gameController is not null, false otherwise.
     */
    public boolean isGameRunning() {
        return gameController != null;
    }


    /**
     * NOT IMPLEMENTED
     * 
     * @param subject
     */
    @Override
    public void update(Subject subject) {
        // XXX do nothing for now
    }

    public RoboRally getRoboRally() {
        return roboRally;
    }
}
