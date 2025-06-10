package com.example.wordle.service;

import com.example.wordle.handler.EmptyWordListException;
import com.example.wordle.handler.NoGameStartedException;
import com.example.wordle.handler.WordListIOException;
import com.example.wordle.model.LetterResult;
import com.example.wordle.model.WordleGame;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.Collections;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class PlayGameServiceTest {

    private static final List<String> DICTIONARY = List.of("water", "apple", "pizza", "fruit", "eagle", "otter");

    private PlayGameService serviceWithValidWords;
    private PlayGameService serviceWithEmptyList;
    private PlayGameService serviceWithIOException;
    private PlayGameService serviceWithNotFound;

    @BeforeEach

    void setUp() {
        serviceWithValidWords = new PlayGameService(() -> DICTIONARY);
        serviceWithEmptyList = new PlayGameService(Collections::emptyList);
        serviceWithNotFound = new PlayGameService(() -> { throw new IllegalArgumentException("File not found"); });
        serviceWithIOException = new PlayGameService(() -> { throw new WordListIOException("I/O error!", null); });
    }

    @Test
    void checkWordListLoaded_ValidWordList_CorrectLoaded() {
        serviceWithValidWords.checkWordListLoaded();
        assertDoesNotThrow(serviceWithValidWords::checkWordListLoaded);
    }

    @Test
    void checkWordListLoaded_IOError_ThrowsWordListIOException() {
        assertThrows(WordListIOException.class, serviceWithIOException::checkWordListLoaded);
    }

    @Test
    void checkWordListLoaded_FileNotFound_ThrowsIllegalArgumentException() {
        assertThrows(IllegalArgumentException.class, serviceWithNotFound::checkWordListLoaded);
    }

    @Test
    void checkWordListLoaded_NullList_ThrowsEmptyWordListException() {
        PlayGameService playGameService = new PlayGameService(() -> {
            throw new EmptyWordListException("word list empty");
        });
        assertThrows(EmptyWordListException.class, playGameService::checkWordListLoaded);
    }

    @Test
    void checkWordListLoaded_EmptyList_ThrowsEmptyWordListException() {
        assertThrows(EmptyWordListException.class, serviceWithEmptyList::checkWordListLoaded);
    }

    @Test
    void startGame_ValidWords_StartsNewGame() {
        serviceWithValidWords.startGame();
        assertNotNull(serviceWithValidWords.getCurrentGame());
        assertEquals(WordleGame.MAX_ATTEMPTS, serviceWithValidWords.getCurrentGame().getRemainingAttempts());
        assertFalse(serviceWithValidWords.isFinished());
    }

    @Test
    void startGame_NotValidWords_ThrowsException() {
        assertThrows(EmptyWordListException.class, serviceWithEmptyList::startGame);
        assertThrows(WordListIOException.class, serviceWithIOException::startGame);
        assertThrows(IllegalArgumentException.class, serviceWithNotFound::startGame);
    }

    @Test
    void attemptGuess_BeforeGameStarted_ThrowsException() {
        Exception ex = assertThrows(IllegalStateException.class, () -> serviceWithValidWords.attemptGuess("water"));
        assertTrue(ex.getMessage().toLowerCase().contains("no game started"));
    }

    @Test
    void attemptGuess_ReturnsCorrectFeedback() {
        serviceWithValidWords.startGame();
        String answer = serviceWithValidWords.getCurrentGame().getAnswer();
        List<LetterResult> result = serviceWithValidWords.attemptGuess(answer);
        assertEquals(List.of(LetterResult.CORRECT, LetterResult.CORRECT, LetterResult.CORRECT, LetterResult.CORRECT,
                LetterResult.CORRECT), result);
        assertTrue(serviceWithValidWords.isFinished());
        assertTrue(serviceWithValidWords.isWinner());
    }

    @Test
    void attemptGuess_IncorrectGuess_FeedbackIsCorrect() {
        serviceWithValidWords.startGame();
        String answer = serviceWithValidWords.getCurrentGame().getAnswer();
        String different = DICTIONARY.stream().filter(w -> !w.equals(answer)).findFirst().orElseThrow();
        List<LetterResult> result = serviceWithValidWords.attemptGuess(different);
        if (!different.equals(answer)) {
            assertNotEquals(List.of(LetterResult.CORRECT, LetterResult.CORRECT, LetterResult.CORRECT,
                    LetterResult.CORRECT, LetterResult.CORRECT), result);
        }
        assertFalse(serviceWithValidWords.isFinished());
        assertFalse(serviceWithValidWords.isWinner());
    }

    @Test
    void attemptGuess_GameOver_MaxAttempts() {
        PlayGameService playGameService = new PlayGameService(() -> DICTIONARY);

        playGameService.startGame();
        String answer = playGameService.getCurrentGame().getAnswer();

        int attempts = 0;
        for (String word : DICTIONARY) {
            if (!word.equals(answer)) {
                playGameService.attemptGuess(word);
                attempts++;
                if (attempts == WordleGame.MAX_ATTEMPTS) break;
            }
        }
        assertTrue(playGameService.isFinished());
        assertFalse(playGameService.isWinner());

        String newGuess = DICTIONARY.stream()
                .filter(w -> !w.equals(answer))
                .skip(WordleGame.MAX_ATTEMPTS)
                .findFirst()
                .orElse("apple");
        assertThrows(NoGameStartedException.class, () -> playGameService.attemptGuess(newGuess));
    }

    @Test
    void isFinished_NoGame() {
        PlayGameService playGameService = new PlayGameService(() -> List.of("apple"));
        assertFalse(playGameService.isFinished());
    }

    @Test
    void isFinished_WhenFinished_ReturnsTrue() {
        PlayGameService playGameService = new PlayGameService(() -> List.of("paper", "fruit", "pizza", "eagle",
                "apple", "water"));
        playGameService.startGame();

        String answer = playGameService.getCurrentGame().getAnswer();
        List<String> guesses = List.of("paper", "fruit", "pizza", "eagle", "apple", "water");
        int count = 0;
        for (String guess : guesses) {
            if (!guess.equals(answer)) {
                playGameService.attemptGuess(guess);
                count++;
                if (count == WordleGame.MAX_ATTEMPTS) break;
            }
        }

        if (!playGameService.isFinished()) {
            for (String g : guesses) {
                if (!g.equals(answer) && !playGameService.getCurrentGame().getPreviousAttempts().contains(g)) {
                    playGameService.attemptGuess(g);
                    break;
                }
            }
        }
        assertTrue(playGameService.isFinished());
    }

    @Test
    void isWinner_NoGame_ReturnsFalse() {
        PlayGameService playGameService = new PlayGameService(() -> List.of("apple"));
        assertFalse(playGameService.isWinner());
    }

    @Test
    void isWinner_GameStartedNoAttempts_ReturnFalse() {
        PlayGameService playGameService = new PlayGameService(() -> List.of("apple"));
        playGameService.startGame();
        assertFalse(playGameService.isWinner());
    }

    @Test
    void getRemainingAttempts_ReturnZeroWhenNoGame() {
        assertEquals(0, serviceWithValidWords.getRemainingAttempts());
    }

    @Test
    void getRemainingAttempts_DecrementProperly() {
        serviceWithValidWords.startGame();
        int before = serviceWithValidWords.getRemainingAttempts();
        String guess = DICTIONARY.get(0).equals(serviceWithValidWords.getCurrentGame()
                .getAnswer()) ? DICTIONARY.get(1) : DICTIONARY.get(0);
        serviceWithValidWords.attemptGuess(guess);
        assertEquals(before - 1, serviceWithValidWords.getRemainingAttempts());
    }

    @Test
    void evaluateAnswer_PresentLetter_TwoCase() {
        PlayGameService playGameService = new PlayGameService(() -> List.of("water", "otter"));
        List<LetterResult> result = playGameService.evaluateAnswer("water", "otter");
        assertEquals(List.of(
                LetterResult.ABSENT,
                LetterResult.ABSENT,
                LetterResult.CORRECT,
                LetterResult.CORRECT,
                LetterResult.CORRECT), result);
    }

    @Test
    void evaluateAnswer_PresentLetter_TreeCase() {
        PlayGameService playGameService = new PlayGameService(() -> List.of("water", "eagle"));
        List<LetterResult> result = playGameService.evaluateAnswer("water", "eagle");
        assertEquals(List.of(
                LetterResult.PRESENT,
                LetterResult.CORRECT,
                LetterResult.ABSENT,
                LetterResult.ABSENT,
                LetterResult.ABSENT), result);
    }
}
