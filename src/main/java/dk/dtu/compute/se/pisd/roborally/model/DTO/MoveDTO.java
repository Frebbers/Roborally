package dk.dtu.compute.se.pisd.roborally.model.DTO;

import dk.dtu.compute.se.pisd.roborally.model.PlayerState;

import java.util.ArrayList;
import java.util.List;

public class MoveDTO {
    private Long id;
    private Long gameId;
    private Long playerId;
    private Integer turnIndex;
    List<String> moveTypes = new ArrayList<>();

    public Long getGameId() {
        return gameId;
    }

    public void setGameId(Long gameId){
        this.gameId = gameId;
    }

    public Long getPlayerId() {
        return playerId;
    }

    public void setPlayerId(Long id){
        this.playerId = id;
    }

    public Integer getTurnIndex() {
        return turnIndex;
    }

    public void setTurnIndex(Integer turnIndex){
        this.turnIndex = turnIndex;
    }

    public List<String> getMoveTypes(){
        return moveTypes;
    }

    public void setMoveTypes(List<String> moves){
        this.moveTypes = moves;
    }
    public void setId(Long id){ this.id = id; }
    public Long getId() { return id; }
}
