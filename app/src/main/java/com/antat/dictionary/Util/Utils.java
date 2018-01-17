package com.antat.dictionary.Util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.AssetManager;

import com.antat.dictionary.activities.MainActivity;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ilnar on 04.06.16.
 */
public class Utils {
    private static File filesDir;

    private static AssetManager assetManager;

    public static String dictionaryFileNames[] = new String[]{"ttr.json", "rtt.json"};

    public static void init(MainActivity activity) {
        filesDir = activity.getDir("dictionaries", Context.MODE_PRIVATE);
        assetManager = activity.getAssets();

        SharedPreferences preferences = activity.getSharedPreferences();
        int appVersion = activity.getAppVersion();
        int previousVersion = preferences.getInt("dictionary_from_version", -1);

        for (String s : dictionaryFileNames) {
            if (shouldUpdateFromAssets(s, appVersion, previousVersion)) {
                updateFileFromAssets(s);
            }
        }

        preferences.edit().putInt("dictionary_from_version", appVersion).apply();
    }

    private static boolean shouldUpdateFromAssets(String assetFilename, int appVersion, int previousVersion) {
        return previousVersion < appVersion || !new File(filesDir, assetFilename).exists();
    }

    private static void updateFileFromAssets(String assetFilename) {
        File dictionaryFile = new File(filesDir, assetFilename);

        InputStream in = null;
        DataOutputStream out = null;
        try {
            in = assetManager.open(assetFilename);
            out = new DataOutputStream(new FileOutputStream(dictionaryFile));
            byte[] buffer = new byte[1024];
            int read;
            while ((read = in.read(buffer)) > 0) {
                out.write(buffer, 0, read);
            }
        } catch (IOException e) {
        } finally {
            if (in != null) {
                try {
                    in.close();
                } catch (IOException ignored) {
                }
            }
            if (out != null) {
                try {
                    out.close();
                } catch (IOException ignored) {
                }
            }
        }
    }

    public static File getDictionaryFile(int dictNumber) {
        return new File(filesDir, dictionaryFileNames[dictNumber]);
    }

    private static final String TAG = "Utils";
}