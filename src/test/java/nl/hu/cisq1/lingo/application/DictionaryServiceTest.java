package nl.hu.cisq1.lingo.application;

import nl.hu.cisq1.lingo.words.application.WordService;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DictionaryServiceTest {
    
    @Mock
    private WordService wordService;
    
    @InjectMocks
    private DictionaryService dictionaryService;

    @Test
    @DisplayName("Word existence check delegates to word service")
    void existingWordReturnsTrue() {
        String existingWord = "bruhh";
        when(wordService.wordExists(existingWord)).thenReturn(true);

        boolean result = dictionaryService.exists(existingWord);

        assertTrue(result);
        verify(wordService).wordExists(existingWord);
    }

    @Test
    @DisplayName("Non-existing word returns false")
    void nonExistingWordReturnsFalse() {
        String nonExistentWord = "pneumonoultramicroscopicsilicovolcanoconiosis";
        when(wordService.wordExists(nonExistentWord)).thenReturn(false);

        boolean result = dictionaryService.exists(nonExistentWord);

        assertFalse(result);
        verify(wordService).wordExists(nonExistentWord);
    }

    @Test
    @DisplayName("Random word retrieval delegates to word service")
    void randomWordRetrievesFromWordService() {
        int wordLength = 5;
        String expectedWord = "bruhh";
        when(wordService.provideRandomWord(wordLength)).thenReturn(expectedWord);

        String result = dictionaryService.randomWord(wordLength);

        assertEquals(expectedWord, result);
        verify(wordService).provideRandomWord(wordLength);
    }

    @Test
    @DisplayName("Random words can be retrieved for different lengths")
    void randomWordSupportsMultipleLengths() {
        when(wordService.provideRandomWord(5)).thenReturn("bruhh");
        when(wordService.provideRandomWord(6)).thenReturn("kroket");
        when(wordService.provideRandomWord(7)).thenReturn("bananen");

        String fiveLetterWord = dictionaryService.randomWord(5);
        String sixLetterWord = dictionaryService.randomWord(6);
        String sevenLetterWord = dictionaryService.randomWord(7);

        assertEquals("bruhh", fiveLetterWord);
        assertEquals("kroket", sixLetterWord);
        assertEquals("bananen", sevenLetterWord);
        verify(wordService).provideRandomWord(5);
        verify(wordService).provideRandomWord(6);
        verify(wordService).provideRandomWord(7);
    }
}
