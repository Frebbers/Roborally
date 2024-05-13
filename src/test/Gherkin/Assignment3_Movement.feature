@test
Feature: Movement mechanics

  Background: The game is initialized
    Given the game is initialized

  Scenario: A player collision occurs
    Given the robot is facing "South"
    And the robot is at position (0, 0)
    And another robot is at position (0, 1)
    And the robot has programmed a "forward" card
    And The player presses execute current register
    Then the robot should be at position (0, 1)
    And there should be a robot at position (0, 2)

  Scenario: A player collides with a wall
    Given the robot is facing "South"
    And the robot is at position (0, 0)
    And the robot has programmed a "forward" card
    And there is a wall at position (0, 0), "South"
    And The player presses execute current register
    Then the robot should be at position (0, 0)
    And the robot should be facing "South"