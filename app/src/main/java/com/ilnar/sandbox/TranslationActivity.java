package com.ilnar.sandbox;

import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import java.util.logging.Logger;

/**
 * Created by ilnar on 29.05.16.
 */
public class TranslationActivity extends AppCompatActivity {
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
        Log.d("Translation", word);
        Log.d("Translation", translation);
        TextView wordView = (TextView)findViewById(R.id.word);
        TextView translationView = (TextView)findViewById(R.id.translation);
        if (wordView != null) {
            wordView.setText(word);
        }
        if (translationView != null) {
            translationView.setText(translation);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.translation_menu, menu);
        return true;
    }
}
