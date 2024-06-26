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

import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import dk.dtu.compute.se.pisd.roborally.util.Utilities;
import javafx.application.Platform;
import javafx.scene.control.*;
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

    final private RoboRally roboRally;
    private ApiServices apiServices;

    private GameController gameController;
    public static PlayerDTO localPlayer;
    private Alert notConnectedAlert;
    private LobbyController lobbyController;

    /**
     * Create an AppController object tied to the given RoboRally object.
     *
     * @param roboRally
     */
    public AppController(@NotNull RoboRally roboRally) {
        this.roboRally = roboRally;
        this.apiServices = new ApiServices(this);

        if(getProperty("local.player.name").isEmpty()){
            TextInputDialog dialog = new TextInputDialog();
            dialog.setTitle("Create Robot");
            dialog.setHeaderText("Enter a name for your robot");
            dialog.setContentText("Name:");
            dialog.getDialogPane().getButtonTypes().setAll(ButtonType.OK);

            // Modify the OK button behavior to validate input
            final ButtonType okButton = ButtonType.OK;
            DialogPane dialogPane = dialog.getDialogPane();
            dialogPane.lookupButton(okButton).addEventFilter(javafx.event.ActionEvent.ACTION, event -> {
                String input = dialog.getEditor().getText();
                if (input.trim().isEmpty()) {
                    event.consume();
                }
            });

            // Show the dialog and wait for the result
            dialog.showAndWait().ifPresent(result -> {
                setProperty("local.player.name", result);
            });
        }

        if (apiServices.isReachable()) {
            //LocalPlayer will be null if the player does not exist on the server
            localPlayer = apiServices.playerExists(getProperty("local.player.name"), getProperty("local.player.id"));
            if (localPlayer != null) {
                loadPlayerProperties();
            }
        }
    }

    public LobbyController getLobbyController() {return lobbyController;}

    public void setLobbyController(LobbyController lobbyController) {this.lobbyController = lobbyController;}

    /**
     * Ask the user for a number of players, thereafter which map they want and initializes the board with the given amount of players.
     * The programming phase is then initialized.
     */
    public void createLobby(String name, int boardId, int players) {
        if (apiServices.isReachable()) {
            onLobbyJoin();
            // Create the lobby
            Game game = apiServices.createGame(name, (long) boardId, players);

            // Join the lobby that was just created
            localPlayer.setState(PlayerState.NOT_READY);
            apiServices.joinGame(game.id, localPlayer.getId());

            // Display the Lobby Window
            roboRally.createLobbyView(this, game.id);
        } else {
            if (notConnectedAlert != null) {notConnectedAlert.showAndWait();}
        }
    }

    /**
     * Joins a lobby for the specified game.
     *
     * @param gameId the ID of the game to join
     */
    public void joinLobby(Long gameId) {
        if (apiServices.isReachable()) {
            onLobbyJoin();
            // Join the game
            localPlayer.setState(PlayerState.NOT_READY);
            apiServices.joinGame(gameId, localPlayer.getId());

            // Display the Lobby Window
            roboRally.createLobbyView(this, gameId);
        }
    }

    /**
     * Loads the game scene for the specified game and board number.
     *
     * @param gameId the ID of the game to load
     * @param boardNumber the number of the board to load
     */
    public void loadGameScene(Long gameId, Long boardNumber) {
        if (gameController != null && !leave(true)) {
            return;
        }

        // Load the board data
        BoardData data = loadJsonBoardFromNumber(Math.toIntExact(boardNumber));

        // Create a board from the data
        Board board = new Board(gameId, data);

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

    /**
     * Loads the board data from a JSON file based on the board number.
     *
     * @param boardNumber the number of the board to load
     * @return the board data loaded from the JSON file
     */
    private BoardData loadJsonBoardFromNumber(int boardNumber) {
        JsonReader jsonReader = new JsonReader(boardNumber);
        return jsonReader.readBoardJson();
    }

    /**
     * Stop playing the current game, giving the user the option to save
     * the game or to cancel stopping the game. The method returns true
     * if the game was successfully stopped (with or without saving the
     * game); returns false, if the current game was not stopped. In case
     * there is no current game, false is returned.
     * @param lobbyLeaveRequested true if the user requested to leave the lobby,
     *                             false if the user requested to exit the application
     * @return true if the current game was stopped, false otherwise
     */
    public boolean leave(boolean lobbyLeaveRequested) {
        if (apiServices != null) {
            // Remove the local player from the game
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
        //if the user is in a game, ask if they want to leave the game before exiting
            if ((gameController != null) && !roboRally.getActiveView().showExitConfirmationAlert()) {return;}
                // If the user did not cancel, or is not in a game, exit any lobbies and close the application
                if (gameController == null || leave(false)) {
                    Platform.exit();
                    System.exit(0);

                }
            }




    /**
     * Check whether this object's lobbyController is not null.
     *
     * @return true if the player exists and is in a lobby, false otherwise
     */
    public boolean isInLobby() {
        if (localPlayer == null) {
            return false;
        }
        return (!(localPlayer.getState().equals(PlayerState.NOT_IN_LOBBY)));
    }

    /**
     * Set the name of the player and create a new player object if none exists.
     * This method is currently only used as a fallback if the player does not exist on the server or locally.
     * @author s224804
     */
    public void createCharacter() {
        if (apiServices.isReachable()) {
            String name = getProperty("local.player.name");

            //Attempt to create player
            localPlayer = apiServices.createPlayer(name);
            updatePlayerID();
            if (apiServices.createPlayer(name) == null) {
                System.out.println("Error creating player");
                roboRally.getActiveView().showAlert(Alert.AlertType.ERROR,
                        "Error creating player. Check your connection to the server.", "Error creating player.");
            } else {
                System.out.println("Player created");
                setProperty("local.player.name", name);
            }
        } else {
            roboRally.getActiveView().showAlert(Alert.AlertType.WARNING, "Failed to create character!",
                    "Error creating character. Check your connection to the server.");

        }
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

    /**
     * Writes the ID of the playerDTO object into the properties file
     *
     * @author s224804
     */
    private void updatePlayerID() {
        setProperty("local.player.id", localPlayer.getId().toString());
    }

    /**
     * Checks if the player exists on the server and creates a new player if they do not.
     * Must be called before entering a lobby to verify the player exists on the server before joining
     * @author s224804
     */
    public void onLobbyJoin() {
        localPlayer = apiServices.playerExists(getProperty("local.player.name"), getProperty("local.player.id"));
        if (localPlayer != null && apiServices.playerExists
                (localPlayer.getName(), localPlayer.getId().toString()) == localPlayer) {
            //LocalPlayer is not null and exists on the server
            return;
        } else if (!(getProperty("local.player.name").isEmpty())) {
            //Player does not exist on the server but the name is stored in the config file
            // localPlayer = apiServices.playerExists(getProperty("local.player.name"), getProperty("local.player.id"));
            localPlayer = apiServices.createPlayer(getProperty("local.player.name"));
            updatePlayerID();

        }
        //This should not happen. The player name should be set before this point
        assert localPlayer != null;
    }

    /**
     * Toggles the ready state of the local player by updating the player's state
     * using the API services.
     */
    public void toggleReady() {
        apiServices.updatePlayerState(localPlayer.getId());
    }

    /**
     * Loads the player properties from the config file into the playerDTO object
     *
     * @author s224804
     */
    public PlayerDTO loadPlayerProperties() {
        if (localPlayer == null) return null;

        if (localPlayer.getId() == null && localPlayer.getId() != 0) {
            updatePlayerID();
        }
        localPlayer.setName(getProperty("local.player.name"));

        try {
            localPlayer.setId(Long.parseLong(getProperty("local.player.id")));}
        catch (NumberFormatException e) {
            //we don't need to do anything here
        }

        try {
            localPlayer.setRobotType(Utilities.toEnum(RobotType.class, Integer.parseInt(getProperty("local.player.robotType"))));
        } catch (NumberFormatException e) {
            localPlayer.setRobotType(RobotType.Circuito);
        }


        return localPlayer;
    }
}
