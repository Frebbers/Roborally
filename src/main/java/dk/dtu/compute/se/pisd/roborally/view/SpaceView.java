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
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import dk.dtu.compute.se.pisd.roborally.model.Player;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import javafx.scene.canvas.Canvas;
import javafx.scene.canvas.GraphicsContext;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.scene.paint.Color;
import javafx.scene.shape.Polygon;
import javafx.scene.shape.StrokeLineCap;
import org.jetbrains.annotations.NotNull;

/**
 * Handles the drawing of a space on a board and its contents.
 *
 * @author Ekkart Kindler, ekki@dtu.dk and Nicolai D. Madsen, s213364@dtu.dk
 *
 */
public class SpaceView extends StackPane implements ViewObserver {

    final public static int SPACE_HEIGHT = 60; // 75;
    final public static int SPACE_WIDTH = 60; // 75;

    public final Space space;
    private ImageView backgroundImage;
    private ImageView playerImage;


    /**
     * Create the view of a certain space. The space is colored
     * such that every other space on a board is white, and
     * every other space is black.
     * 
     * @param space
     */
    public SpaceView(@NotNull Space space) {
        this.space = space;

        backgroundImage = new ImageView(new Image("/images/empty.png"));
        backgroundImage.setFitWidth(SPACE_WIDTH);
        backgroundImage.setFitHeight(SPACE_HEIGHT);

        playerImage = new ImageView();
        playerImage.setFitWidth(SPACE_WIDTH);
        playerImage.setFitHeight(SPACE_HEIGHT);

        // Add the image view as the first child of the stack pane
        if (space.getCheckpoint() == null && space.getBelt() == null) {
            this.getChildren().addAll(backgroundImage);
        }
        this.getChildren().addAll(playerImage);

        // Register as an observer to the checkpoint
        space.attach(this);
        update(space);
    }

    /**
     * Draw a player (if there is one) on this space.
     */
    private void updatePlayer() {
        // Clear the player
        playerImage.setImage(null);

        // Get the new player if there is one
        Player player = space.getPlayer();

        if (player != null) {
            // Set the image of the player
            Image image = new Image("/images/r" + (player.getId() + 1) + ".png");
            playerImage.setImage(image);

            switch(player.getHeading()) {
                case NORTH:
                    playerImage.setRotate(180);
                    break;
                case EAST:
                    playerImage.setRotate(270);
                    break;
                case SOUTH:
                    playerImage.setRotate(0);
                    break;
                case WEST:
                    playerImage.setRotate(90);
                    break;
                default:
                    // code block
            }
        }
    }

    /**
     * Update space view as to include a recent change in state.
     */
    @Override
    public void updateView(Subject subject) {
        if (subject == this.space) {
            updatePlayer();
        }
    }

}
