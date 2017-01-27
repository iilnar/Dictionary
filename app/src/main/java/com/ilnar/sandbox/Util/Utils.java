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

    public static String dictionaryFileNames[] = new String[]{"ttr.json", "rtt.json"};

    public static void init(Activity activity) {
        externalFilesDir = activity.getExternalFilesDir(null);
        assetManager = activity.getAssets();
    }

    public static File getDictionaryFile(int dictNumber, boolean loadFromAsset) {
        File dictionaryFile = new File(externalFilesDir, dictionaryFileNames[dictNumber]);
        if (loadFromAsset && !dictionaryFile.exists()) {
            InputStream in = null;
            DataOutputStream out = null;
            try {
                in = assetManager.open(dictionaryFileNames[dictNumber]);
                out = new DataOutputStream(new FileOutputStream(dictionaryFile));
                byte[] buffer = new byte[1024];
                int read;
                while ((read = in.read(buffer)) > 0) {
                    out.write(buffer, 0, read);
                }
            } catch (IOException e) {
                Log.w(TAG, e);
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
        return dictionaryFile;
    }

    private static final String TAG = "Utils";
}