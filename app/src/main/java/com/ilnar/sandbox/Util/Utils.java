package com.ilnar.sandbox.Util;

import android.app.Activity;
import android.content.res.AssetManager;
import android.util.Log;

import java.io.DataOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

/**
 * Created by ilnar on 04.06.16.
 */
public class Utils {
    private static File externalFilesDir;

    private static AssetManager assetManager;

    public static String dictionaryFileName = "newdata.dict";

    public static String updateURL = "http://rain.ifmo.ru/~sabirzyanov/download.php";

    public static void init(Activity activity) {
        externalFilesDir = activity.getExternalFilesDir(null);
        assetManager = activity.getAssets();
    }

    public static File getDictionaryFile(boolean loadFromAsset) {
        File dictionaryFile = new File(externalFilesDir, dictionaryFileName);
        if (loadFromAsset && !dictionaryFile.exists()) {
            try (InputStream in = assetManager.open("data.dict");
                 DataOutputStream out = new DataOutputStream(new FileOutputStream(dictionaryFile))) {
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) > 0) {
                    out.write(buffer, 0, read);
                }
            } catch (IOException e) {
                Log.w(LOG_TAG, e);
            }
        }
        return dictionaryFile;
    }

    public static final String LOG_TAG = Utils.class.getName();
}