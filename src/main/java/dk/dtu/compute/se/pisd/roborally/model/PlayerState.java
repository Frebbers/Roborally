package dk.dtu.compute.se.pisd.roborally.model;

public enum PlayerState {
    NOT_READY,
    READY,
    NOT_IN_LOBBY,
    INTERACTING;

    @Override
    public String toString() {
        return switch (this) {
            case NOT_READY -> "Not Ready";
            case READY -> "Ready";
            case INTERACTING -> "Interacting";
            case NOT_IN_LOBBY -> "Not in Lobby";
        };
    }
}
