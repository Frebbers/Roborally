package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBeltController;
import org.jetbrains.annotations.NotNull;

public class ConveyorBelt extends Subject {

    public int x;
    public int y;
    public String direction;

    public ConveyorBelt(int x, int y, String direction) {
        this.x = x;
        this.y = y;
        this.direction = direction;
    }



}
