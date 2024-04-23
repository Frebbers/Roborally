package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import org.jetbrains.annotations.NotNull;

/**
 * Checkpoint class for marking positions on the board that players' robots need to reach.
 * Implements observer pattern to notify changes in its state.
 *
 */
public class Checkpoint extends Subject {

    private final Space space;
    private final int checkpointId;

    /**
     * Constructs a new checkpoint on the specified space with an identifier.
     *
     * @param checkpointId the unique identifier for the checkpoint
     * @param space the initial space for the checkpoint
     */
    public Checkpoint(@NotNull Space space, int checkpointId) {
        this.space = space;
        this.checkpointId = checkpointId;

        space.setCheckpoint(this);
    }

    /**
     * Returns the checkpoint identifier.
     *
     * @return checkpoint identifier
     */
    public int getCheckpointId() {
        return checkpointId;
    }

    /**
     * Returns the space where this checkpoint is located.
     *
     * @return the space of this checkpoint
     */
    public Space getSpace() {
        return space;
    }
}
