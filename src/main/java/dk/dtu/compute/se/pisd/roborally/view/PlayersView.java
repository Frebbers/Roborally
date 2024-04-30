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

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.Board;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import javafx.scene.control.TabPane;

import java.util.Comparator;

/**
 * Handles the views of several players on a particular board.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class PlayersView extends TabPane implements ViewObserver {

    private Board board;

    private PlayerView[] playerViews;

    /**
     * Create a player view tied to a certain game controller.
     * 
     * @param gameController
     */
    public PlayersView(GameController gameController) {
        board = gameController.board;

        this.setTabClosingPolicy(TabClosingPolicy.UNAVAILABLE);

        playerViews = new PlayerView[board.getPlayersNumber()];
        for (int i = 0; i < board.getPlayersNumber();  i++) {
            playerViews[i] = new PlayerView(gameController, board.getPlayer(i));
            this.getTabs().add(playerViews[i]);
        }
        board.attach(this);
        update(board);
    }

    /**
     * Update the order of tabs in {@link PlayersView#getTabs()} in
     * accordance with the order of player turns in a given register.
     * <p>
     *     This utilizes the fact that the array {@link #playerViews}
     *     has the same indexes for players as the Board's list used in
     *     {@link Board#getPlayer(int)}. If this ever stops being the case,
     *     this method will need to be rewritten.
     * </p>
     * <p>
     *     This may be rewritten to utilize {@link java.util.List#sort(Comparator)}
     * </p>
     *
     * @author s214972@dtu.dk
     */
    public void updatePlayersViewOrder() {
        // clear tabs to re-add in the correct order
        this.getTabs().clear();
        // iterate through players in the turn order
        for (int i = 0; i < board.getPlayersNumber(); i++) {
            // get the player in the ith position in the turn order
            Player currentOrderedPlayer = board.getPlayerByTurnOrder(i);
            // iterate through player views to find the one corresponding to this player
            for (PlayerView playerView : playerViews) {
                if (playerView.getPlayer() == currentOrderedPlayer) { // when found
                    // get the non-ordered index of the player, and add the player view of
                    // the same number.
                    this.getTabs().add(playerViews[board.getPlayerNumber(currentOrderedPlayer)]);
                }
            }
        }
    }

    /**
     * Update board view as to include a recent change in state.
     * 
     * @param subject
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            Player current = board.getCurrentPlayer();
            this.updatePlayersViewOrder();
            this.getSelectionModel().select(board.getPlayerNumberByTurnOrder(current));
        }
    }

}
