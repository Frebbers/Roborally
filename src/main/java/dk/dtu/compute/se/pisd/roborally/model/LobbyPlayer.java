package dk.dtu.compute.se.pisd.roborally.model;

public class LobbyPlayer {
    public Long id;
    public String name;
    public String state;
    public Long gameId;

    public LobbyPlayer(){
        this.state = "not_ready";
        this.gameId = 0L;
    }
}
