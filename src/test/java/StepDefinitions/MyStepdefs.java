package StepDefinitions;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.GameControllerTest;
import dk.dtu.compute.se.pisd.roborally.model.*;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;

import static dk.dtu.compute.se.pisd.roborally.model.Command.fromString;
import static org.junit.jupiter.api.Assertions.assertEquals;


public class MyStepdefs {
    private GameController gameController;
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
        GameControllerTest gameControllerTest = new GameControllerTest();
        gameControllerTest.setUp();
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
        gameController.board.getCurrentPlayer().setSpace(gameController.board.getSpace(x, y));
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
        CommandCardField lastCommandCardField = player.getPreviousCardField();
        CommandCard prevcommandCard = lastCommandCardField.getCard();

        theRobotHasProgrammedACard(command);

        assert (commandCard.equals(prevcommandCard));

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
     * Method to place a robot at a specific position
     * @param x the x coordinate to place the robot
     * @param y the y coordinate to place the robot
     * @author Frederik Bode Hendrichsen s224804
     */
    @And("another robot is at position \\({int}, {int})")
    public void anotherRobotIsAtPosition(int x, int y) {
        Player player = new Player(gameController.board, "Player2", 2, null);
    }
    @And("there should be a robot at position \\({int}, {int})")
    public void thereShouldBeARobotAtPosition(int x, int y) {
        assert gameController.board.getSpace(x, y).getPlayer() != null;
    }

    @And("there is a wall at position \\({int}, {int}), {string}")
    public void thereIsAWallAtPosition(int x, int y, String heading) {
        Wall wall = new Wall(x, y, heading, "north");
    }
}