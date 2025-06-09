package com.example.wordle.repository;

import com.example.wordle.util.WordLoader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class FileWordRepository implements WordRepository {

    private final String dictionaryFile;

    public FileWordRepository(@Value("${wordle.dictionary}") String dictionaryFile) {
        this.dictionaryFile = dictionaryFile;
    }

    @Override
    public List<String> loadWords() {
        return WordLoader.loadWordList(dictionaryFile);
    }
}
