package com.ilnar.sandbox.dictionary;

/**
 * Created by ilnar on 29.05.16.
 */
public class DictionaryRecord {
    private final String word;
    private final String translation;

    public DictionaryRecord(String word, String translation) {
        this.word = word;
        this.translation = translation;
    }

    public String getWord() {
        return word;
    }

    public String getTranslation() {
        return translation;
    }
}
