Feature: Apply programming to move the robot

  Background: The game is initialized
    Given the game is initialized

  Scenario: Robot moves forward
    Given the robot is facing "South"
    And the robot is at position (0, 0)
    And the turn counter is at (0)
    When the robot has programmed a "Forward" card
    Then the robot should be at position (0, 1)
    And the turn counter should be at (1)

    Scenario: Robot turns around