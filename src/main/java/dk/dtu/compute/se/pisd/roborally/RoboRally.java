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
package dk.dtu.compute.se.pisd.roborally;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyBrowserController;
import dk.dtu.compute.se.pisd.roborally.controller.LobbyController;
import dk.dtu.compute.se.pisd.roborally.view.*;
import javafx.application.Application;
import javafx.scene.Scene;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;


/**
 * Class for handling the javaFX initialization of the RoboRally game.
 * Controls the game through an AppController object.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRally extends Application {

    private static final int MIN_APP_WIDTH = 600;

    private Stage stage;
    private BorderPane boardRoot;

    /**
     * Call init() of the Application class
     */
    @Override
    public void init() throws Exception {
        super.init();
    }

    /**
     * Initialize javafx stages and scenes
     * 
     * @param primaryStage
     */
    @Override
    public void start(Stage primaryStage) {
        stage = primaryStage;

        AppController appController = new AppController(this);

        // create the primary scene with the a menu bar and a pane for
        // the board view (which initially is empty); it will be filled
        // when the user creates a new game or loads a game
        RoboRallyMenuBar menuBar = new RoboRallyMenuBar(appController);
        boardRoot = new BorderPane();

        VBox vbox = new VBox(boardRoot);
        vbox.setMinWidth(MIN_APP_WIDTH);
        Scene primaryScene = new Scene(vbox);

        stage.setScene(primaryScene);
        stage.setTitle("RoboRally");
        stage.setOnCloseRequest(
                e -> {
                    e.consume();
                    appController.exit();} );
        stage.setResizable(false);
        stage.sizeToScene();

        createStartView(appController);

        stage.show();
    }

    /**
     * Create a StartView with the given appController
     *
     * @param appController
     */
    public void createStartView(AppController appController) {
        // if present, remove old view
        boardRoot.getChildren().clear();

        if (appController != null) {
            // create and add view for start screen
            StartView startView = new StartView(appController);
            startView.initialize();
            boardRoot.setCenter(startView);
        }

        stage.sizeToScene();
    }

    /**
     * Create a RobotSettingsView with the given appController
     *
     * @param appController
     */
    public void createRobotSettingsView(AppController appController) {
        // if present, remove old view
        boardRoot.getChildren().clear();

        if (appController != null) {
            // create and add view for new board
            RobotSettingsView robotSettingsView = new RobotSettingsView(appController);
            robotSettingsView.initialize();
            boardRoot.setCenter(robotSettingsView);
        }

        stage.sizeToScene();
    }

    /**
     * Create a BoardView with the given gameController
     * 
     * @param gameController
     */
    public void createBoardView(GameController gameController) {
        // if present, remove old view
        boardRoot.getChildren().clear();

        if (gameController != null) {
            // create and add view for new board
            BoardView boardView = new BoardView(gameController);
            boardView.initialize();
            boardRoot.setCenter(boardView);
        }

        stage.sizeToScene();
    }

    /**
     * Create a LobbyView
     *
     */
    public void createLobbyView(AppController controller, Long gameId) {
        // if present, remove old view
        boardRoot.getChildren().clear();

        if(controller != null){
            LobbyView lobbyView = new LobbyView(controller, gameId);
            lobbyView.initialize();
            boardRoot.setCenter(lobbyView);
        }

        stage.sizeToScene();
    }

    /**
     * Create a LobbySelectView
     *
     */
    public void createLobbyBrowserView(AppController controller) {
        // if present, remove old view
        boardRoot.getChildren().clear();

        if(controller != null){
            LobbyBrowserView lobbyBrowserView = new LobbyBrowserView(controller);
            lobbyBrowserView.initialize();
            boardRoot.setCenter(lobbyBrowserView);
        }

        stage.sizeToScene();
    }

    /**
     * Call the stop() method of the Application class
     */
    @Override
    public void stop() throws Exception {
        super.stop();

        // XXX just in case we need to do something here eventually;
        //     but right now the only way for the user to exit the app
        //     is delegated to the exit() method in the AppController,
        //     so that the AppController can take care of that.
    }

    public Stage getStage(){
        return stage;
    }

    /**
     * Call the javafx method launch() with the given arguments. This automatically calls RoboRally.start().
     * 
     * @param args arguments passed to launch()
     */
    public static void main(String[] args) {
        launch(args);
    }

}