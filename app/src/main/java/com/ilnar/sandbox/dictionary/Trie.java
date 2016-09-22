package com.ilnar.sandbox.dictionary;

import android.util.Log;

import java.io.File;
import java.io.IOException;
import java.io.Reader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ilnar on 01.06.16.
 */
public class Trie extends Dictionary {
    private class Node {
        private HashMap<Character, Node> link;
        private DictionaryRecord record;

        Node() {
            this.link = new HashMap<>(4);
        }

        void setRecord(DictionaryRecord record) {
            this.record = record;
        }

        DictionaryRecord getRecord() {
            return record;
        }

        boolean containsNode(Character c) {
            return link.containsKey(c);
        }

        Node getNode(Character c) {
            if (!containsNode(c)) {
                link.put(c, new Node());
            }
            return link.get(c);
        }
    }

    public static final int MISSPELLS_ALLOWED = 2;
    private Node root = new Node();

    public Trie() {
        root = new Node();
    }

    public Trie(File f) {
        try {
            read(f);
        } catch (IOException e) {
            Log.w(TAG, e);
        }
    }

    public Trie(Reader reader) {
        try {
            read(reader);
        } catch (IOException e) {
            Log.w(TAG, e);
        }
    }

    public int getVersion() {
        return version;
    }

    public void addWord(DictionaryRecord record) {
        Node current = root;
        for (int i = 0; i < record.getWord().length(); i++) {
            current = current.getNode(record.getWord().charAt(i));
        }
        current.setRecord(record);
    }

    public void addWords(List<DictionaryRecord> words) {
        for (DictionaryRecord record : words) {
            addWord(record);
        }
    }

    private void search(String word, Node node, char prevChar, int[] prev, List<DictionaryRecord> result) {
        int[] current = new int[word.length() + 1];
        current[0] = prev[0] + 1;
        int min = current[0];
        for (int i = 1; i <= word.length(); i++) {
            current[i] = prev[i  -1] + (word.charAt(i - 1) == prevChar ? 0 : 1);
            current[i] = Math.min(current[i], current[i - 1] + 1);
            current[i] = Math.min(current[i], prev[i] + 1);
            min = Math.min(min, current[i]);
        }
        if (node.getRecord() != null && current[word.length()] <= MISSPELLS_ALLOWED) {
            result.add(node.getRecord());
        }
        if (min <= MISSPELLS_ALLOWED) {
            for (Map.Entry<Character, Node> characterNodeEntry : node.link.entrySet()) {
                search(word, characterNodeEntry.getValue(), characterNodeEntry.getKey(), current, result);
            }
        }
    }

    private void searchPrefix(String prefix, int pos, Node node, List<DictionaryRecord> result) {
        if (pos < prefix.length()) {
            if (node.containsNode(prefix.charAt(pos))) {
                searchPrefix(prefix, pos + 1, node.getNode(prefix.charAt(pos)), result);
            }
        } else {
            if (node.getRecord() != null) {
                result.add(node.getRecord());
            }
            for (Map.Entry<Character, Node> characterNodeEntry : node.link.entrySet()) {
                Node nextNode = characterNodeEntry.getValue();
                searchPrefix(prefix, pos + 1, nextNode, result);
            }
        }
    }

    public List<DictionaryRecord> search(String prefix) {
        while (state != State.READY) {
        }
        List <DictionaryRecord> result = new ArrayList<>();
        int j = prefix.length() - 1;
        while (j > 0 && Character.isSpaceChar(prefix.charAt(j))) {
            j--;
        }
        prefix = prefix.substring(0, j + 1);
        searchPrefix(prefix, 0, root, result);
        if (result.isEmpty()) {
            int[] arr = new int[prefix.length() + 1];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = i;
            }
            for (Map.Entry<Character, Node> characterNodeEntry : root.link.entrySet()) {
                search(prefix, characterNodeEntry.getValue(), characterNodeEntry.getKey(), arr, result);
            }
        }
        Collections.sort(result);
        return result;
    }

    private static final String TAG = "Trie";
}
