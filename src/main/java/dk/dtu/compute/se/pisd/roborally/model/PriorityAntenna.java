package dk.dtu.compute.se.pisd.roborally.model;

import java.util.List;

/**
 * @author s214972@dtu.dk
 */
public class PriorityAntenna {
    List<Player> players;
    List<Player> playersLeft;
    Space space;

    public void addPlayer(Player player) {
        players.add(player);
        resetPlayersLeft();
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public void resetPlayersLeft() {
        playersLeft = players;
    }

    public boolean noPlayersLeft() {
        return playersLeft.isEmpty();
    }

    public Player popNextPlayer() {
        Player next = determineNextPlayer();
        playersLeft.remove(next);
        return next;
    }

    private Player determineNextPlayer() {
        Player min = playersLeft.get(0);
        for (Player p : playersLeft) {
            if (calcPlayerDistance(p) < calcPlayerDistance(min)) {
                min = p;
            }
        }
        return min;
    }

    private int calcPlayerDistance(Player player) {
        int px = player.getSpace().x;
        int py = player.getSpace().y;
        int ax = space.x;
        int ay = space.y;

        return Math.abs(px - ax) + Math.abs(py - ay);
    }
}
