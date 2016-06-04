package com.ilnar.sandbox.activities;

import android.support.v4.app.NavUtils;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.ilnar.sandbox.R;

public class AddTranslationActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_translation);
        String word = getIntent().getStringExtra("word");
        TextView wordView = (TextView)findViewById(R.id.word);
        if (wordView != null) {
            wordView.setText(word);
        }
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
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
