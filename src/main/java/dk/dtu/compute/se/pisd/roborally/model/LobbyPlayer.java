package dk.dtu.compute.se.pisd.roborally.model;

public class LobbyPlayer {
    public Long id;
    public String name;
    private PlayerState state = PlayerState.NOT_READY;
    private boolean isReady = false;
    public Long gameId;

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
}

