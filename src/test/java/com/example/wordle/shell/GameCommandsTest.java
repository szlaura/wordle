package com.example.wordle.shell;

import com.example.wordle.handler.EmptyWordListException;
import com.example.wordle.model.LetterResult;
import com.example.wordle.model.WordleGame;
import com.example.wordle.service.PlayGameService;
import org.jline.utils.AttributedString;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.Mockito.*;

class GameCommandsTest {

    @Test
    void testStart_ReturnStartMessage() {
        PlayGameService mockService = mock(PlayGameService.class);
        GameCommands commands = new GameCommands(mockService);
        doNothing().when(mockService).startGame();
        String result = commands.start();
        assertTrue(result.contains("Game started"));
    }

    @Test
    void testStart_handleServiceException() {
        PlayGameService mockService = mock(PlayGameService.class);
        doThrow(new EmptyWordListException("Word list problem!")).when(mockService).startGame();
        GameCommands gameCommands = new GameCommands(mockService);
        assertThrows(EmptyWordListException.class, gameCommands::start);
    }

    @Test
    void testGuess_ReturnNoGameStarted() {
        PlayGameService mockService = mock(PlayGameService.class);
        GameCommands commands = new GameCommands(mockService);
        when(mockService.getCurrentGame()).thenReturn(null);
        AttributedString result = commands.guess("apple");
        assertTrue(result.toString().toLowerCase().contains("start a new game"));
    }

    @Test
    void testGuess_ReturnGameOver() {
        PlayGameService mockedService = mock(PlayGameService.class);
        WordleGame finishedGame = mock(WordleGame.class);
        when(mockedService.getCurrentGame()).thenReturn(finishedGame);
        when(mockedService.isFinished()).thenReturn(true);
        GameCommands commands = new GameCommands(mockedService);
        AttributedString result = commands.guess("apple");
        assertTrue(result.toString().toLowerCase().contains("game over"));
    }

    @Test
    void testGuess_NotCorrectGuess_FeedbackMoreAttempts() {
        PlayGameService mockService = mock(PlayGameService.class);
        WordleGame game = mock(WordleGame.class);
        when(mockService.getCurrentGame()).thenReturn(game);
        when(mockService.isFinished()).thenReturn(false);
        when(mockService.attemptGuess("apple")).thenReturn(List.of(LetterResult.PRESENT, LetterResult.CORRECT,
                LetterResult.ABSENT, LetterResult.ABSENT, LetterResult.CORRECT));
        when(mockService.isWinner()).thenReturn(false);
        when(mockService.getRemainingAttempts()).thenReturn(3);
        GameCommands commands = new GameCommands(mockService);
        AttributedString result = commands.guess("apple");
        String out = result.toString();
        assertTrue(out.contains("more attempts"));
    }

    @Test
    void testGuess_CorrectGuess_FeedbackWin() {
        PlayGameService mockService = mock(PlayGameService.class);
        WordleGame game = mock(WordleGame.class);
        when(mockService.getCurrentGame()).thenReturn(game);
        when(mockService.isFinished()).thenReturn(false);
        when(mockService.attemptGuess("apple")).thenReturn(List.of(LetterResult.CORRECT, LetterResult.CORRECT,
                LetterResult.CORRECT, LetterResult.CORRECT, LetterResult.CORRECT));
        when(mockService.isWinner()).thenReturn(true);
        GameCommands commands = new GameCommands(mockService);
        AttributedString result = commands.guess("apple");
        assertTrue(result.toString().toLowerCase().contains("congratulations"));
    }

    @Test
    void testGuess_GameOverAfterGuess_FeedbackGameOver() {
        PlayGameService mockService = mock(PlayGameService.class);
        WordleGame game = mock(WordleGame.class);
        when(mockService.getCurrentGame()).thenReturn(game);
        when(mockService.isFinished()).thenReturn(false).thenReturn(true);
        when(mockService.attemptGuess("apple")).thenReturn(List.of(LetterResult.ABSENT, LetterResult.ABSENT,
                LetterResult.ABSENT, LetterResult.ABSENT, LetterResult.ABSENT));
        when(mockService.isWinner()).thenReturn(false);
        when(game.getAnswer()).thenReturn("paper");
        GameCommands commands = new GameCommands(mockService);
        AttributedString result = commands.guess("apple");
        assertTrue(result.toString().toLowerCase().contains("game over"));
        assertTrue(result.toString().toLowerCase().contains("paper"));
    }

    @Test
    void testGuess_ServiceThrowsIllegalStateException() {
        PlayGameService mockService = mock(PlayGameService.class);
        WordleGame game = mock(WordleGame.class);
        when(mockService.getCurrentGame()).thenReturn(game);
        when(mockService.isFinished()).thenReturn(false);
        when(mockService.attemptGuess(anyString())).thenThrow(new IllegalStateException("Unexpected guess: apple"));
        GameCommands commands = new GameCommands(mockService);
        AttributedString result = commands.guess("apple");
        assertTrue(result.toString().contains("Unexpected guess: apple"));
    }

    @Test
    void testInfo_ReturnsRules() {
        PlayGameService mockService = mock(PlayGameService.class);
        GameCommands commands = new GameCommands(mockService);
        AttributedString info = commands.info();
        assertTrue(info.toString().toLowerCase().contains("how to play"));
        assertTrue(info.toString().toLowerCase().contains("guess"));
    }
}
