package com.ilnar.sandbox.dictionary;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilnar on 30.05.16.
 */
public class ListDictionary extends Dictionary {
    private List<DictionaryRecord> data = new ArrayList<>();

    public ListDictionary() {
    }

    public ListDictionary(File f){
        try {
            read(f);
        } catch (IOException e) {
            Log.w(LOG_tAG, e);
        }
    }

    public ListDictionary(Reader reader) {
        try {
            read(reader);
        } catch (IOException e) {
            Log.w(LOG_tAG,e);
        }
    }

    public void addWord(DictionaryRecord record) {
        data.add(record);
    }

    public void addWords(List<DictionaryRecord> words) {
        data.addAll(words);
    }

    public List<DictionaryRecord> search(String prefix) {
        Log.d(ListDictionary.class.getName(), "search called");
        List<DictionaryRecord> records = new ArrayList<>();
        for (DictionaryRecord record : data) {
            if (record.getWord().startsWith(prefix)) {
                records.add(record);
            }
        }
        return records;
    }

    private static final String LOG_tAG = ListDictionary.class.getName();
}