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

    public Checkpoint(int x, int y, int checkpointId) {
        this.x = x;
        this.y = y;
        this.checkpointId = checkpointId;
    }
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
