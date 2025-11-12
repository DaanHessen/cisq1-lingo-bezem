Feature: Training for Lingo
    As a player, 
    I want to be able to train guessing Lingo words, 
    so that I can improve my Lingo skills.

    Scenario: Player starts a new game
        Given the application is running
        When the player starts a new game
        Then a new round is started
        And the game gives the player a word to guess (5 letters)

    Scenario: Player makes an incorrect guess
        Given the application is running
        And a round is started
        When the player makes a guess "SNAKE"
        Then the game responds with feedback indicating which letters are correct and in the correct position
        And the number of attempts remaining is decreased by one

    Scenario: Player makes a correct guess
        Given the application is running
        And a round is started
        When the player makes a guess "APPLE"
        Then the game responds with feedback indicating that the guess is correct
        And the round is marked as won and player moves to the next round

    Scenario: Player starts a new round
        When the application is running
        And the previous round was won
        And the last word had "<previous length>" letters
        When the player start a new round
        Then the word to guess has "<next length>" letters

        Examples:
        | previous length | next length |
        | 5               | 6           |
        | 6               | 7           |
        | 7               | 5           |

    Scenario: Player views past rounds
        Given the application is running
        When the player requests to view past rounds
        Then a list of past rounds with their outcomes (won/lost) is displayed

    Scenario: Player quits the game
        Given the application is running
        And a round is started
        When the player decides to forfeit a game
        Then the game is marked as lost, with all remaining attempts exausted
        And the correct word is reveiled to the player

    Scenario Outline: Guessing a word
        Given the application is running
        And a round is started with word "<word>"
        When the player makes a guess "<guess>"
        Then the game responds with feedback "<feedback>"

        Examples:
        | word  | guess | feedback                                    |
        | APPLE | APPLE | correct, correct, correct, correct, correct |
        | APPLE | APPLY | correct, correct, correct, correct, absent  |
        | APPLE | GRAPE | absent, absent, correct, absent, correct    |
        | APPLE | PLANE | present, absent, absent, present, absent    |

    Scenario: Score increases after successful guess
        Given the application is running
        And a round is started
        When the player makes a correct guess
        Then the score is increased

    #-------------------- exceptions --------------------

    Scenario: An eliminated player cannot start a new round
        Given the application is running
        And the player has been eliminated
        Then the player cannot start a new round

    Scenario: A new round cannot be started if the player is still playing
        Given the application is running
        And the player is in the middle of a round
        Then the player cannot start an additional new round

    Scenario: Player is out after five attempts in a round
        Given the application is running
        And a round is started
        When the player makes five incorrect guesses
        Then the round is marked as lost
        And the player is out

    Scenario: Player cannot guess if word is already guessed
        Given the application is running
        And a round is started
        And the word has been guessed correctly
        When the player tries to make another guess
        Then the guess is not accepted

    Scenario: Player loses an attempt for trying a non-existent word
        Given the application is running
        And a round is started
        When the player makes a guess with a non-existent word
        Then the number of attempts remaining is decreased by one
        And the guess is not counted as a valid attempt

    Scenario: Player cannot start new round if still guessing
        Given the application is running
        And a round is started
        And the player is still guessing
        When the player tries to start a new round
        Then the new round is not started

    Scenario: Player cannot start new round if no game exists
        Given the application is running
        And no game has been started
        When the player tries to start a new round
        Then the new round is not started