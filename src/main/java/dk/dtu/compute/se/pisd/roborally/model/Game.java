package dk.dtu.compute.se.pisd.roborally.model;

import java.util.List;

public class Game {
    public Long id;
    public String name;
    public Long boardId;
    public int maxPlayers;

    public List<Long> playerIds;
}
