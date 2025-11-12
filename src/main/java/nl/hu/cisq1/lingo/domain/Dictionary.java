package nl.hu.cisq1.lingo.domain;

public interface Dictionary {
    boolean exists(String word);
    String randomWord(int length);
}