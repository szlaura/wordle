package com.example.wordle.model;

import com.example.wordle.handler.NoGameStartedException;
import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class WordleGame {

    public static final int MAX_ATTEMPTS = 5;
    public static final int MAX_LETTERS = 5;

    private final String answer;
    private final List<String> previousAttempts = new ArrayList<>();
    private int remainingAttempts;
    private boolean finished = false;

    public WordleGame(String answer) {
        Objects.requireNonNull(answer, "Answer cannot be null!\n");
        this.answer = answer.toLowerCase();
        this.remainingAttempts = MAX_ATTEMPTS;
    }

    /**
     *Handles the player's guess and updates the game state accordingly.
     *
     * @param guess The player's guess.
     * @throws NoGameStartedException If the guess is attempted after the game is ended.
     * @throws IllegalArgumentException If the guess is null, contains invalid character, invalid length,
     * or already guessed.
     */
    public void attempt(String guess) {
        if (finished) throw new NoGameStartedException("Game over! Type 'start' for a new game.", null);

        if (guess == null || guess.length() != MAX_LETTERS || !guess.chars().allMatch(Character::isLetter))
            throw new IllegalArgumentException("Guess must be " + MAX_LETTERS+ " alphabetic characters long!" +
                " (No digits or symbols allowed)\n");

        String guessLowerCase = guess.toLowerCase();
        if (previousAttempts.contains(guessLowerCase))
            throw new IllegalArgumentException("You have already guessed " + "this word!\n");

        previousAttempts.add(guessLowerCase);
        remainingAttempts--;

        if (answer.equals(guessLowerCase) || remainingAttempts == 0)
            finished = true;

    }
}
