package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

/**
 * Checkpoint class for marking positions on the board that players' robots need to reach.
 * Implements observer pattern to notify changes in its state.
 *
 */
public class Checkpoint extends Subject {

    public int x;
    public int y;
    private final int checkpointId;

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
}
