import dk.dtu.compute.se.pisd.roborally.controller.*;
import dk.dtu.compute.se.pisd.roborally.controller.GameController;

import static Assignment2test.Assignment2tests.configureTestenvironment;


public class MyStepdefsA3PC {
    public MyStepdefsA3PC() {
        Given("^The game is initialized$", () -> {
            public void theGameIsInitialized() {
                GameController gameController = configureTestenvironment();
        })
    }
;}

    private void Given(String s, Object o) {
    }
