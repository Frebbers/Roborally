package dk.dtu.compute.se.pisd.roborally.view;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.BoardData;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.model.*;
import javafx.application.Platform;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.control.Label;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.GridPane;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

/**
 * Handles the drawing of the game board.
 *
 * @author Ekkart Kindler, ekki@dtu.dk
 *
 */
public class BoardView extends BaseView implements ViewObserver {

    private GameController gameController;
    private Board board;
    private GridPane mainBoardPane;
    private SpaceView[][] spaces;
    private PlayersView playersView;
    private Label statusLabel;
    private SpaceEventHandler spaceEventHandler;

    /**
     * Create a board view tied to a certain game controller.
     *
     * @param gameController
     */
    public BoardView(@NotNull GameController gameController) {
        super(gameController.getAppController());
        this.gameController = gameController;
    }

    @Override
    public void initialize() {
        board = gameController.board;
        board.setBoardView(this);

        mainBoardPane = new GridPane();
        playersView = new PlayersView(gameController);
        statusLabel = new Label("<no status>");

        this.getChildren().add(mainBoardPane);
        this.getChildren().add(playersView);
        this.getChildren().add(statusLabel);

        spaces = new SpaceView[board.width][board.height];
        spaceEventHandler = new SpaceEventHandler(gameController);

        for(int i = 0; i < board.getPlayers().size(); i++){
            Spawn spawn = board.getData().spawns.get(i);
            Space space = board.getSpace(spawn.x, spawn.y);
            space.setSpawn(spawn);
            SpawnView spawnView = new SpawnView(spawn);
            mainBoardPane.add(spawnView, space.x, space.y);
        }

        for (Checkpoint checkpoint : board.getData().checkpoints){
            Space space = board.getSpace(checkpoint.x, checkpoint.y);
            space.setCheckpoint(checkpoint);
            CheckpointView checkpointView = new CheckpointView(checkpoint);
            mainBoardPane.add(checkpointView, space.x, space.y);
        }

        for (ConveyorBelt belt : board.getData().conveyorBelts){
            Space space = board.getSpace(belt.x, belt.y);
            space.setConveyorBelt(belt);
            ConveyorBeltView beltView = new ConveyorBeltView(belt);
            mainBoardPane.add(beltView, space.x, space.y);
        }

        for (int x = 0; x < board.width; x++) {
            for (int y = 0; y < board.height; y++) {
                Space space = board.getSpace(x, y);
                SpaceView spaceView = new SpaceView(space);
                spaces[x][y] = spaceView;
                mainBoardPane.add(spaceView, x, y);
                spaceView.setOnMouseClicked(spaceEventHandler);
            }
        }

        for (PriorityAntenna priorityAntenna : board.getData().priorityAntennas){
            Space space = board.getSpace(priorityAntenna.getX(), priorityAntenna.getY());
            space.setPriorityAntenna(priorityAntenna);
            PriorityAntennaView priorityAntennaView = new PriorityAntennaView(priorityAntenna);
            mainBoardPane.add(priorityAntennaView, space.x, space.y);
        }

        for (Wall wall : board.getData().walls) {
            Space space = board.getSpace(wall.x, wall.y);
            space.setWall(wall);
            WallView wallView = new WallView(wall);
            mainBoardPane.add(wallView, space.x, space.y);
        }

        board.attach(this);
        update(board);
    }

    /**
     * Removes the CheckpointView from the game board when a player reaches it.
     *
     * @param checkpoint the checkpoint to remove
     */
    public void removeCheckpointView(Checkpoint checkpoint) {
        Platform.runLater(() -> {
            Space space = board.getSpace(checkpoint.x, checkpoint.y);
            if (space.getCheckpoint() != null && space.getCheckpoint().equals(checkpoint)) {
                space.setCheckpoint(null);
                Node toRemove = findNodeByCoordinates(checkpoint.x, checkpoint.y);
                if (toRemove != null) {
                    mainBoardPane.getChildren().remove(toRemove);
                    SpaceView newSpaceView = new SpaceView(space);
                    mainBoardPane.add(newSpaceView, checkpoint.x, checkpoint.y);
                }
            }
        });
    }

    /**
     * Helper method to find a node by their coordinates
     *
     * @param x x-coordination
     * @param y y-coordination
     */
    private Node findNodeByCoordinates(int x, int y) {
        for (Node node : mainBoardPane.getChildren()) {
            if (GridPane.getColumnIndex(node) != null && GridPane.getRowIndex(node) != null &&
                    GridPane.getColumnIndex(node) == x && GridPane.getRowIndex(node) == y) {
                return node;
            }
        }
        return null;
    }

    @Override
    public void updateView(Subject subject) {
        if (subject == board) {
            Phase phase = board.getPhase();
            statusLabel.setText(board.getStatusMessage());
        }
    }

    private class SpaceEventHandler implements EventHandler<MouseEvent> {
        final public GameController gameController;

        public SpaceEventHandler(@NotNull GameController gameController) {
            this.gameController = gameController;
        }

        @Override
        public void handle(MouseEvent event) {
            Object source = event.getSource();
            if (source instanceof SpaceView) {
                SpaceView spaceView = (SpaceView) source;
                Space space = spaceView.space;
                Board board = space.board;

                if (board == gameController.board) {
                    event.consume();
                }
            }
        }
    }
}
