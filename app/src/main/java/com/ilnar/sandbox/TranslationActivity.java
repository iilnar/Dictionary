package com.ilnar.sandbox;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.ShareActionProvider;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.logging.Logger;

/**
 * Created by ilnar on 29.05.16.
 */
public class TranslationActivity extends AppCompatActivity {
    private TextView translationView;
    private final static String LOG_TAG = TranslationActivity.class.getName();
    private ShareActionProvider shareActionProvider;

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
            translationView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    ClipboardManager clipboardManager = (ClipboardManager)getSystemService(CLIPBOARD_SERVICE);
                    clipboardManager.setPrimaryClip(ClipData.newPlainText(LOG_TAG, translationView.getText()));
                    Toast.makeText(getApplicationContext(), "Copied to clipboard", Toast.LENGTH_SHORT).show();
                    Log.d(LOG_TAG, "copied");
                }
            });
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        CharSequence text = translationView.getText();
        Log.d(LOG_TAG + "onCreateMenu", text.toString());
        getMenuInflater().inflate(R.menu.translation_menu, menu);

        MenuItem shareButton = menu.findItem(R.id.menu_share);
        shareButton.setOnMenuItemClickListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Intent shareIntent = new Intent();
                shareIntent.setAction(Intent.ACTION_SEND);
                shareIntent.putExtra(Intent.EXTRA_TEXT, translationView.getText().toString());
                shareIntent.setType("text/plain");
                startActivity(Intent.createChooser(shareIntent, "Send via"));
                return true;
            }
        });
        return true;
    }
}
