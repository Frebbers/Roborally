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
package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import javafx.scene.control.Menu;
import javafx.scene.control.MenuBar;
import javafx.scene.control.MenuItem;

/**
 * Handles the menu bar at the top of the game window for
 * controlling the functions of an app controller.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class RoboRallyMenuBar extends MenuBar {

    private AppController appController;

    private Menu controlMenu;

    private MenuItem createLobby;
    private MenuItem joinLobby;
    private MenuItem createCharacter;
    private MenuItem leaveLobby;
    private MenuItem exitApp;

    /**
     * Create a menu bar tied to a certain app controller.
     * 
     * @param appController
     */
    public RoboRallyMenuBar(AppController appController) {
        this.appController = appController;

        controlMenu = new Menu("Menu");
        this.getMenus().add(controlMenu);

        createLobby = new MenuItem("Create Lobby");
        createLobby.setOnAction( e -> this.appController.createLobby());
        controlMenu.getItems().add(createLobby);

        createCharacter = new MenuItem("Create Character");
        createCharacter.setOnAction( e -> this.appController.createCharacter());
        controlMenu.getItems().add(createCharacter);

        joinLobby = new MenuItem("Join Lobby");
        joinLobby.setOnAction( e -> this.appController.joinLobby());
        controlMenu.getItems().add(joinLobby);

        leaveLobby = new MenuItem("Leave");
        leaveLobby.setOnAction( e -> this.appController.leave());
        controlMenu.getItems().add(leaveLobby);

        exitApp = new MenuItem("Exit");
        exitApp.setOnAction( e -> this.appController.exit());
        controlMenu.getItems().add(exitApp);

        controlMenu.setOnShowing(e -> update());
        controlMenu.setOnShown(e -> this.updateBounds());
        update();
    }

    /**
     * Update menu bar as to include a recent change in state.
     */
    public void update() {
        if (appController.isInLobby()) {
            createLobby.setVisible(false);
            joinLobby.setVisible(false);
            leaveLobby.setVisible(true);
            createCharacter.setVisible(false);
        } else {
            createCharacter.setVisible(true);
            createLobby.setVisible(true);
            joinLobby.setVisible(true);
            leaveLobby.setVisible(false);
        }
    }
}
