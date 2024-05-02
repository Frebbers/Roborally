package dk.dtu.compute.se.pisd.roborally.model;

import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

/**
 * For keeping track of the order of players in the current register.
 *
 * <p>
 *     Has a list which contains the order of player turns this register.
 *     This is updated through {@link #updatePlayers()} when the register
 *     is over.
 * </p>
 * <p>
 *     Has a space field for comparing its own position with the players'
 *     for determining player order.
 * </p>
 * <p>
 *     Also has {@link #getPlayer(int)} and {@link #getPlayerNumber(Player)}
 *     for other classes to get information about the player order.
 * </p>
 *
 * @author s214872@dtu.dk
 */
public class PriorityAntenna {
    private List<Player> playerOrder = new ArrayList<Player>();

    private Board board;

    private final int x;
    private final int y;

    /**
     * Constructor initializing a priority antenna on a given board.
     *
     * @param x x-coordinate for the antenna
     * @param y y-coordinate for the antenna
     *
     * @author s214872@dtu.dk
     */
    public PriorityAntenna(int x, int y) {
        this.x = x;
        this.y = y;
    }

    /**
     * Set the board the antenna is on.
     *
     * @param board Board on which the antenna is
     *
     * @author s214872@dtu.dk
     */
    public void setBoard(Board board) {
        this.board = board;
    }

    /**
     * Return the player in the given position in the turn order.
     *
     * @param i the index in the turn order of the player to be returned
     *
     * @return Player in the ith position in the turn order
     *
     * @author s214872@dtu.dk
     */
    public Player getPlayer(int i) {
        return playerOrder.get(i);
    }

    /**
     * Return the index of the given player in the turn order.
     *
     * @param player Player to return the index of in the turn order
     *
     * @return the index of the given player in the turn order
     *
     * @author s214872@dtu.dk
     */
    public int getPlayerNumber(Player player) {
        return playerOrder.indexOf(player);
    }

    /**
     * Updates the contents of the priority antennas player list
     * to match that of the board.
     *
     * @author s214872@dtu.dk
     */
    public void updatePlayers() {
        playerOrder = board.getPlayers();
    }

    /**
     * Update {@link #playerOrder} such that the players are ordered based on who is
     * closest to this priority antenna.
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

        Player closest = null;
        int closestDist = Integer.MAX_VALUE;

        /*
        This loop runs through the list of players and adds the
        player that 1. is the closest to the antenna 2. without
        having already been added to the sorted players list.
        This continues until the list of sorted players is the
        same size as the list of players.
         */
        while (sortedPlayers.size() < playerOrder.size()) {
            // get starting player, as to make sure the closest player does not get added again
            for (Player player : playerOrder) {
                if (!sortedPlayers.contains(player)) {
                    closest = player;
                    closestDist = distanceToPlayer(closest);
                    break;
                }
            }

            // Now run through players finding the closest.
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

    /**
     * Calculate the "taxi cab distance" between the given player and the
     * priority antenna. As in, total number of tiles horizontally and
     * vertically to go from one to the other.
     *
     * @param player Player for which to calculate the distance
     *               between it and the antenna
     *
     * @return Distance as an integer between the player and antenna
     *
     * @author s214872@dtu.dk
     */
    private int distanceToPlayer(@NotNull Player player) {
        int px = player.getSpace().x;
        int py = player.getSpace().y;

        return Math.abs(px - x) + Math.abs(py - y);
    }
}
