package nl.hu.cisq1.lingo.application;

import nl.hu.cisq1.lingo.words.data.WordRepository;
import nl.hu.cisq1.lingo.words.domain.Word;
import nl.hu.cisq1.lingo.words.domain.exception.WordLengthNotSupportedException;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
class DictionaryServiceIntegrationTest {
    private static final String WORD_5 = "bruhh";
    private static final String WORD_6 = "kroket";
    private static final String WORD_7 = "bananen";
    private static final String WORD_INVALID = "zzzzz";

    @Autowired
    private DictionaryService dictionaryService;

    @Autowired
    private WordRepository wordRepository;

    @BeforeEach
    void seedDatabase() {
        wordRepository.deleteAll();
        wordRepository.save(new Word(WORD_5));
        wordRepository.save(new Word(WORD_6));
        wordRepository.save(new Word(WORD_7));
    }

    @AfterEach
    void cleanDatabase() {
        wordRepository.deleteAll();
    }

    @Test
    @DisplayName("Word that exists in database is validated as existing")
    void existsReturnsTrueForPersistedWord() {
        boolean result = dictionaryService.exists(WORD_5);

        assertTrue(result);
    }

    @Test
    @DisplayName("Word that does not exist in database is validated as non-existing")
    void existsReturnsFalseForNonPersistedWord() {
        boolean result = dictionaryService.exists(WORD_INVALID);

        assertFalse(result);
    }

    @Test
    @DisplayName("Random five-letter word is retrieved from database")
    void randomWordReturnsFiveLetterWord() {
        String result = dictionaryService.randomWord(5);

        assertEquals(WORD_5, result);
    }

    @Test
    @DisplayName("Random six-letter word is retrieved from database")
    void randomWordReturnsSixLetterWord() {
        String result = dictionaryService.randomWord(6);

        assertEquals(WORD_6, result);
    }

    @Test
    @DisplayName("Random seven-letter word is retrieved from database")
    void randomWordReturnsSevenLetterWord() {
        String result = dictionaryService.randomWord(7);

        assertEquals(WORD_7, result);
    }

    @Test
    @DisplayName("Requesting word with unsupported length throws exception")
    void randomWordThrowsExceptionForUnsupportedLength() {
        int unsupportedLength = 10;

        assertThrows(
            WordLengthNotSupportedException.class,
            () -> dictionaryService.randomWord(unsupportedLength)
        );
    }
}
