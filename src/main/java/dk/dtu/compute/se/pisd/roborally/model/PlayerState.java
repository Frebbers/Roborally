package dk.dtu.compute.se.pisd.roborally.model;

public enum PlayerState {
    NOT_READY,
    READY,
    NOT_IN_LOBBY,
    INTERACTING;

    @Override
    public String toString() {
        switch(this) {
            case NOT_READY:
                return "Not Ready";
            case READY:
                return "Ready";
            case INTERACTING:
                return "Interacting";
            case NOT_IN_LOBBY:
                return "Not in Lobby";
            default:
                return super.toString();
        }
    }
}
