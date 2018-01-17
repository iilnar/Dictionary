package com.antat.dictionary.dictionary;

import android.util.JsonReader;

import com.antat.dictionary.Util.XoredFileInputStream;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Reader;
import java.util.LinkedList;
import java.util.List;

/**
 * Created by ilnar on 03.06.16.
 */
public abstract class Dictionary {
    int version = 0;

    abstract public void addWord(DictionaryRecord word);

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

    synchronized protected void read(File f) {
        Reader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader(new XoredFileInputStream(f), "UTF-8"));
            read(reader);
        } catch (IOException e) {
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        long et = System.currentTimeMillis();

    }

    protected void read(Reader is) throws IOException {
        read(new JsonReader(is));
    }

    protected void read(JsonReader reader) throws IOException {
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

                        addWord(new DictionaryRecord(word, pos, translation.toArray(new String[translation.size()]), examples.toArray(new String[examples.size()])));

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
    }


    private static final String TAG = "Dictionary";
}
