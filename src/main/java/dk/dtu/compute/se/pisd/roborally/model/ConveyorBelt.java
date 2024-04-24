package dk.dtu.compute.se.pisd.roborally.model;

import dk.dtu.compute.se.pisd.designpatterns.observer.Subject;
import dk.dtu.compute.se.pisd.roborally.controller.ConveyorBeltController;
import org.jetbrains.annotations.NotNull;

public class ConveyorBelt extends Subject {

    private final Space space;
    private ConveyorBeltController beltCtrl;

    public ConveyorBelt(@NotNull Space space) {
        this.space = space;

        space.setConveyorBelt(this);
    }

    public Space getSpace() {
        return space;
    }
}
