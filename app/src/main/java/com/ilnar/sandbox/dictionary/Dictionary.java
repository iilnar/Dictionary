package com.ilnar.sandbox.dictionary;

import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
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

    public void write(File f) {
        Writer os = null;
        state = State.READING;
        try {
            os = new BufferedWriter(new FileWriter(f));
            List<DictionaryRecord> words = getWords();
            os.write("{");
            os.write(String.format("\"version\":%d, \n", getVersion()));
            os.write("\"data\": [\n");
            int count = words.size();
            for (int i = 0; i < count; i++) {
                os.write("[\n");
                os.write(String.format("\"%s\",\n", words.get(i).getWord()));
                os.write(String.format("\"%s\"\n", words.get(i).getTranslation()));
                if (i + 1 != count) {
                    os.write("],\n");
                } else {
                    os.write("]\n");
                }
            }
            os.write("]\n");
            os.write("}\n");
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (os != null) {
                try {
                    os.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            state = State.READY;
        }
    }

    protected void read(File f) throws IOException {
        read(new BufferedReader(new FileReader(f)));
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
                case "data":
                    reader.beginArray();
                    while (reader.hasNext()) {
                        reader.beginArray();
                        addWord(new DictionaryRecord(reader.nextString(), reader.nextString()));
                        reader.endArray();
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
