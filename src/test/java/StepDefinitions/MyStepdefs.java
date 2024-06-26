package StepDefinitions;

import dk.dtu.compute.se.pisd.roborally.RoboRally;
import dk.dtu.compute.se.pisd.roborally.config.AppConfig;
import dk.dtu.compute.se.pisd.roborally.controller.*;
import dk.dtu.compute.se.pisd.roborally.model.*;
import dk.dtu.compute.se.pisd.roborally.model.DTO.PlayerDTO;
import dk.dtu.compute.se.pisd.roborally.service.ApiServices;
import dk.dtu.compute.se.pisd.roborally.view.LobbyBrowserViewTest;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;
import javafx.stage.Stage;
import org.junit.jupiter.api.Assertions;
import javafx.stage.Stage;
import org.testfx.framework.junit5.ApplicationTest;
import org.junit.jupiter.api.Test;

import java.util.Objects;

import static dk.dtu.compute.se.pisd.roborally.model.Command.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;


public class MyStepdefs {
    private GameController gameController;
    private RoboRally testRoborally;
    private PlayerDTO otherPlayerDTO;
    private PlayerDTO localPlayer;
    private LobbyBrowserViewTest lobbyBrowserViewTest;
    @Given("the robot is facing {string}")

    /**
     * Method to set the direction the robot is facing
     * @param direction the direction the robot is facing
     * @Author Frederik Bode Hendrichsen s224804
     */
    public void theRobotIsFacing(String direction) {
        switch (direction) {
            case "north":
                gameController.board.getCurrentPlayer().setHeading(Heading.NORTH);
                break;
            case "south":
                gameController.board.getCurrentPlayer().setHeading(Heading.SOUTH);
                break;
            case "east":
                gameController.board.getCurrentPlayer().setHeading(Heading.EAST);
                break;
            case "west":
                gameController.board.getCurrentPlayer().setHeading(Heading.WEST);
                break;
        }
    }

    /**
     * Method to initialize the testing environment before each test
     * @author Frederik Bode Hendrichsen s224804
     */
    @Given("the game is initialized")
    public void theGameIsInitialized() {
        testRoborally = new RoboRally(true);
        GameControllerTest gameControllerTest = new GameControllerTest();
        gameControllerTest.setUp();
        AppConfig.setProperty("player.name", "TestPlayer");
        gameController.getAppController().onLobbyJoin();
        this.gameController = gameControllerTest.getGameController();
    }
    /**
     * Method to set the position of the robot
     * @param x the x coordinate of the robot
     * @param y the y coordinate of the robot
     * @author Frederik Bode Hendrichsen s224804
     */
    @And("the robot is at position \\({int}, {int})")
    public void theRobotIsAtPosition(int x, int y) {
        gameController.board.getSpace(x, y).setPlayer(gameController.board.getCurrentPlayer());
    }
    /**
     * Method to set the turn counter of the game
     * @param arg0 the turn count to set the game to
     * @Author Frederik Bode Hendrichsen s224804
     */
    @And("the turn counter is at \\({int})")
    public void theTurnCounterIsAt(int arg0) {
        gameController.board.setMoveCount(arg0);
    }

    /**
     * Method to manually program a command card for the robot
     * @param command the command to program the robot with
     * @author Frederik Bode Hendrichsen s224804
     */
    @When("the robot has programmed a {string} card")
    public void theRobotHasProgrammedACard(String command) {
        CommandCard commandCard = new CommandCard(fromString(command));
        gameController.board.getCurrentPlayer().getProgramField(0).setCard(commandCard);
    }

    /**
     * Method to assert if the robot is at a specific position
     * @param x the x coordinate the robot should be at
     * @param y the y coordinate the robot should be at
     */
    @Then("the robot should be at position \\({int}, {int})")
    public void theRobotShouldBeAtPosition(int x, int y) {
        assert gameController.board.getCurrentPlayer().getSpace().x == x;
        assert gameController.board.getCurrentPlayer().getSpace().y == y;
    }

    /**
     * Method to assert if the turn counter is at a specific count
     * @param count the count the turn counter should be at
     * @author Frederik Bode Hendrichsen s224804
     */
    @And("the turn counter should be at \\({int})")
    public void theTurnCounterShouldBeAt(int count) {
        assert gameController.board.getMoveCount() == count;
    }

    /**
     * Method to assert if the robot is facing a specific direction
     * @param h the direction the robot should be facing as a String
     * @author Frederik Bode Hendrichsen s224804
     */
    @Then("the robot should be facing {string}")
    public void theRobotShouldBeFacing(String h) {
        Heading heading = Heading.fromString(h);
        assert gameController.board.getCurrentPlayer().getHeading().equals(heading);
    }

    /**
     * Method to set current phase to be player_interaction
     * @author Adrian Akinade
     */
    @And("A player interaction phase should be active")
    public void aPlayerInteractionPhaseShouldBeActive() {
        assertEquals((Phase.PLAYER_INTERACTION),gameController.board.getPhase());
    }

    /**
     * Method to assert if the robot is facing a specific direction
     * @param HeadingL the direction the robot should be facing as a String
     * @param HeadingR the direction the robot should be facing as a String
     * @Author Adrian Akinade
     */
    @Then("the robot should be facing {string} or {string}")
    public void theRobotShouldBeFacingOr(String HeadingL, String HeadingR) {
        Heading heading1 = Heading.fromString(HeadingL);
        Heading heading2 = Heading.fromString(HeadingR);
        assert gameController.board.getCurrentPlayer().getHeading().equals(heading1)
                || gameController.board.getCurrentPlayer().getHeading().equals(heading2);


    }

    /**
     * Method to assert if the last command used was a specific command
     * @param command the command the robot should have programmed last
     * @author Adrian Akinade
     */
    @And("Last repository used was {string}")
    public void lastRepositoryUsedWas(String command) {
        Player player = gameController.board.getCurrentPlayer();
        CommandCardField commandCardField = player.getCardField(0);
        CommandCard commandCard = commandCardField.getCard();
        Command prevCommand = player.getPreviousCommand();

        theRobotHasProgrammedACard(command);

        assert (commandCard.command.equals(prevCommand));

    }

    /**
     * Method to execute the next step
     * @Author Adrian Akinade
     */
    @And("The player presses execute current register")
    public void thePlayerPressesExecuteCurrentRegister() {
        gameController.executeStep();
    }

    /**
     * Method to assert if the phase is a specific phase
     * @param phase the phase the game should be in
     * @author Adrian Akinade
     */
    @And("the phase is {string}")
    public void thePhaseIs(String phase) {
        gameController.board.setPhase(Phase.fromString(phase));
    }

    /**
     * Method to place a robot at a specific position with no state or gameID
     * @param x the x coordinate to place the robot
     * @param y the y coordinate to place the robot
     * @author Frederik Bode Hendrichsen s224804
     */
    @And("another robot is at position \\({int}, {int})")
    public void anotherRobotIsAtPosition(int x, int y) {
        Player player = new Player((long)10, "Player2", RobotType.Spark, null, null);
    }
    @And("there should be a robot at position \\({int}, {int})")
    public void thereShouldBeARobotAtPosition(int x, int y) {
        assert gameController.board.getSpace(x, y).getPlayer() != null;
    }

    @And("there is a wall at position \\({int}, {int}), {string}")
    public void thereIsAWallAtPosition(int x, int y, String heading) {
        Wall wall = new Wall(x, y, heading, "north");
    }


    @Given("A game has been initialized online")
    public void aGameHasBeenInitializedOnline() {
        testRoborally = new RoboRally();
        theGameIsInitialized();
        ApiServices apiServices = new ApiServices(gameController.getAppController());

        Game game = apiServices.createGame("OtherTestPlayer", 1L, 2);
        otherPlayerDTO = apiServices.createPlayer("OtherTestPlayer");

        Game joinGame = apiServices.joinGame(game.id,otherPlayerDTO.getId());
        Game getGame = apiServices.getGameById(game.id);

        // Direct comparison of objects does not work as each method creates a new Game
        // object but with the same properties. This does, however, still confirm that
        // the games are the same.
        localPlayer = apiServices.createPlayer("LocalTestPlayer");
        assertEquals(joinGame.id, getGame.id);
    }

    @Then("another game can be initialized online")
    public void anotherGameCanBeInitializedOnline() {
        ApiServices apiServices = new ApiServices(gameController.getAppController());

        Game game = apiServices.createGame("Game2", 1L, 3);
        PlayerDTO player = apiServices.createPlayer("Player2");

        Game joinGame = apiServices.joinGame(game.id,player.getId());
        Game getGame = apiServices.getGameById(game.id);

        // Direct comparison of objects does not work as each method creates a new Game
        // object but with the same properties. This does, however, still confirm that
        // the games are the same.
        assertEquals(joinGame.id, getGame.id);
    }

    @When("All players have finished their programming phase")
    public void allPlayersHaveFinishedTheirProgrammingPhase() {
        assertEquals(gameController.getReadyPlayersCount(),gameController.board.getPlayers().toArray().length);
    }

    @Then("All clients should display the moves in correct order")
    public void allClientsShouldDisplayTheMovesInCorrectOrder() {
        gameController.board.setMoveCount(gameController.board.getMoveCount());
        for (int i = 0; i < gameController.board.getMoveCount(); i++) {
            assert (gameController.getNextCommand().equals(gameController.board.getCurrentPlayer().getPreviousCommand()));
        }

    }

    @Given("a lobby has to be initialized")
    public void aLobbyHasToBeInitialized() {
        AppController testappController = new AppController(testRoborally);
        testappController.createLobby("TestLobby", 1, 2);
    }

    @And("there are \\({int}) players in the game")
    public void thereArePlayersInTheGame(int count) {
        for (int i = 0; i < count; i++) {
            gameController.board.addPlayer(new Player(
                    (long) i,
                    "Player" + i,
                    RobotType.Gizmo,
                    PlayerState.READY,
                    gameController.board.getGameId()
            ));
        }
    }

    @Then("the player cannot see other players' cards")
    public void thePlayerCannotSeeOtherPlayersCards() {
        for (Player player : gameController.board.getPlayers()) {
            for (int i = 0; i < Player.NO_REGISTERS; i++) {
                Assertions.assertNull(player.getProgramField(i).getCard());
            }
        }
    }

    @And("A player needs to join the lobby")
    public void aPlayerNeedsToJoinTheLobby() {
        Player player = gameController.board.getCurrentPlayer();
        //aLobbyHasToBeInitialized();
        gameController.getAppController().joinLobby(1L);
        assertTrue(gameController.getAppController().isInLobby());
    }

    @When("All players are ready")
    public void allPlayersAreReady() {
        assertEquals(gameController.getReadyPlayersCount(),gameController.board.getPlayers().toArray().length);
    }

    @Then("All players should be in the the Phase {string}")
    public void allPlayersAreInTheThePhase(String phase) {
        assertEquals(gameController.board.getPhase(), Phase.fromString(phase));
    }

    @When("A player ends on a checkpoint")
    public void aPlayerEndsOnACheckpoint(int Index) {

    }

    @And("There is a checkpoint in coordinate \\({int}, {int}, {int})")
    public void thereIsACheckpointInCoordinate(int x, int y, int testId) {
        Checkpoint testcheckpoint = new Checkpoint(x, y, testId);
        gameController.board.getSpace(x, y).setCheckpoint(testcheckpoint);
    }



    @Then("Checkpoints passed increments with \\({int})")
    public void checkpointsPassedIncrementsWith(int increment) {
        assertEquals (gameController.board.getCurrentPlayer().getCheckpoints().size(), increment);
    }

    @When("A player ends on a checkpoint with the index \\({int})")
    public void aPlayerEndsOnACheckpointWithTheIndex(int testId) {
        assert (gameController.board.getCurrentPlayer().getSpace().getCheckpoint() == gameController.board.getCurrentPlayer().getCheckpoints().get(testId));
    }


    @Given("the server is offline")
    public void theServerIsOffline() {

    }


    @And("the lobby browser is opened")
    public void theLobbyBrowserIsOpened() {
        lobbyBrowserViewTest = new LobbyBrowserViewTest(gameController.getAppController(), testRoborally);
    }


    @Then("the lobby browser should show a message that the server is offline and the join lobby button should be disabled")
    public void theLobbyBrowserShouldShowAMessageThatTheServerIsOffline() {
            assert !lobbyBrowserViewTest.isConnectedStatus();
            assert lobbyBrowserViewTest.isJoinLobbyButtonDisabled();
    }


    @Given("the server is {string} offline")
    public void theServerIsOffline(String not) {
        boolean offline = !(not.equals("not"));
        if (offline) {
            gameController.getAppController().getApiServices().setApiType(ApiType.SERVER);
            gameController.getAppController().getApiServices().connectToServer("0.0.0.0");
            assert !gameController.getAppController().getApiServices().isReachable();
        } else {
            gameController.getAppController().getApiServices().setApiType(ApiType.LOCAL);
            assert gameController.getAppController().getApiServices().isReachable();
        }
    }

    @When("a lobby has been created on the server")
    public void aLobbyHasBeenCreatedOnTheServer() {
        gameController.getAppController().createLobby("TestLobby", 1, 2);
    }

    @And("another player is in the lobby")
    public void anotherPlayerIsInTheLobby() {
        gameController.getAppController().joinLobby(1L);
    }



    @And("the other player should be in the lobby")
    public void theOtherPlayerShouldBeInTheLobby() {
    }


}
