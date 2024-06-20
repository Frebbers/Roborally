@test
  Feature: Testing the implementation of various multiplayer features.
    Background: The game is initialized
      Given the game is initialized

      Scenario: The moves work on multiple clients
        Given A game has been initialized online
        And the robot has programmed a "Move forward" card
        And the turn counter should be at (0)
        When All players have finished their programming phase
        Then All clients should display the moves in correct order
        And the turn counter should be at (0)

        Scenario: