package dk.dtu.compute.se.pisd.roborally.model;

public class LobbyPlayer {
    public Long id;
    public String name;
    public PlayerState state;
    public Long gameId;

    public LobbyPlayer(){
        this.state = PlayerState.NOT_READY;
        this.gameId = 0L;
    }
}

