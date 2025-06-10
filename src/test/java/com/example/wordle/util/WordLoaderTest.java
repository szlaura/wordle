package com.example.wordle.util;

import com.example.wordle.handler.WordListIOException;
import com.example.wordle.model.WordleGame;
import org.junit.jupiter.api.Test;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

class WordLoaderTest {

    @Test
    void testWordList_ValidInputStream_ReturnsValidList(){
        String wordList = String.join("\n", "apple", "elephant", "car", "0five", "two**", "");
        InputStream inputStream = new ByteArrayInputStream(wordList.getBytes());
        List<String> result = WordLoader.loadWordList(inputStream, "test");
        assertEquals(List.of("apple"), result);
        assertTrue(result.stream().allMatch(word -> word.length() == WordleGame.MAX_LETTERS));
        assertTrue(result.stream().allMatch(word -> word.chars().allMatch(Character::isLetter)));
        assertTrue(result.stream().allMatch(word -> word.matches("[a-z]*")));
        assertTrue(result.stream().noneMatch(String::isBlank));
        assertTrue(result.stream().allMatch(word -> word.equals(word.toLowerCase())));
    }

    @Test
    void testWordList_NUllInputStream_ThrowsException(){
        IllegalArgumentException exception = assertThrows(IllegalArgumentException.class,
                () -> WordLoader.loadWordList(null, "test-null"));
        assertEquals("Word list file not found: test-null", exception.getMessage());
    }

    @Test
    void testWordList_IOException_ThrowsException(){
        InputStream inputStream = new InputStream() {
            @Override
            public int read() throws java.io.IOException {
                throw new java.io.IOException("Test IOException");
            }
        };

        WordListIOException exception = assertThrows(WordListIOException.class,
                () -> WordLoader.loadWordList(inputStream, "test-ioexception"));
        assertEquals("Error reading word list file: test-ioexception", exception.getMessage());
    }
}
