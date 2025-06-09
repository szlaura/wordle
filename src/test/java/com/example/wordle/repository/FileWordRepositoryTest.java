package com.example.wordle.repository;

import com.example.wordle.handler.WordListIOException;
import com.example.wordle.util.WordLoader;
import org.junit.jupiter.api.Test;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class FileWordRepositoryTest {

    @Test
    void testLoadWords_ReturnsValidList() {
        FileWordRepository fileWordRepository = new FileWordRepository("/testwords.txt");
        List<String> words = fileWordRepository.loadWords();
        assertEquals(List.of("apple", "brave", "cloud", "dream"), words);
    }

    @Test
    void testLoadWords_FileNotFound(){
        FileWordRepository fileWordRepository = new FileWordRepository("/notfound.txt");
        Exception exception = assertThrows(IllegalArgumentException.class, fileWordRepository::loadWords);
        assertEquals("Word list file not found: /notfound.txt", exception.getMessage());
    }

    @Test
    void testLoadWords_IOException(){
        FileWordRepository fileWordRepository = new FileWordRepository("/non.txt"){
            @Override
            public List<String> loadWords() {
                return WordLoader.loadWordList(new InputStream(){
                    @Override
                    public int read() throws IOException {
                        throw new IOException("Test IOException");
                    }
                }, "/non.txt");
            }
        };
        assertThrows(WordListIOException.class, fileWordRepository::loadWords);
    }
}
