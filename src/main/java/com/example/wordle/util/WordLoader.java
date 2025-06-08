package com.example.wordle.util;

import com.example.wordle.handler.WordLoadingException;
import com.example.wordle.model.WordleGame;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.List;
import java.util.Objects;

public class WordLoader {

    /**
     * Loads the words of {@link WordleGame#MAX_LETTERS} length from a resource file.
     *
     * @param wordSource the source path to the word list file
     * @return list of valid lowercase words
     * @throws IllegalArgumentException if the file is not found
     * @throws WordLoadingException if there is an I/O error while reading the file
     */
    public static List<String> loadWordList(String wordSource){
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(Objects.requireNonNull(
                WordLoader.class.getResourceAsStream(wordSource),
                "Word list file not found: " + wordSource)))) {

            return bufferedReader.lines()
                    .filter(word -> word.length() == WordleGame.MAX_LETTERS)
                    .map(String::toLowerCase)
                    .toList();

        } catch (IOException e) {
            throw new WordLoadingException("Error reading word list file: " +wordSource, e);
        }
    }
}
