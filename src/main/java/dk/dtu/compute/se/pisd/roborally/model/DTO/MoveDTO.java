package dk.dtu.compute.se.pisd.roborally.model.DTO;

import dk.dtu.compute.se.pisd.roborally.model.PlayerState;

import java.util.ArrayList;
import java.util.List;

public class MoveDTO {
    private Long gameId;
    private Long playerId;
    private Integer turnIndex;
    List<String> moves = new ArrayList<>();

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

    public List<String> getMoves(){
        return moves;
    }

    public void setMoves(List<String> moves){
        this.moves = moves;
    }
}
