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

    @Given("the game is initialized")
    public void theGameIsInitialized() {
        GameControllerTest gameControllerTest = new GameControllerTest();
        gameControllerTest.setUp();
        this.gameController = gameControllerTest.getGameController();
    }
    @And("the robot is at position \\({int}, {int})")
    public void theRobotIsAtPosition(int x, int y) {
        gameController.board.getCurrentPlayer().setSpace(gameController.board.getSpace(x, y));
    }

    @And("the turn counter is at \\({int})")
    public void theTurnCounterIsAt(int arg0) {
        gameController.board.setMoveCount(arg0);
    }

    @When("the robot has programmed a {string} card")
    public void theRobotHasProgrammedACard(String command) {
        CommandCard commandCard = new CommandCard(fromString(command));
        gameController.board.getCurrentPlayer().getCardField(0).setCard(commandCard);

    }


    @Then("the robot should be at position \\({int}, {int})")
    public void theRobotShouldBeAtPosition(int x, int y) {
        assert gameController.board.getCurrentPlayer().getSpace().x == x;
        assert gameController.board.getCurrentPlayer().getSpace().y == y;
    }

    @And("the turn counter should be at \\({int})")
    public void theTurnCounterShouldBeAt(int count) {
        assert gameController.board.getMoveCount() == count;
    }

    @Then("the robot should be facing {string}")
    public void theRobotShouldBeFacing(String h) {
        Heading heading = Heading.fromString(h);
        assert gameController.board.getCurrentPlayer().getHeading().equals(heading);
    }

    @And("A player interaction phase should be active")
    public void aPlayerInteractionPhaseShouldBeActive() {
        assertEquals((Phase.PLAYER_INTERACTION),gameController.board.getPhase());
    }

    @Then("the robot should be facing {string} or {string}")
    public void theRobotShouldBeFacingOr(String HeadingL, String HeadingR) {
        Heading heading1 = Heading.fromString(HeadingL);
        Heading heading2 = Heading.fromString(HeadingR);
        assert gameController.board.getCurrentPlayer().getHeading().equals(heading1)
                || gameController.board.getCurrentPlayer().getHeading().equals(heading2);


    }

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

    @And("The player presses execute current register")
    public void thePlayerPressesExecuteCurrentRegister() {
        gameController.executeStep();
    }

    @And("the phase is {string}")
    public void thePhaseIs(String phase) {
        gameController.board.setPhase(Phase.fromString(phase));
    }
}
