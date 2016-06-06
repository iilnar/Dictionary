package com.ilnar.sandbox.activities;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;

import com.ilnar.sandbox.R;
import com.ilnar.sandbox.Util.KeyboardListener;


public class AddTranslationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_translation);
        final String word = getIntent().getStringExtra("word");
        final EditText wordView = (EditText) findViewById(R.id.word);
        final EditText translationView = (EditText) findViewById(R.id.translation);
        View footer = findViewById(R.id.llFooter);

        assert wordView != null;
        assert translationView != null;

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
                    Log.d(LOG_TAG, "submit");
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

    private static final String LOG_TAG = AddTranslationActivity.class.getName();
}
