# Created by frede at 23-06-2024
Feature: Lobby browser
  # Enter feature description here
  Background: The game is initialized
    Given the game is initialized

  Scenario: opening then closing the lobby browser while the server is offline
    Given the server is "" offline
    And the player opens the "main menu"
    Then the "main menu" should be shown
    When the player opens the "lobby browser"
    Then the "lobby browser" should be shown
    Then the lobby browser should show a message that the server is offline
    When the player opens the "main menu"
    Then the "main menu" should be shown


  Scenario: joining a lobby from the lobby browser while the server is online
    Given the server is "not" offline
    And the player opens the "main menu"
    Then the "main menu" should be shown
    When the player opens the "lobby browser"
    Then the "lobby browser" should be shown
    When a lobby has been created on the server
    And another player is in the lobby
    And the player joins the lobby
    Then the "lobby" should be shown
    And the other player should be in the lobby


  Scenario: creating a lobby from the lobby browser while the server is offline


  Scenario: entering a game from the lobby while the server is online

  Scenario: leaving a lobby, then going to main menu while the server is online