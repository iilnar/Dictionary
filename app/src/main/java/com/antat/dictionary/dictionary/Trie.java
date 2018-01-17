package com.antat.dictionary.dictionary;

import android.support.annotation.NonNull;

import java.io.File;
import java.util.AbstractSet;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * Created by ilnar on 01.06.16.
 */
public class Trie extends Dictionary {
    private static final int MISSPELLS_ALLOWED = 2;
    private Node root = new Node();

    public Trie(File f) {
        read(f);
    }

    public int getVersion() {
        return version;
    }

    public void addWord(DictionaryRecord record) {
        Node current = root;
        for (int i = 0; i < record.getWord().length(); i++) {
            current = current.getNodeOrAdd(record.getWord().charAt(i));
        }
        current.setRecord(record);
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
            for (Map.Entry<Character, Node> characterNodeEntry : node.entrySet()) {
                search(word, characterNodeEntry.getValue(), characterNodeEntry.getKey(), current, result);
            }
        }
    }

    private void searchPrefix(String prefix, int pos, Node node, List<DictionaryRecord> result) {
        if (node == null) {
            return;
        }
        if (pos < prefix.length()) {
            if (node.containsNode(prefix.charAt(pos))) {
                searchPrefix(prefix, pos + 1, node.getNode(prefix.charAt(pos)), result);
            }
        } else {
            if (node.getRecord() != null) {
                result.add(node.getRecord());
            }
            for (Map.Entry<Character, Node> characterNodeEntry : node.entrySet()) {
                Node nextNode = characterNodeEntry.getValue();
                searchPrefix(prefix, pos + 1, nextNode, result);
            }
        }
    }

    synchronized public List<DictionaryRecord> search(String prefix) {
        List <DictionaryRecord> result = new ArrayList<>();
        searchPrefix(prefix, 0, root, result);
        if (result.isEmpty()) {
            int[] arr = new int[prefix.length() + 1];
            for (int i = 0; i < arr.length; i++) {
                arr[i] = i;
            }
            for (Map.Entry<Character, Node> characterNodeEntry : root.entrySet()) {
                search(prefix, characterNodeEntry.getValue(), characterNodeEntry.getKey(), arr, result);
            }
        }
        Collections.sort(result);
        return result;
    }

    private static final String TAG = "Trie";
}


class Node {
    private HashMap<Character, Node> link;
    //small object
    char smoC;
    Node smoN;

    private DictionaryRecord record;

    Node() {}

    void setRecord(DictionaryRecord record) {
        this.record = record;
    }

    DictionaryRecord getRecord() {
        return record;
    }

    boolean containsNode(Character c) {
        if (link == null) {
            return smoC == c;
        }
        return link.containsKey(c);
    }

    Node getNode(Character c) {
        if (link == null) {
            if (c == smoC) {
                return smoN;
            }
            return null;
        }
        return link.get(c);
    }

    Node getNodeOrAdd(char c) {
        if (link == null) {
            if (c == smoC) {
                return smoN;
            } else {
                if (smoC == '\u0000') {
                    smoC = c;
                    smoN = new Node();
                    return smoN;
                } else {
                    link = new HashMap<>(4);
                    link.put(smoC, smoN);
                    smoC = '\u0000';
                    smoN = null;
                    Node node = new Node();
                    link.put(c, node);
                    return node;
                }
            }
        }
        if (!containsNode(c)) {
            link.put(c, new Node());
        }
        return link.get(c);
    }

    Set<Map.Entry<Character, Node>> entrySet() {
        if (link != null) {
            return link.entrySet();
        }
        return new AbstractSet<Map.Entry<Character, Node>>() {
            @NonNull
            @Override
            public Iterator<Map.Entry<Character, Node>> iterator() {
                return new Iterator<Map.Entry<Character, Node>>() {
                    boolean hasNext = smoN != null;

                    @Override
                    public boolean hasNext() {
                        return hasNext;
                    }

                    @Override
                    public Map.Entry<Character, Node> next() {
                        hasNext = false;
                        return new Map.Entry<Character, Node>() {
                            @Override
                            public Character getKey() {
                                return smoC;
                            }

                            @Override
                            public Node getValue() {
                                return smoN;
                            }

                            @Override
                            public Node setValue(Node object) {
                                return null;
                            }
                        };
                    }

                    @Override
                    public void remove() {
                    }
                };
            }

            @Override
            public int size() {
                return smoC == '\u0000' ? 0 : 1;
            }
        };
    }
}
