package com.ilnar.sandbox.dictionary;

import android.support.annotation.NonNull;

import com.ilnar.sandbox.Util.Alphabet;

/**
 * Created by ilnar on 29.05.16.
 */
public class DictionaryRecord implements Comparable<DictionaryRecord> {
    public static final String WORD = "word";
    public static final String TRANSLATION = "translation";

    private final String word;
    private final String pos;
    private final String[] translation;
    private final String[] examples;

    public DictionaryRecord(String word, String pos, String[] translation, String[] examples) {
        this.word = word;
        this.pos = pos;
        this.translation = translation;
        this.examples = examples;
    }

    public String getWord() {
        return word;
    }

    public String[] getTranslation() {
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
        Alphabet alphabet = Alphabet.getInstance();
        for (int i = 0; i < Math.min(word.length(), another.word.length()); i++) {
            int r = alphabet.compare(word.charAt(i), another.word.charAt(i));
            if (r != 0) {
                return r;
            }
        }
        if (word.length() < another.word.length()) {
            return -1;
        } else if (word.length() == another.word.length()) {
            return 0;
        } else {
            return 1;
        }
    }
}

