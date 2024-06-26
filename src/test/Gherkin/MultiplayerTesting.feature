@test
  Feature: Testing the implementation of various multiplayer features.
    Background: The game is initialized Online
      Given A game has been initialized online

      Scenario: Moves get collected and distributed among all the players connected.
        Given there should be a robot at position (0, 0)
        And there should be a robot at position (1, 1)
        And the robot has programmed a "Forward" card
        And the turn counter should be at (0)
        Then All clients should display the moves in correct order
        And the turn counter should be at (0)



       Scenario: Test if checkpoints are registered
         And There is a checkpoint in coordinate (0, 1, 1)
         And the robot is at position (0, 1)
         When A player ends on a checkpoint with the index (1)
         Then Checkpoints passed increments with (1)

