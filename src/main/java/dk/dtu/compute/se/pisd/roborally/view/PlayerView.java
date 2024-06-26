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
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.application.Platform;
import javafx.scene.control.*;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

import static dk.dtu.compute.se.pisd.roborally.controller.AppController.localPlayer;

/**
 * Handles the drawing of a player mat.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class PlayerView extends Tab implements ViewObserver {

    private Player player;
    private final boolean isLocalPlayer;

    private VBox top;

    private Label programLabel;
    private GridPane programPane;
    private Label cardsLabel;
    private GridPane cardsPane;

    private CardFieldView[] programCardViews;
    private CardFieldView[] cardViews;

    private VBox buttonPanel;

    private Button finishButton;
    private Button leaveButton;
    private Label readyPlayersLabel;


    private VBox playerInteractionPanel;

    private GameController gameController;
    private ScheduledExecutorService scheduler;

    /**
     * Create a player view belonging to a certain player.
     *
     * @param gameController
     * @param player         the player whose player view this is
     */
    public PlayerView(@NotNull GameController gameController, @NotNull Player player) {
        super(player.getName());
        //this.setStyle("-fx-text-base-color: " + player.getColor() + ";");

        this.gameController = gameController;
        this.player = player;

        isLocalPlayer = Objects.equals(localPlayer.getId(), player.getId());

        // Set up GUI components
        initializeGUIComponents();

        if (player.board != null) {
            player.board.attach(this);
            update(player.board);
        }
    }

    private void initializeGUIComponents() {
        top = new VBox();

        programLabel = new Label("Program");
        programPane = setupProgramPane();
        top.getChildren().addAll(programLabel, programPane);

        if (isLocalPlayer) {
            finishButton = new Button("Finish Programming");
            finishButton.setOnAction(e -> {
                gameController.finishProgrammingPhase();
                readyPlayersLabel.setVisible(true);
                finishButton.setDisable(true);
                setupScheduler();
                disableCards(true);
            });

            leaveButton = new Button("Leave Game");
            leaveButton.setOnAction(e -> onPlayerLeave());

            readyPlayersLabel = new Label();
            readyPlayersLabel.setVisible(false);

            buttonPanel = new VBox(3);
            buttonPanel.getChildren().addAll(finishButton, leaveButton, readyPlayersLabel);

            playerInteractionPanel = new VBox();

            cardsLabel = new Label("Command Cards");
            cardsPane = setupCardsPane();
            top.getChildren().addAll(cardsLabel, cardsPane, buttonPanel);
        }

        this.setContent(top);
    }


    private GridPane setupProgramPane() {
        GridPane programPane = new GridPane();
        programPane.setVgap(20.0);
        programPane.setHgap(2.0);
        programCardViews = new CardFieldView[Player.NO_REGISTERS];
        for (int i = 0; i < Player.NO_REGISTERS; i++) {
            CommandCardField cardField = player.getProgramField(i);
            if (cardField != null) {
                programCardViews[i] = new CardFieldView(gameController, cardField);

                if (programCardViews[i] != null) {
                    programPane.add(programCardViews[i], i, 0);
                } else {
                    System.out.println("Warning: programCardView at index " + i + " is null.");
                }
            }
        }
        return programPane;
    }

    private GridPane setupCardsPane() {
        GridPane cardsPane = new GridPane();
        cardsPane.setVgap(2.0);
        cardsPane.setHgap(2.0);
        cardViews = new CardFieldView[Player.NO_CARDS];
        for (int i = 0; i < Player.NO_CARDS; i++) {
            CommandCardField cardField = player.getCardField(i);
            if (cardField != null) {
                cardViews[i] = new CardFieldView(gameController, cardField);
                cardsPane.add(cardViews[i], i, 0);
            }
        }
        return cardsPane;
    }

    private void onPlayerLeave() {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Leave the game");
        alert.setHeaderText("Are you sure you wish to leave the game?");
        alert.getButtonTypes().setAll(ButtonType.YES, ButtonType.NO);

        Optional<ButtonType> result = alert.showAndWait();

        if (result.isPresent() && result.get() == ButtonType.YES) {
            // Tell the app controller to leave the lobby
            gameController.getAppController().leave(true);
        }
    }

    /**
     * Return player this view belongs to.
     *
     * @return Player which this view belongs to
     * @author s214972@dtu.dk
     */
    public Player getPlayer() {
        return player;
    }

    /**
     * Update player view as to include a recent change in state.
     *
     * @param subject
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == player.board) {
            updateProgramCards();

            if (isLocalPlayer) {
                updateControlPanel();
            }
        }
    }

    private void updateProgramCards() {
        for (int i = 0; i < Player.NO_REGISTERS; i++) {
            CardFieldView cardFieldView = programCardViews[i];
            if (cardFieldView != null) {
                if (player.board.getPhase() == Phase.PROGRAMMING) {
                    cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                } else {
                    if (i < player.board.getStep()) {
                        cardFieldView.setBackground(CardFieldView.BG_DONE);
                    } else if (i == player.board.getStep()) {
                        if (player.board.getCurrentPlayer() == player) {
                            cardFieldView.setBackground(CardFieldView.BG_ACTIVE);
                        } else if (player.board.getPlayerNumberByTurnOrder(player.board.getCurrentPlayer()) > player.board.getPlayerNumberByTurnOrder(player)) {
                            cardFieldView.setBackground(CardFieldView.BG_DONE);
                        } else {
                            cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                        }
                    } else {
                        cardFieldView.setBackground(CardFieldView.BG_DEFAULT);
                    }
                }
            }
        }
    }

    private void updateControlPanel() {
        readyPlayersLabel.setVisible(false);
        stopScheduler();

        if (player.board.getPhase() != Phase.PLAYER_INTERACTION) {
            if (!programPane.getChildren().contains(buttonPanel)) {
                programPane.getChildren().remove(playerInteractionPanel);
                programPane.add(buttonPanel, Player.NO_REGISTERS, 0);
            }
            switch (player.board.getPhase()) {
                case PROGRAMMING:
                    disableCards(false);
                    finishButton.setDisable(false);
                    leaveButton.setDisable(false);
                    break;
                default:
                    finishButton.setDisable(true);
                    leaveButton.setDisable(true);
            }
        } else {
            if (!programPane.getChildren().contains(playerInteractionPanel)) {
                programPane.getChildren().remove(buttonPanel);
                programPane.add(playerInteractionPanel, Player.NO_REGISTERS, 0);
            }
            playerInteractionPanel.getChildren().clear();
            if (player.board.getCurrentPlayer() == player) {
                for (Command currentCommand : gameController.getNextCommand().getOptions()) {
                    Button optionButton = new Button(currentCommand.displayName);
                    optionButton.setOnAction(e -> gameController.finishPlayerInteractionPhase(currentCommand));
                    optionButton.setDisable(false);
                    playerInteractionPanel.getChildren().add(optionButton);
                }
            }
        }
    }

    private void disableCards(boolean disabled){
        for (CardFieldView cardView : programCardViews) {
            if (cardView != null) {
                cardView.setDisable(disabled);
            }
        }
        for (CardFieldView cardView : cardViews) {
            if (cardView != null) {
                cardView.setDisable(disabled);
            }
        }
    }

    private void setupScheduler() {
        if (scheduler != null) {
            scheduler.shutdownNow();  // Ensure previous scheduler is stopped before creating a new one
        }
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::updateReadyPlayersCount, 0, 500, TimeUnit.MILLISECONDS);
    }

    private void stopScheduler() {
        if (scheduler != null) {
            scheduler.shutdown();
            try {
                if (!scheduler.awaitTermination(800, TimeUnit.MILLISECONDS)) {
                    scheduler.shutdownNow();
                }
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
        }
    }

    private void updateReadyPlayersCount() {
        if (Platform.isFxApplicationThread()) {
            int readyPlayersCount = gameController.getReadyPlayersCount();
            int maxPlayers = gameController.board.getPlayersNumber();
            int count = maxPlayers - readyPlayersCount;
            readyPlayersLabel.setText("(Waiting for " + count + (count == 1 ? " player)" : " players)"));
        } else {
            Platform.runLater(this::updateReadyPlayersCount);
        }
    }

}

