package com.ilnar.sandbox.dictionary;

import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilnar on 30.05.16.
 */
public class Dictionary {
    private List<DictionaryRecord> data;

    public Dictionary() {
        Log.d(Dictionary.class.getName(), "constructor called");
        data = new ArrayList<>();
        data.add(new DictionaryRecord("one", "caba"));
        data.add(new DictionaryRecord("two", "casadfba"));
        data.add(new DictionaryRecord("three", "cadagsba"));
        data.add(new DictionaryRecord("four", "caa\ndfba"));
        data.add(new DictionaryRecord("five", "caadfba"));
        data.add(new DictionaryRecord("six", "caa\nd\nfba"));
        data.add(new DictionaryRecord("seven", "caa\ndfba"));
        data.add(new DictionaryRecord("eight", "caa\ndfba"));
        data.add(new DictionaryRecord("nine", "caa\ndfba"));
        data.add(new DictionaryRecord("ten", "caa\ndfba"));
        data.add(new DictionaryRecord("eleven", "caa\ndfba"));
    }

    public void addRecord(DictionaryRecord record) {
        data.add(record);
    }

    public List<DictionaryRecord> getData() {
        return data;
    }

    public List<DictionaryRecord> search(String prefix) {
        Log.d(Dictionary.class.getName(), "search called");
        List<DictionaryRecord> records = new ArrayList<>();
        for (DictionaryRecord record : data) {
            if (record.getWord().startsWith(prefix)) {
                records.add(record);
            }
        }
        return records;
    }
}
