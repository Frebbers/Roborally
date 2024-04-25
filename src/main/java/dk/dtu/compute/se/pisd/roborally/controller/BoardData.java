package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.Checkpoint;
import dk.dtu.compute.se.pisd.roborally.model.ConveyorBelt;
import dk.dtu.compute.se.pisd.roborally.model.Space;
import dk.dtu.compute.se.pisd.roborally.model.Wall;

import java.util.List;

public class BoardData {
    public String name;
    public int width;
    public int height;

    public List<Wall> walls;
    public List<Checkpoint> checkpoints;
    public List<ConveyorBelt> conveyorBelts;

    public BoardData(String name, int width, int height, List<Wall> walls, List<Checkpoint> checkpoints, List<ConveyorBelt> conveyorBelts){
        this.name = name;
        this.width = width;
        this.height = height;
        this.walls = walls;
        this.checkpoints = checkpoints;
        this.conveyorBelts = conveyorBelts;
    }
}
