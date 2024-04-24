package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBeltController;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.fromString;

public class ConveyorBelt extends Subject {

    public int x;
    public int y;
    public String direction;

    public ConveyorBelt(int x, int y, String direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public Heading getDirection() { return fromString(this.direction); }



}
