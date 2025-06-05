package com.example.wordle.model;

import lombok.Getter;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

@Getter
public class WordleGame {

    public static final int MAX_ATTEMPTS = 5;
    public static final int MAX_LETTERS = 5;

    private final String answer;
    private final List<String> previousAttempts = new ArrayList<>();
    private int remainingAttempts;
    private boolean finished = false;

    public WordleGame(String answer) {
        Objects.requireNonNull(answer, "Answer cannot be null!");

        if (answer.length() != MAX_LETTERS) throw new IllegalArgumentException("Answer must be " +
                MAX_LETTERS + " letters long!");

        this.answer = answer.toLowerCase();
        this.remainingAttempts = MAX_ATTEMPTS;
    }
}
