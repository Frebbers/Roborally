# Created by frede at 23-06-2024
Feature: Lobby browser
  # Enter feature description here
  Background: The game is initialized
    Given the game is initialized


  Scenario: joining a lobby from the lobby browser while the server is online and starting a game
    Given the server is "not" offline
    And A game has been initialized online
    When a lobby has been created on the server
    And another player is in the lobby
    Then the other player should be in the lobby
    When the game is started
    Then All players should be in the the Phase "INITIALISATION"
    And the game should exist on the server


  Scenario: Attempting to create a lobby while the server is offline
    Given the server is offline
    When a lobby has been created on the server
    Then the lobby should not be created
  #  Then the lobby should not be created
   # And the server should be offline


  Scenario: leaving a lobby, then going to main menu while the server is online
    Given the server is "not" offline
    And A game has been initialized online
    When a lobby has been created on the server
    Then the other player should be in the lobby
    When the game is started
    When the player leaves the game
    Then the player should be in the main menu