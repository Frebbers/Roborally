package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.AppController;
import dk.dtu.compute.se.pisd.roborally.view.CheckpointView;
import org.jetbrains.annotations.NotNull;

/**
 * Checkpoint class for marking positions on the board that players' robots need to reach.
 * Implements observer pattern to notify changes in its state.
 *
 *  @author Nicolai D. Madsen, s213364@dtu.dk
 */
public class Checkpoint extends Subject {

    public int x;
    public int y;
    private final int checkpointId;
    private CheckpointView checkpointView;

    /**
     * Constructs a new checkpoint on the specified space with an identifier.
     *
     * @param checkpointId the unique identifier for the checkpoint
     * @param x the initial x-coordinate for the checkpoint
     */
    public Checkpoint(int x, int y, int checkpointId) {
        this.x = x;
        this.y = y;
        this.checkpointId = checkpointId;
    }

    /**
     * Returns the checkpoint identifier.
     *
     * @return checkpoint identifier
     */
    public int getCheckpointId() {
        return checkpointId;
    }

    public void setCheckpointView(CheckpointView checkpointView) {
        this.checkpointView = checkpointView;
    }

    public CheckpointView getCheckpointView() {
        return checkpointView;
    }
}
