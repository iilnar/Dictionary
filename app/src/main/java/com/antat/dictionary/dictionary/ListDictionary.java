package com.antat.dictionary.dictionary;

import com.antat.dictionary.Util.Alphabet;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by ilnar on 30.05.16.
 */
public class ListDictionary extends Dictionary {
    private List<DictionaryRecord> data = new ArrayList<>();

    private static final int MISSPELLS_ALLOWED = 2;

    public ListDictionary() {
    }

    public ListDictionary(File f){
        read(f);
    }

    public ListDictionary(Reader reader) {
        try {
            read(reader);
        } catch (IOException e) {
        }
    }

    public void addWord(DictionaryRecord record) {
        data.add(record);
    }

    private boolean startsWith(String word, String prefix) {
        Alphabet alphabet = Alphabet.getInstance();
        if (prefix.length() > word.length()) {
            return false;
        }
        for (int i = 0; i < prefix.length(); i++) {
            if (alphabet.compare(word.charAt(i), prefix.charAt(i)) != 0) {
                return false;
            }
        }
        return true;
    }

    public List<DictionaryRecord> search(String prefix) {
        List<DictionaryRecord> records = new ArrayList<>();
        for (DictionaryRecord record : data) {
            if (startsWith(record.getWord(), prefix)) {
                records.add(record);
            }
        }
        if (records.size() == 0) {
            for (DictionaryRecord record : data) {
                if (editDistance(prefix, record.getWord()) <= MISSPELLS_ALLOWED) {
                    records.add(record);
                }
            }
        }
        return records;
    }

    static int editDistance(String a, String b) {
        Alphabet alphabet = Alphabet.getInstance();
        int n = a.length() + 1;
        int m = b.length() + 1;
        if (n > m) {
            return editDistance(b, a);
        }
        int k = MISSPELLS_ALLOWED;
        int[] cost = new int[m];
        int[] newCost = new int[m];
        for (int i = 0; i < m; i++) {
            cost[i] = i;
            newCost[i] = Integer.MAX_VALUE / 4;
        }
        for (int j = 1; j < n; j++) {
            newCost[0] = j;
            int minimalDist = newCost[0];
            for (int i = Math.max(1, j - k); i < Math.min(m, j + k + 1); i++) {
                if (alphabet.compare(a.charAt(j - 1), b.charAt(i - 1)) == 0) {
                    newCost[i] = cost[i - 1];
                } else {
                    newCost[i] = cost[i - 1] + 1;
                }
                newCost[i] = Math.min(newCost[i], cost[i] + 1);
                newCost[i] = Math.min(newCost[i], newCost[i - 1] + 1);
                minimalDist = Math.min(minimalDist, newCost[i]);
            }
            if (j >= 2 && minimalDist > MISSPELLS_ALLOWED) {
                return minimalDist;
            }
            int[] t = cost;
            cost = newCost;
            newCost = t;
        }
        return cost[m - 1];
    }

    private static final String TAG = "ListDictionary";
}
