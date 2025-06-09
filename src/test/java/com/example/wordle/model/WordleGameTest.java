package com.example.wordle.model;

import com.example.wordle.handler.NoGameStartedException;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class WordleGameTest {

    @Test
    void testInitConstructor_Valid() {
        WordleGame wordleGame = new WordleGame("apple");
        assertEquals("apple", wordleGame.getAnswer());
        assertEquals(WordleGame.MAX_ATTEMPTS, wordleGame.getRemainingAttempts());
        assertEquals(0, wordleGame.getPreviousAttempts().size());
        assertFalse(wordleGame.isFinished());
        assertEquals(WordleGame.MAX_LETTERS, wordleGame.getAnswer().length());
    }

    @Test
    void testAttempt_ValidGuess() {
        WordleGame wordleGame = new WordleGame("apple");
        wordleGame.attempt("apple");
        assertEquals(WordleGame.MAX_ATTEMPTS - 1, wordleGame.getRemainingAttempts());
        assertTrue(wordleGame.getPreviousAttempts().contains("apple"));
        assertTrue(wordleGame.isFinished());
    }

    @Test
    void testAttempt_ValidGuess_GuessedCorrectly(){
        WordleGame wordleGame = new WordleGame("apple");
        wordleGame.attempt("apple");
        assertEquals(WordleGame.MAX_ATTEMPTS - 1, wordleGame.getRemainingAttempts());
        assertTrue(wordleGame.isFinished());
    }

    @Test
    void testAttempt_InvalidGuess(){
        WordleGame wordleGame = new WordleGame("apple");
        assertThrows(IllegalArgumentException.class, () -> wordleGame.attempt(""));
        assertThrows(IllegalArgumentException.class, () -> wordleGame.attempt(null));
        assertThrows(IllegalArgumentException.class, () -> wordleGame.attempt("123"));
        assertThrows(IllegalArgumentException.class, () -> wordleGame.attempt("car12"));
        assertThrows(IllegalArgumentException.class, () -> wordleGame.attempt(" "));
        assertThrows(IllegalArgumentException.class, () -> wordleGame.attempt("elephant"));
        assertThrows(IllegalArgumentException.class, () -> wordleGame.attempt("!@#"));
    }

    @Test
    void testAttempt_AlreadyGuessed(){
        WordleGame wordleGame = new WordleGame("apple");
        wordleGame.attempt("pizza");
        Exception exception = assertThrows(IllegalArgumentException.class,
                () -> wordleGame.attempt("pizza"));
        assertEquals("You have already guessed this word!\n", exception.getMessage());
    }

    @Test
    void testAttempt_NullGuessThrowsException(){
        Exception exception =  assertThrows(NullPointerException.class, () -> new WordleGame(null));
        assertEquals("Answer cannot be null!\n", exception.getMessage());
    }

    @Test
    void testAttempt_NoMoreAttemptsThrowsException(){
        WordleGame wordleGame = new WordleGame("apple");
        wordleGame.attempt("again");
        wordleGame.attempt("pizza");
        wordleGame.attempt("floor");
        wordleGame.attempt("water");
        wordleGame.attempt("fruit");
        assertTrue(wordleGame.isFinished());
        Exception exception = assertThrows(NoGameStartedException.class, () -> wordleGame.attempt("apple"));
        assertEquals("Game over! Type 'start' for a new game.", exception.getMessage());

    }

    @Test
    void testAttempt_NoGameStartedThrowsException(){
        WordleGame wordleGame = new WordleGame("apple");
        wordleGame.attempt("apple");
        Exception exception = assertThrows(NoGameStartedException.class, () -> wordleGame.attempt("apple"));
        assertEquals("Game over! Type 'start' for a new game.", exception.getMessage());
    }

}
