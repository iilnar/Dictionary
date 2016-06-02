package com.ilnar.sandbox.dictionary;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by ilnar on 01.06.16.
 */
public class Trie {
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

        void write(DataOutputStream os) throws IOException {
            os.writeBoolean(this.record != null);
            if (this.record != null) {
                os.writeUTF(this.record.getTranslation());
            }
            os.writeInt(link.size());
            for (Map.Entry<Character, Node> entry : link.entrySet()) {
                os.writeChar(entry.getKey());
                entry.getValue().write(os);
            }
        }

        void read(DataInputStream is, StringBuilder sb) throws IOException {
            if (is.readBoolean()) {
                this.record = new DictionaryRecord(sb.toString(), is.readUTF());
            }
            int child = is.readInt();
            for (int i = 0; i < child; i++) {
                char c = is.readChar();
                Node newNode = new Node();
                link.put(c, newNode);
                newNode.read(is, sb.append(c));
                sb.deleteCharAt(sb.length() - 1);
            }
        }
    }

    public static final int MISSPELLS_ALLOWED = 2;
    private Node root;

    public Trie() {
        root = new Node();
    }

    public void add(DictionaryRecord record) {
        Node current = root;
        for (int i = 0; i < record.getWord().length(); i++) {
            current = current.getNode(record.getWord().charAt(i));
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
        List <DictionaryRecord> result = new ArrayList<>();
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

    public void read(File file) {
        try (DataInputStream is = new DataInputStream(new BufferedInputStream(new FileInputStream(file)))){
            root = new Node();
            root.read(is, new StringBuilder());
        } catch (IOException e) {
            //TODO log error
        }
    }

    public void write(File file) {
        try (DataOutputStream os = new DataOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
            root.write(os);
        } catch (IOException e) {
            //TODO log error
        }
    }
}
