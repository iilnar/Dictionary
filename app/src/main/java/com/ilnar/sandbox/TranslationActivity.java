package com.ilnar.sandbox;

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
    private TextView wordView;
    private TextView translationView;

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
        wordView = (TextView)findViewById(R.id.word);
        translationView = (TextView)findViewById(R.id.translation);
        if (wordView != null) {
            wordView.setText(word);
        }
        if (translationView != null) {
            translationView.setText(Html.fromHtml(translation.replace("\n", "<br>")));
        }
        View v = findViewById(R.id.translation_view);
        v.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                Log.d("touch", "touch");
                return false;
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.translation_menu, menu);
        return true;
    }
}
