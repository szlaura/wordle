package com.example.wordle.repository;

import java.util.List;

public interface WordRepository {
    List<String> loadWords();
}
