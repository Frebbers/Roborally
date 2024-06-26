@test
Feature: Implementing all the programming cards in the game.

  Background: The game is initialized
    Given the game is initialized

  Scenario: I get and program the card move back
    Given the robot is facing "South"
    And the robot is at position (0, 1)
    And the turn counter should be at (0)
    When the robot has programmed a "Back" card
    Then the robot should be at position (0, 0)
    And the turn counter should be at (1)


  Scenario: I get and program the card U-Turn
    Given the robot is facing "North"
    And the robot is at position (0, 0)
    And the turn counter should be at (0)
    When the robot has programmed a "u-turn" card
    Then the robot should be facing "South"
    And the turn counter should be at (1)
    
    
  Scenario: Left Or Right
    Given the robot is facing "East"
    And the robot is at position (2, 2)
    And the turn counter should be at (1)
    When the robot has programmed a "left or right" card
    And the phase is "INTERACTION"
    Then the robot should be facing "North" or "South"


  Scenario: Again
    Given the robot is facing "West"
    And the robot is at position (0, 1)
    And the turn counter should be at (0)
    And Last repository used was "Forward"
    When the robot has programmed a "Again" card
    Then the robot should be at position (0, 2)