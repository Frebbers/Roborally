package dk.dtu.compute.se.pisd.roborally.model;

public enum RobotType {
    Circuito(1),
    HexaByte(2),
    Quantix(3),
    Boltz(4),
    Spark(5),
    Gizmo(6);

    private final int value;

    RobotType(int value){
        this.value = value;
    }

    public int getValue(){
        return value;
    }
}
