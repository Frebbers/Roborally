@test
Feature: MVP requirements

  Background: The game is initialized
    Given the game is initialized

  Scenario: The player can only see their own cards
    Given the phase is "PROGRAMMING"
    And there are (2) players in the game
    Then the player cannot see other players' cards
