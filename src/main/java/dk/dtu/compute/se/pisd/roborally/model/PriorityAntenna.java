package dk.dtu.compute.se.pisd.roborally.model;

import java.util.List;

/**
 * For handling the order of players' turns.
 * <p>
 *     Resides on a certain space and board and has a list to keep track of the
 *     players who have not yet had their turn.
 * </p>
 *
 * @author s214972@dtu.dk
 */
public class PriorityAntenna {
    Board board;
    List<Player> playersLeft;
    Space space;

    /**
     * Construct a Priority Antenna on a certain board.
     *
     * @param board The board the antenna is on
     *
     * @author s214972@dtu.dk
     */
    public PriorityAntenna(Board board) {
        this.board = board;
    }

    /**
     * Set the space the antenna is on.
     *
     * @param space The space the antenna is on
     *
     * @author s214972@dtu.dk
     */
    public void setSpace(Space space) {
        this.space = space;
    }

    /**
     * Reset the list of remaining players. Typically for when all players
     * have had their turn and the next register begins.
     * <p>
     *     The list is reset to be equal to its board's list of players.
     * </p>
     *
     * @author s214972@dtu.dk
     */
    public void resetPlayersLeft() {
        playersLeft = board.getPlayers();
    }

    /**
     * Tell whether the list of remaining players is empty.
     *
     * @return true if {@link #playersLeft} is empty, false otherwise
     *
     * @author s214972@dtu.dk
     */
    public boolean noPlayersLeft() {
        return playersLeft.isEmpty();
    }

    /**
     * Remove and return the player closest to the priority antenna, and who has not yet
     * had their turn this register.
     *
     * @return Remaining player who is the closest to the priority antenna
     *
     * @author s214972@dtu.dk
     */
    public Player popNextPlayer() {
        Player next = determineNextPlayer();
        playersLeft.remove(next);
        return next;
    }

    /**
     * Return the remaining player closest to the antenna.
     *
     * @return Remaining player closest to the antenna
     *
     * @author s214972@dtu.dk
     */
    private Player determineNextPlayer() {
        Player min = playersLeft.get(0);
        for (Player p : playersLeft) {
            if (calcPlayerDistance(p) < calcPlayerDistance(min)) {
                min = p;
            }
        }
        return min;
    }

    /**
     * Calculate the distance from the given player to the antenna. This is done in
     * "Taxi cab distance" (diagonals are not used in calculation; only right angle lines).
     *
     * @param player The player whose distance to the antenna is to be calculated
     *
     * @return Distance from the player to the antenna in taxi cab distance
     *
     * @author s214972@dtu.dk
     */
    private int calcPlayerDistance(Player player) {
        int px = player.getSpace().x;
        int py = player.getSpace().y;
        int ax = space.x;
        int ay = space.y;

        return Math.abs(px - ax) + Math.abs(py - ay);
    }
}
