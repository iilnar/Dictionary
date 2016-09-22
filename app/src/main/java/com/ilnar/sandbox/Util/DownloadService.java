package com.ilnar.sandbox.Util;

import android.os.AsyncTask;
import android.util.Log;
import android.widget.Toast;

import com.ilnar.sandbox.R;
import com.ilnar.sandbox.activities.MainActivity;
import com.ilnar.sandbox.database.RecentQueryDBHelper;
import com.ilnar.sandbox.dictionary.Dictionary;
import com.ilnar.sandbox.dictionary.ListDictionary;
import com.ilnar.sandbox.dictionary.Trie;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.ref.WeakReference;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by ilnar on 03.06.16.
 */
public class DownloadService extends AsyncTask<Void, Void, Void> {
    private WeakReference<MainActivity> reference;
    Dictionary old;
    File folder;
    boolean errorOccurred = false;

    public DownloadService(MainActivity activity) {
        reference = new WeakReference<>(activity);
        folder = activity.getExternalFilesDir(null);//TODO change to getFilesDir
        Toast.makeText(activity, R.string.base_update_start, Toast.LENGTH_LONG).show();
    }

    @Override
    protected Void doInBackground(Void... params) {
        try {
            File dictionaryFile = Utils.getDictionaryFile(false);
            if (dictionaryFile.exists()) {
                old = new Trie(dictionaryFile);
            } else {
                old = new ListDictionary();
            }
            URL url = new URL(Utils.updateURL);
            HttpURLConnection httpURLConnection = (HttpURLConnection) url.openConnection();
            httpURLConnection.setRequestMethod("GET");
            httpURLConnection.setRequestProperty("version", String.valueOf(old.getVersion()));
            httpURLConnection.setDoOutput(true);
            httpURLConnection.connect();

            InputStream in = httpURLConnection.getInputStream();

            Dictionary update = new ListDictionary(new InputStreamReader(in));

            if (update.getWords().isEmpty() ) {
                return null;
            }

            old.setVersion(update.getVersion());
            old.addWords(update.getWords());

            File tmpFile = File.createTempFile("data", "dict", folder);
            old.write(tmpFile);
            if (!dictionaryFile.exists() || dictionaryFile.delete()) {
                if (tmpFile.renameTo(dictionaryFile)) {
                    Log.d(TAG, "Updated successfully");
                } else {
                    Log.e(TAG, "Couldn't rename");
                    errorOccurred = true;
                }
            } else {
                Log.e(TAG, "Couldn't delete old dict");
                errorOccurred = true;
            }
            RecentQueryDBHelper recentQuery = RecentQueryDBHelper.getInstance(null);
            recentQuery.updateTranslations(update.getWords());

        } catch (IOException e) {
            Log.w(TAG, e);
            errorOccurred = true;
        }
        return null;
    }

    @Override
    protected void onPostExecute(Void aVoid) {
        MainActivity activity = reference.get();
        if (activity != null) {
            activity.setDictionary(old);
            if (!errorOccurred) {
                Toast.makeText(activity, R.string.base_update_success, Toast.LENGTH_LONG).show();
            } else {
                Toast.makeText(activity, R.string.base_update_failed, Toast.LENGTH_LONG).show();
            }
        }
    }

    private static final String TAG = "DownloadService";
}
