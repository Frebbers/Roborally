package dk.dtu.compute.se.pisd.roborally.model.DTO;

import dk.dtu.compute.se.pisd.roborally.model.PlayerState;

public class PlayerDTO {

    private Long id;
    private String name;
    private PlayerState state;
    private Long gameId;

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