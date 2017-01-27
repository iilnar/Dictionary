package com.ilnar.sandbox.dictionary;

import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ilnar on 03.06.16.
 */
public abstract class Dictionary {
    enum State {
        READING, READY
    }

    State state = State.READY;

    int version = 0;

    abstract public void addWord(DictionaryRecord word);

    abstract public void addWords(List<DictionaryRecord> list);

    abstract public List<DictionaryRecord> search(String prefix);

    public int getVersion() {
        return version;
    }

    public void setVersion(int version) {
        this.version = version;
    }

    public List<DictionaryRecord> getWords() {
        return search("");
    }

    protected void read(File f) {
        state = State.READING;
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new FileReader(f));
            read(reader);
        } catch (IOException e) {
            Log.w(TAG, e);
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            state = State.READY;
        }

    }

    protected void read(Reader is) throws IOException {
        read(new JsonReader(is));
    }

    protected void read(JsonReader reader) throws IOException {
        long begin = System.currentTimeMillis();
        reader.beginObject();
        while (reader.hasNext()) {
            String name = reader.nextName();
            switch (name) {
                case "version":
                    version = reader.nextInt();
                    break;
                case "entries":
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginObject();

                        String word = null;
                        String pos = null;
                        LinkedList<String> translation = new LinkedList<>();
                        LinkedList<String> examples = new LinkedList<>();

                        while (reader.hasNext()) {
                            name = reader.nextName().toLowerCase();
                            switch (name) {
                                case "word":
                                    word = reader.nextString();
                                    break;
                                case "pos":
                                    pos = reader.nextString();
                                    break;
                                case "translation":
                                    reader.beginArray();
                                    while (reader.hasNext()) {
                                        translation.add(reader.nextString());
                                    }
                                    reader.endArray();
                                    break;
                                case "examples":
                                    reader.beginArray();
                                    while (reader.hasNext()) {
                                        examples.add(reader.nextString());
                                    }
                                    reader.endArray();
                                    break;
                                default:
                                    reader.skipValue();
                            }
                        }

                        addWord(new DictionaryRecord(word, pos, translation.toArray(new String[1]), examples.toArray(new String[1])));

                        reader.endObject();
                    }
                    reader.endArray();
                    break;
                default:
                    reader.skipValue();
                    break;
            }
        }
        reader.endObject();
        long end = System.currentTimeMillis();
        Log.d(TAG, String.format("Reading took%d ms", end - begin));
    }


    private static final String TAG = "Dictionary";
}
