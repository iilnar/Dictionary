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
        data = new ArrayList<>();
        data.add(new DictionaryRecord("aba", "caba"));
        data.add(new DictionaryRecord("aa", "casadfba"));
        data.add(new DictionaryRecord("aaba", "cadagsba"));
        data.add(new DictionaryRecord("abfaa", "caadfba"));
    }

    public void addRecord(DictionaryRecord record) {
        data.add(record);
    }

    public List<DictionaryRecord> getData() {
        return data;
    }

    public List<DictionaryRecord> search(String prefix) {
        List<DictionaryRecord> records = new ArrayList<>();
        for (DictionaryRecord record : data) {
            if (record.getWord().startsWith(prefix)) {
                records.add(record);
            }
        }
        Log.d(this.getClass().getCanonicalName(), String.valueOf(records.size()));
        return records;
    }
}
