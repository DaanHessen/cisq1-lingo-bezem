package nl.hu.cisq1.lingo.application;

import nl.hu.cisq1.lingo.words.application.WordService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class DictionaryServiceTest {
    private WordService wordService;
    private DictionaryService dictionaryService;

    @BeforeEach
    void setUp() {
        wordService = mock(WordService.class);
        dictionaryService = new DictionaryService(wordService);
    }

    @Test
    @DisplayName("exists should delegate to wordService.wordExists")
    void existsShouldDelegateToWordService() {
        String word = "hello";
        when(wordService.wordExists(word)).thenReturn(true);

        boolean result = dictionaryService.exists(word);

        assertTrue(result);
        verify(wordService, times(1)).wordExists(word);
    }

    @Test
    @DisplayName("exists should return false when word does not exist")
    void existsShouldReturnFalseWhenWordDoesNotExist() {
        String word = "nonexistent";
        when(wordService.wordExists(word)).thenReturn(false);

        boolean result = dictionaryService.exists(word);

        assertFalse(result);
        verify(wordService, times(1)).wordExists(word);
    }

    @Test
    @DisplayName("randomWord should delegate to wordService.provideRandomWord")
    void randomWordShouldDelegateToWordService() {
        int length = 5;
        String expectedWord = "words";
        when(wordService.provideRandomWord(length)).thenReturn(expectedWord);

        String result = dictionaryService.randomWord(length);

        assertEquals(expectedWord, result);
        verify(wordService, times(1)).provideRandomWord(length);
    }

    @Test
    @DisplayName("randomWord should handle different word lengths")
    void randomWordShouldHandleDifferentLengths() {
        when(wordService.provideRandomWord(5)).thenReturn("apple");
        when(wordService.provideRandomWord(6)).thenReturn("banana");
        when(wordService.provideRandomWord(7)).thenReturn("coconut");

        assertEquals("apple", dictionaryService.randomWord(5));
        assertEquals("banana", dictionaryService.randomWord(6));
        assertEquals("coconut", dictionaryService.randomWord(7));
        
        verify(wordService, times(1)).provideRandomWord(5);
        verify(wordService, times(1)).provideRandomWord(6);
        verify(wordService, times(1)).provideRandomWord(7);
    }
}
