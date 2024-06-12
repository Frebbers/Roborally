package dk.dtu.compute.se.pisd.roborally.model.APIObjects;

import dk.dtu.compute.se.pisd.roborally.model.PlayerState;

public class LobbyPlayer extends ApiObject{
    public Long id;
    public String name;
    private PlayerState state = PlayerState.NOT_READY;
    private boolean isReady = false;
    public Long gameId;
    private String path;

    public LobbyPlayer(long gameId){this.gameId = gameId;}
    public LobbyPlayer(String name, long gameId){
        this.name = name;
        this.gameId = gameId;
    }
    public void setState(PlayerState state){
        this.state = state;
        isReady = (state == PlayerState.READY);
    }
    public boolean isReady(){return isReady;}
    public PlayerState getState(){return state;}

    @Override public String getPath() {
        return path;
    }

    @Override public long getId() {
        return id;
    }
}

