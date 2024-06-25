@test
Feature: MVP requirements

  Background: The game is initialized
    Given the game is initialized

  Scenario: The player can only see their own cards
    Given the phase is "PROGRAMMING"
    And there are (2) players in the game
    Then the player cannot see other players' cards

  Scenario: Two games are started on the same server
    Given A game has been initialized online
    Then another game can be initialized online
