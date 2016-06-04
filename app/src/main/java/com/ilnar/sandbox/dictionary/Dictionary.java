package com.ilnar.sandbox.dictionary;

import android.util.JsonReader;
import android.util.Log;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.Reader;
import java.io.Writer;
import java.util.List;

/**
 * Created by ilnar on 03.06.16.
 */
public abstract class Dictionary {

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
        try (Writer os = new BufferedWriter(new FileWriter(f))) {
            List<DictionaryRecord> words = getWords();
            os.write("{");
            os.write("\"version\":1, \n");
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
            if (name.equals("version")) {
                version = reader.nextInt();
            } else if (name.equals("data")) {
                reader.beginArray();
                while (reader.hasNext()) {
                    reader.beginArray();
                    addWord(new DictionaryRecord(reader.nextString(), reader.nextString()));
                    reader.endArray();
                }
                reader.endArray();
            } else {
                reader.skipValue();
            }
        }
        reader.endObject();
        long end = System.currentTimeMillis();
        Log.d(LOG_TAG, String.format("Reading took%d ms", end - begin));
    }

    private static final String LOG_TAG = Dictionary.class.getName();
}
