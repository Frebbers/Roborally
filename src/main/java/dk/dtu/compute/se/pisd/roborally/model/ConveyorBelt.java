package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBeltController;
import org.jetbrains.annotations.NotNull;

import static dk.dtu.compute.se.pisd.roborally.model.Heading.fromString;

public class ConveyorBelt extends Subject {

    public int x;
    public int y;
    public Heading heading;
    public ConveyorBeltController beltCtrl;

    public ConveyorBelt(int x, int y, String heading) {
        this.x = x;
        this.y = y;
        this.heading = fromString(heading);
        this.beltCtrl = new ConveyorBeltController();
    }

    public int getX() { return this.x; }
    public int getY() { return this.y; }
    public ConveyorBeltController getBeltCtrl(){ return this.beltCtrl; }
    public Heading getDirection() { return heading; }



}
