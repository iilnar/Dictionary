package com.ilnar.sandbox.dictionary;

import android.support.annotation.NonNull;

/**
 * Created by ilnar on 29.05.16.
 */
public class DictionaryRecord implements Comparable<DictionaryRecord> {
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

    @Override
    public boolean equals(Object o) {
        return o instanceof DictionaryRecord && word.equals(((DictionaryRecord) o).word);
    }

    @Override
    public int hashCode() {
        return word.hashCode();
    }

    @Override
    public int compareTo(@NonNull DictionaryRecord another) {
        return word.compareTo(another.word);
    }
}
