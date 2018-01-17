package com.antat.dictionary.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.antat.dictionary.R;
import com.antat.dictionary.Util.KeyboardListener;
import com.antat.dictionary.dictionary.DictionaryRecord;


public class AddTranslationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_translation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.add_translation_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        final String word = getIntent().getStringExtra(DictionaryRecord.WORD);
        final EditText wordView = (EditText) findViewById(R.id.word);
        final EditText translationView = (EditText) findViewById(R.id.translation);
        View footer = findViewById(R.id.llFooter);

        wordView.getViewTreeObserver().addOnGlobalLayoutListener(new KeyboardListener(footer, wordView, translationView));
        wordView.setText(word);
        if (getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
        Button submit = (Button)findViewById(R.id.submit);
        if (submit != null) {
            submit.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //TODO send data
                }
            });
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    private static final String TAG = "AddTranslationActivity";
}
