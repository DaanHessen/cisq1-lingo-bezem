package nl.hu.cisq1.lingo.application;

import nl.hu.cisq1.lingo.domain.Dictionary; 
import nl.hu.cisq1.lingo.words.application.WordService;

public class DictionaryService implements Dictionary {
    private final WordService wordService;

    public DictionaryService(WordService wordService) {
        this.wordService = wordService;
    }

    @Override
    public boolean exists(String word) {
        return wordService.wordExists(word);
    }

    @Override
    public String randomWord(int length) {
        return wordService.provideRandomWord(length);
    }
}
