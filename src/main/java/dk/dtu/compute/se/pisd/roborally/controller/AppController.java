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
import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.model.*;

import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import dk.dtu.compute.se.pisd.roborally.util.Utilities;
import dk.dtu.compute.se.pisd.roborally.view.CreateLobbyView;
import dk.dtu.compute.se.pisd.roborally.view.SpawnView;
import dk.dtu.compute.se.pisd.roborally.view.StartView;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Alert.AlertType;
import javafx.stage.Stage;
import org.jetbrains.annotations.NotNull;

import java.util.*;

import static dk.dtu.compute.se.pisd.roborally.config.AppConfig.getProperty;
import static dk.dtu.compute.se.pisd.roborally.config.AppConfig.setProperty;

/**
 * Controls the outermost functions of the game such as creating a new game, saving, loading, and stopping the game.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class AppController implements Observer {

    final private List<Integer> PLAYER_NUMBER_OPTIONS = Arrays.asList(1, 2, 3, 4, 5, 6);
    final private List<String> PLAYER_COLORS = Arrays.asList("red", "green", "blue", "orange", "grey");
    final private List<Integer> BOARD_NUMBER_OPTIONS = Arrays.asList(1);

    final private RoboRally roboRally;
    private ApiServices apiServices;

    private GameController gameController;
    public static PlayerDTO localPlayer;

    /**
     * Create an AppController object tied to the given RoboRally object.
     *
     * @param roboRally
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
        this.apiServices = new ApiServices();
        //Will be null if the player does not exist on the server
        localPlayer = apiServices.playerExists(getProperty("local.player.name"), getProperty("local.player.id"));
    }


    /**
     * Ask the user for a number of players, thereafter which map they want and initializes the board with the given amount of players.
     * The programming phase is then initialized.
     */
    public void createLobby(String name, int boardId, int players) {
        // Tell the server to create the player in the database

        onLobbyJoin();

        // Create the lobby
        Game game = apiServices.createGame(name, (long) boardId, players);

        // Join the lobby that was just created
        localPlayer.setState(PlayerState.NOT_READY);
        apiServices.joinGame(game.id, localPlayer.getId());

        // Display the Lobby Window
        roboRally.createLobbyView(this, game.id);
    }

    public void joinLobby(Long gameId) {
        onLobbyJoin();
        // Join the game
        localPlayer.setState(PlayerState.NOT_READY);
        apiServices.joinGame(gameId, localPlayer.getId());

        // Display the Lobby Window
        roboRally.createLobbyView(this, gameId);
    }

    public void loadGameScene(Long gameId, Long boardNumber) {
        if (gameController != null && !leave(true)) {
            return;
        }

        // Load the board data
        BoardData data = loadJsonBoardFromNumber(Math.toIntExact(boardNumber));

        // Create a board from the data
        Board board = new Board(data);

        // Create a new controller for the game
        gameController = new GameController(this, board);

        // Get all the players from the lobby
        List<PlayerDTO> playersDTOs = apiServices.getPlayersInGame(gameId);

        for (int i = 0; i < playersDTOs.size(); i++) {
            // Get the DTO of the player
            PlayerDTO playerDTO = playersDTOs.get(i);

            // Create the actual player object for the local client
            Player player = new Player(playerDTO.getId(), playerDTO.getName(), playerDTO.getRobotType(), playerDTO.getState(), playerDTO.getGameId());

            // Update the required fields for the player
            player.setBoard(board);
            Spawn spawn = board.getData().spawns.get(i);
            Space space = board.getSpace(spawn.x, spawn.y);
            player.setSpace(space);

            // Add the player to the board
            board.addPlayer(player);
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

    /**
     * NOT IMPLEMENTED
     * Load a previously saved game state.
     */
    public void loadGame() {
        // XXX needs to be implemented eventually
        // for now, we just create a new game
        if (gameController == null) {
            //createLobby();
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
    public boolean leave(boolean lobbyLeaveRequested) {
        if(apiServices != null){
            apiServices.onPlayerLeave(localPlayer.getId());

            if (gameController != null) {
                gameController.shutdownScheduler();
                gameController = null;
            }
            if (lobbyLeaveRequested) roboRally.createStartView(this);
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

            if (result.isEmpty() || result.get() != ButtonType.OK) {
                return; // return without exiting the application
            }
        }

        // If the user did not cancel, the RoboRally application will exit
        // after the option to save the game
        if (gameController == null || leave(false)) {
            Platform.exit();
            System.exit(0);
        }
    }


    /**
     * Check whether this object's lobbyController is not null.
     *
     * @return true if localPlayer is not null, false otherwise.
     */
    public boolean isInLobby() {
        if (localPlayer == null) {
            return false;
        }
        return (!(localPlayer.getState().equals(PlayerState.NOT_IN_LOBBY)));
    }

    /**
     * Check whether this object's gameController is not null.
     *
     * @return true if gameController is not null, false otherwise.
     */
    public boolean isGameRunning() {
        return gameController != null;
    }


    public void createCharacter() {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle("Create Character");
        dialog.setHeaderText("Enter your name");
        dialog.setContentText("Name:");
        //TODO maybe also store the player id in the config file
        Optional<String> result = dialog.showAndWait();

        result.ifPresent(name -> {
            //Attempt to create player
            localPlayer = apiServices.createPlayer(name);
            if (apiServices.createPlayer(name) == null) {
                System.out.println("Error creating player");
                Alert alert = new Alert(AlertType.ERROR,
                        "Error creating player. Check your connection to the server.", ButtonType.OK);
                alert.showAndWait();
            } else {
                System.out.println("Player created");
                setProperty("local.player.name", name);
            }
        });
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

    public ApiServices getApiServices() {
        return apiServices;
    }

    private void onLobbyJoin() {
        //TODO test this thoroughly
        if (localPlayer != null) {
            //LocalPlayer is not null and exists on the server
            return;
        } else if (!(getProperty("local.player.name").isEmpty())) {
            //Player does not exist on the server but the name is stored in the config file
            localPlayer = apiServices.createPlayer(getProperty("local.player.name"));
        } else {
            //No character exists and no name is stored in the config file
            createCharacter();
        }
    }

    public void toggleReady() {apiServices.updatePlayerState(localPlayer.getId());}

    public void setLocalPlayer(PlayerDTO body) {}
}
