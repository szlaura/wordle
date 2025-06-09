package com.example.wordle.model;

import org.junit.jupiter.api.Test;

import static org.hibernate.validator.internal.util.Contracts.assertNotNull;

class LetterResultTest {

    @Test
    void testEnumValues_Valid() {
        for (LetterResult letterResult : LetterResult.values()) {
            assertNotNull(letterResult.name());
        }
    }
}
