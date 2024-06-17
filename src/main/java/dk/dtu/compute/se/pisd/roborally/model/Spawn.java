package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

/**
 * Wall class for defining boundaries on the board.
 * Implements observer pattern to notify changes in its state.
 *
 *  @author Nicolai D. Madsen, s213364@dtu.dk
 */

public class Spawn extends Subject {
    public int x;
    public int y;

    /**
     * Constructs a new wall at a specified location with a heading.
     *
     * @param x the x-coordinate of the wall on the board
     * @param y the y-coordinate of the wall on the board
     */
    public Spawn(int x, int y) {
        this.x = x;
        this.y = y;
    }
}
