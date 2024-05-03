package dk.dtu.compute.se.pisd.roborally.controller;

import dk.dtu.compute.se.pisd.roborally.model.*;

import java.util.List;

public class BoardData {
    public String name;
    public int width;
    public int height;

    public List<Wall> walls;
    public List<Checkpoint> checkpoints;
    public List<ConveyorBelt> conveyorBelts;
    public List<PriorityAntenna> priorityAntennas;

    public BoardData(String name, int width, int height, List<Wall> walls, List<Checkpoint> checkpoints, List<ConveyorBelt> conveyorBelts, List<PriorityAntenna> priorityAntennas){
        this.name = name;
        this.width = width;
        this.height = height;
        this.walls = walls;
        this.checkpoints = checkpoints;
        this.conveyorBelts = conveyorBelts;
        this.priorityAntennas = priorityAntennas;
    }
}
