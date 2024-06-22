package dk.dtu.compute.se.pisd.roborally.model.DTO;

import dk.dtu.compute.se.pisd.roborally.model.PlayerState;
import dk.dtu.compute.se.pisd.roborally.model.RobotType;

public class PlayerDTO {

    private Long id;
    private String name;
    private RobotType robotType;
    private PlayerState state;
    private Long gameId;

    public PlayerDTO(Long id, String name, RobotType robotType, PlayerState state) {
        this.id = id;
        this.name = name;
        this.robotType = robotType;
        this.state = state;
    }
    public PlayerDTO(){}
    public PlayerDTO(long id){this.id = id;}

    public Long getId() {
        return id;
    }

    public void setId(Long id){
        this.id = id;
    }

    public String getName(){
        return name;
    }

    public void setName(String name){
        this.name = name;
    }

    public RobotType getRobotType(){
        return robotType;
    }

    public void setRobotType(RobotType robotType){
        this.robotType = robotType;
    }

    public PlayerState getState(){
        return this.state;
    }

    public void setState(PlayerState state){
        this.state = state;
    }

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId){
        this.gameId = gameId;
    }
}