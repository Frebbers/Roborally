Feature: Implementing all the programming cards in the game.

  Background: The game is initialized

    Scenario 1 : I get and use a permanent upgrade card. (Virus module).
      Given There are upgrade spaces on the player mat, to equip your permanent upgrade(s).
      And I draw an upgrade card.
      And the card is a Permanant upgrade from the deck of upgrade cards.
      When I equip the upgrade it works from the next instance it can be activated.
      Then The upgrade is in effect.

  Scenario 2 : I get and use a temporary upgrade card (HACK)
    Given The card has been purchased already, and is ready to be used mid round
    When The card is used it goes out of play, sent to the discrd pile.
    And there might be a special programming card assigned to the affected player.
    Then the action desitred action is performed at any time on YOUR turn.

  Scenario 3 :