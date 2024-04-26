package dk.dtu.compute.se.pisd.roborally.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class PriorityAntenna {
    List<Player> playerOrder = new ArrayList<Player>();

    Board board;

    Space space;

    public PriorityAntenna(Board board) {
        this.board = board;
    }

    public void setSpace(Space space) {
        this.space = space;
    }

    public Player getPlayer(int i) {
        return playerOrder.get(i);
    }

    public void updatePlayers() {
        playerOrder = board.getPlayers();
    }

    /**
     * Update {@link #playerOrder} such that the players are ordered based on who is
     * closest to this priority antenna.
     *
     * <p>
     *     Ties are broken by setting closest the player that was also closest previous
     *     to this sorting.
     * </p>
     * <p>
     *     The sorting algorithm used is O(n^2) so good there is a max of 6 players :)
     * </p>
     *
     * @author s214972@dtu.dk
     * (slightly ashamed and proud simultaneously)
     */
    public void orderPlayers() {
        List<Player> sortedPlayers = new ArrayList<>();

        /*
        This loop runs through the list of players and adds the
        player that 1. is the closest to the antenna 2. without
        having already been added to the sorted players list.
        This continues until the list of sorted players is the
        same size as the list of players.
         */
        while (sortedPlayers.size() < playerOrder.size()) {
            Player closest = playerOrder.get(0);
            int closestDist = distanceToPlayer(closest);

            for (Player player : playerOrder) {
                int thisDist = distanceToPlayer(player);
                if (thisDist < closestDist && !sortedPlayers.contains(player)) {
                    closest = player;
                    closestDist = thisDist;
                }
            }

            sortedPlayers.add(closest);
        }

        playerOrder = sortedPlayers;

    }

    private int distanceToPlayer(@NotNull Player player) {
        int px = player.getSpace().x;
        int py = player.getSpace().y;
        int ax = space.x;
        int ay = space.y;

        return Math.abs(px - ax) + Math.abs(py - ay);
    }
}
