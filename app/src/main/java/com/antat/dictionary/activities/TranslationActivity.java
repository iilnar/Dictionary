package com.antat.dictionary.activities;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.antat.dictionary.R;
import com.antat.dictionary.dictionary.DictionaryRecord;

/**
 * Created by ilnar on 29.05.16.
 */
public class TranslationActivity extends AppCompatActivity {
    private DictionaryRecord dr;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_translation);

        Toolbar toolbar = (Toolbar) findViewById(R.id.translation_toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        Bundle b = getIntent().getExtras();
        dr = (DictionaryRecord) b.getSerializable("entry");

        TextView wordView = (TextView) findViewById(R.id.word);
        TextView translationView = (TextView) findViewById(R.id.translation);
        TextView examplesTitleView = (TextView) findViewById(R.id.examples_title);
        TextView examplesView = (TextView) findViewById(R.id.examples);

        if (translationView != null && wordView != null) {
            wordView.setText(dr.getWord());
            translationView.setText(joinArray(dr.getTranslation()));

            translationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager) getSystemService(CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(TAG, dictionaryRecordToString()));
                    Toast.makeText(getApplicationContext(), getString(R.string.copied_to_clipboard), Toast.LENGTH_SHORT).show();
                }
            });
        }

        if (dr.getExamples().length > 0) {
            examplesView.setText(joinArray(dr.getExamples()));
            examplesTitleView.setVisibility(View.VISIBLE);
            examplesView.setVisibility(View.VISIBLE);
        } else {
            examplesTitleView.setVisibility(View.INVISIBLE);
            examplesView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.translation_menu, menu);

        MenuItem shareButton = menu.findItem(R.id.menu_share);
        shareButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, dictionaryRecordToString());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, getString(R.string.share)));
                return true;
            }
        });

        MenuItem editTranslation = menu.findItem(R.id.add_translation);
        editTranslation.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent intent = new Intent(TranslationActivity.this, AddTranslationActivity.class);
                intent.putExtra(DictionaryRecord.WORD, dr.getWord());
                startActivity(intent);
                return true;
            }
        });

        return true;
    }

    private String joinArray(String[] strings) {
        final StringBuilder result = new StringBuilder();
        for (int i = 0; i < strings.length; i++) {
            result.append(String.format("%d) %s\n", i + 1, strings[i]));
        }
        return result.toString();
    }

    private String dictionaryRecordToString() {
        String res = String.format("%s\n%s", dr.getWord(), joinArray(dr.getTranslation()));
        if (dr.getExamples().length > 0) {
            res += String.format("\n%s\n%s", getString(R.string.examples_title), joinArray(dr.getExamples()));
        }
        return res;
    }

    private static final String TAG = "TranslationActivity";
}
