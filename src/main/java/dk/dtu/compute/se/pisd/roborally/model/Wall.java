package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;

/**
 * Wall class for defining boundaries on the board.
 * Implements observer pattern to notify changes in its state.
 */
public class Wall extends Subject {
    private Heading heading;
    private Heading offset;
    public int x;
    public int y;

    /**
     * Constructs a new wall at a specified location with a heading.
     *
     * @param x the x-coordinate of the wall on the board
     * @param y the y-coordinate of the wall on the board
     * @param heading the direction the wall faces
     */
    public Wall(int x, int y, String heading, String offset) {
        this.x = x;
        this.y = y;
        this.heading = Heading.fromString(heading);
        this.offset =  Heading.fromString(offset);
    }

    // Getters and setters
    public Heading getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = Heading.fromString(heading);
    }

    public Heading getOffset() {
        return offset;
    }

    public void setOffset(String offset) {
        this.offset = Heading.fromString(offset);
    }
}
