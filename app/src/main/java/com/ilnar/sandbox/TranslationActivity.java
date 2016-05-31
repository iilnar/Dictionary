package com.ilnar.sandbox;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;

import java.util.logging.Logger;

/**
 * Created by ilnar on 29.05.16.
 */
public class TranslationActivity extends AppCompatActivity {
    private TextView translationView;
    private final static String LOG_TAG = TranslationActivity.class.getName();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.translation_layout);
        Bundle b = getIntent().getExtras();
        String word = b.getString("word");
        String translation = b.getString("translation");
        if (word == null) {
            word = "No word";
        }
        if (translation == null) {
            translation = "No translation";
        }
        Log.d(LOG_TAG, word);
        Log.d(LOG_TAG, translation);
        translationView = (TextView)findViewById(R.id.translation);
        if (translationView != null) {
            translationView.setText(Html.fromHtml(String.format("<h1>%s</h1><br>%s", word, translation.replace("\n", "<br>"))));
//            translationView.setOnTouchListener(new View.OnTouchListener() {
//                @Override
//                public boolean onTouch(View v, MotionEvent event) {
//                    Log.d("touch", "touch");
//                    return false;
//                }
//            });
            translationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(LOG_TAG, translationView.getText()));
                    Log.d(LOG_TAG, "copied");
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.translation_menu, menu);
        return true;
    }
}
