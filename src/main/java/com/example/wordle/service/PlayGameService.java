package com.example.wordle.service;

import com.example.wordle.handler.EmptyWordListException;
import com.example.wordle.model.LetterResult;
import com.example.wordle.model.WordleGame;
import com.example.wordle.repository.WordRepository;
import lombok.Getter;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Random;
import java.util.stream.IntStream;

@Service
public class PlayGameService {

    private final WordRepository wordRepository;
    private final Random random = new Random();
    private List<String> wordList;
    private boolean triedLoading;
    @Getter
    private WordleGame currentGame;

    public PlayGameService(WordRepository wordRepository){
        this.wordRepository = wordRepository;
    }

    /**
     * Lazy loads the word list from the repository on the first invocation. If the word list is empty, invalid or
     * cannot be loaded for any reason, this method throws an exception.
     *
     * @throws EmptyWordListException if the word list cannot be loaded for any reason
     */
    public void checkWordListLoaded(){
        if (!triedLoading) {
            triedLoading = true;
            wordList = List.copyOf(wordRepository.loadWords());
        }

        if (wordList == null || wordList.isEmpty()) throw new EmptyWordListException("Word list is empty, contains" +
                "only invalid words, or could not be loaded.");
    }

    /**
     * Selects a random word for the correct answer and starts the game initializing with this word.
     */
    public void startGame() {
        checkWordListLoaded();
        String answerWord = wordList.get(random.nextInt(wordList.size()));
        currentGame = new WordleGame(answerWord);
    }

    /**
     * Processes the user's guess for the current round in the game.
     *
     * @param guess The player's guess in the current round.
     * @return List of LetterResult feedback for all the letters in the guess.
     * @throws IllegalStateException If there is no active game.
     */
    public List<LetterResult> attemptGuess(String guess) {
        if (currentGame == null) {
            throw new IllegalStateException("No game started! Type 'start' to begin.");
        }

        currentGame.attempt(guess);
        return evaluateAnswer(currentGame.getAnswer(), guess);
    }

    /**
     * Evaluates the player's current guess against the answer word, returns feedback for each letter.
     *
     * @param answer The answer word to compare with.
     * @param guess The player's guess
     * @return List of LetterResult for all the letters in the guess.
     */
    public List<LetterResult> evaluateAnswer(String answer, String guess) {
        final int ANSWER_LEN = answer.length();
        answer = answer.toLowerCase();
        guess = guess.toLowerCase();

        char[] guessArr = guess.toCharArray();
        char[] answerArr = answer.toCharArray();
        LetterResult[] results = new LetterResult[ANSWER_LEN];
        boolean[] used = new boolean[ANSWER_LEN];

        // highlight the correct letter
        IntStream.range(0, ANSWER_LEN)
                .filter(letterPos -> guessArr[letterPos] == answerArr[letterPos])
                .forEach(letterPos -> {
                    results[letterPos] = LetterResult.CORRECT;
                    used[letterPos] = true;
                });

        // highlight the present and absent letters
        IntStream.range(0, ANSWER_LEN).forEach(guessPos -> {
            if (results[guessPos] != null) return;
            IntStream.range(0, ANSWER_LEN)
                    .filter(answerPos -> !used[answerPos] && guessArr[guessPos] == answerArr[answerPos] &&
                            guessPos != answerPos)
                    .findFirst()
                    .ifPresentOrElse(
                            idx -> {
                                results[guessPos] = LetterResult.PRESENT;
                                used[idx] = true;
                            },
                            () -> results[guessPos] = LetterResult.ABSENT
                    );
        });

        return List.of(results);
    }

    /**
     * Checks if the current game is a win or out of attempts.
     *
     * @return True if the game is finished, false otherwise.
     */
    public boolean isFinished() {
        return currentGame != null && currentGame.isFinished();
    }

    /**
     * Decides if the current last guess is the correct word answer.
     *
     * @return True if the current last guess matches the answer, false otherwise.
     */
    public boolean isWinner() {
        if (currentGame == null || currentGame.getPreviousAttempts().isEmpty()) return false;
        String lastGuess = currentGame.getPreviousAttempts().getLast();
        return currentGame.getAnswer().equals(lastGuess);
    }

    /**
     * Gets the number of remaining attempts of the current game.
     *
     * @return An integer number of guesses left, or 0 if there's no game.
     */
    public int getRemainingAttempts() {
        return currentGame != null ? currentGame.getRemainingAttempts() : 0;
    }
}
