package dk.dtu.compute.se.pisd.roborally.model;

public enum GameState {
    LOBBY,
    IN_PROGRESS,
    FINISHED;

    @Override
    public String toString() {
        return switch (this) {
            case LOBBY -> "Lobby";
            case IN_PROGRESS -> "In Progress";
            case FINISHED -> "Finished";
        };
    }
}
