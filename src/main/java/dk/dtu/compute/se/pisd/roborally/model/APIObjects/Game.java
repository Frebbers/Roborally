package dk.dtu.compute.se.pisd.roborally.model.APIObjects;

import java.util.List;

public class Game implements ApiObject {
    public Long id;
    public Long boardId;
    public int maxPlayers;
    private String path;
    public List<Long> playerIds;
    public Game (Long id, Long boardId, int maxPlayers, String path){
        this.id = id;
        this.boardId = boardId;
        this.maxPlayers = maxPlayers;
        this.path = path;
    }
    public void addPlayer(Long playerId){
        playerIds.add(playerId);
    }
    @Override public String getPath() {
        return null;
    }

    public long getId() {
        return id;
    }
}
