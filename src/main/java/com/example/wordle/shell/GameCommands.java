package com.example.wordle.shell;

import com.example.wordle.model.LetterResult;
import com.example.wordle.model.WordleGame;
import com.example.wordle.service.PlayGameService;
import lombok.RequiredArgsConstructor;
import org.jline.utils.AttributedString;
import org.jline.utils.AttributedStringBuilder;
import org.jline.utils.AttributedStyle;
import org.springframework.shell.standard.ShellComponent;
import org.springframework.shell.standard.ShellMethod;
import org.springframework.shell.standard.ShellOption;
import java.util.List;
import java.util.stream.IntStream;

@ShellComponent
@RequiredArgsConstructor
public class GameCommands {

    private final PlayGameService playGameService;

    /**
     * Starts a new Wordle game and informs the user to start guessing.
     *
     * @return Start message
     */
    @ShellMethod(key = "start", value = "Start a new Wordle game.")
    public String start() {
        playGameService.startGame();
        return "Game started, type 'guess <word>' to make a guess.";
    }

    /**
     * Handles a guess for the current game round, evaluates the guess, and returns color-coded feedback for each letter.
     * Provides information about the remaining attempts and win/game over conditions.
     *
     * @param guessWord The word that the user guessed. It has to be {@link WordleGame#MAX_LETTERS} letters long.
     * @return An AttributedString feedback text about the guess or game state.
     */
    @ShellMethod(key = "guess", value = "Guess the secret word, type 'guess <word>.'")
    public AttributedString guess(@ShellOption(help = "Your "+ WordleGame.MAX_LETTERS +" letter long guess")
                                      String guessWord) {
        WordleGame currentGame = playGameService.getCurrentGame();

        if (currentGame == null) return new AttributedString("Let's start a new game with 'start' command!",
                AttributedStyle.DEFAULT.foreground(AttributedStyle.YELLOW));

        if (playGameService.isFinished()) return new AttributedString("Game over. Let's start a new game with " +
                "start command!", AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));

        AttributedStringBuilder stringBuilder = new AttributedStringBuilder();

        try{
            List<LetterResult> letterResults = playGameService.attemptGuess(guessWord);

            IntStream.range(0, guessWord.length()).forEach(letterPos -> {
                char currentGuessChar = guessWord.toLowerCase().charAt(letterPos);
                int letterColor = switch (letterResults.get(letterPos)) {
                    case CORRECT -> AttributedStyle.GREEN;
                    case PRESENT -> AttributedStyle.YELLOW;
                    case ABSENT -> 8; //ANSI gray (HEX #808080)
                };
                stringBuilder.append(String.valueOf(currentGuessChar), AttributedStyle.DEFAULT.foreground(letterColor));
            });

            if (playGameService.isWinner()) {
                stringBuilder.append("""
                                \nCongratulations! You guessed the word!
                                Type 'start' for another game or 'exit' to quit.
                                """, AttributedStyle.DEFAULT.foreground(AttributedStyle.GREEN));

            } else if (playGameService.isFinished()) {
                stringBuilder.append("""
                                \nGame over! The word was %s.
                                Type 'start' for another game or 'exit' to quit.
                                """.formatted(currentGame.getAnswer()),
                                AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));

            } else {
                stringBuilder.append("\nYou have ")
                        .append(String.valueOf(playGameService.getRemainingAttempts()))
                        .append(" more attempts.");
            }

        } catch (IllegalStateException e) {
            return new AttributedString(e.getMessage(), AttributedStyle.DEFAULT.foreground(AttributedStyle.RED));
        }

        return stringBuilder.toAttributedString();
    }

    /**
     * Shows rules and available commands for the Wordle CLI game.
     *
     * @return An AttributedString text about the rules and available commands for the game.
     */
    @ShellMethod(key = {"info"}, value = "Provide Wordle CLI rules and available commands.")
    public AttributedString info() {
        return new AttributedString("""
                --------------------------------------------------------
                                 How to play Wordle CLI
                --------------------------------------------------------
                Rules:
                - Guess the %d-letter word in %d tries.
                - After each guess,  you will see feedback:
                    - [Green]: Correct letter in the  correct position.
                    - [Yellow]: Correct letter in the wrong position.
                    - [Gray]: Letter not in the word.
                
                Commands:
                - 'start': Start a new game.
                - 'guess <word>': Submit your guess. (e.g., guess apple)
                - 'info': Show this help message.
                - 'exit': Quit the game.
                
                Type 'start' to begin.
                --------------------------------------------------------
                """.formatted(WordleGame.MAX_LETTERS, WordleGame.MAX_ATTEMPTS),
                AttributedStyle.DEFAULT.foreground(AttributedStyle.CYAN));
    }
}
