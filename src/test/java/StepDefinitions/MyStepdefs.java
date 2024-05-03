package StepDefinitions;

import dk.dtu.compute.se.pisd.roborally.controller.GameController;
import dk.dtu.compute.se.pisd.roborally.controller.GameControllerTest;
import dk.dtu.compute.se.pisd.roborally.model.Command;
import dk.dtu.compute.se.pisd.roborally.model.CommandCard;
import dk.dtu.compute.se.pisd.roborally.model.Heading;
import io.cucumber.java.en.And;
import io.cucumber.java.en.Given;
import io.cucumber.java.en.Then;
import io.cucumber.java.en.When;




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
        CommandCard commandCard = new CommandCard(Command.fromString(command));
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
}
