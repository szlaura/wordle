package com.example.wordle.util;

import com.example.wordle.handler.EmptyWordListException;
import com.example.wordle.handler.WordListIOException;
import com.example.wordle.model.WordleGame;

import java.io.*;
import java.util.List;

public class WordLoader {

    /**
     * Loads the words of {@link WordleGame#MAX_LETTERS} length from a resource file.
     *
     * @param input The input stream for reading words
     * @param wordSource File name for the source
     * @return List of valid lowercase words
     * @throws IllegalArgumentException If the file is not found
     * @throws WordListIOException If there is an I/O error while reading the file
     */
    public static List<String> loadWordList(InputStream input, String wordSource){
        if (input == null) throw new IllegalArgumentException("Word list file not found: " + wordSource);
        try(BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(input))) {

            List<String> wordList = bufferedReader.lines()
                    .filter(word -> word.chars().allMatch(Character::isLetter))
                    .filter(word -> word.length() == WordleGame.MAX_LETTERS)
                    .map(String::toLowerCase)
                    .toList();

            if(wordList.isEmpty()) throw new EmptyWordListException("Word list file is empty or contains no valid " +
                    "words: " + wordSource + "\n");

            return wordList;

        } catch (IOException | UncheckedIOException e) {
            throw new WordListIOException("Error reading word list file: " +wordSource, e);
        }
    }
}
