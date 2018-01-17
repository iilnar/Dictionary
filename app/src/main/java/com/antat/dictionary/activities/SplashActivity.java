package com.antat.dictionary.activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;

/**
 * Created by Ilnar on 17/05/2017.
 */

public class SplashActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Intent intent = new Intent(this, MainActivity.class);

        Intent splashIntent = getIntent();
        String action = splashIntent.getAction();
        String type = splashIntent.getType();

        if (Intent.ACTION_SEND.equals(action)) {
            if ("text/plain".equals(type)) {
                intent.setAction("MY_ACTION");
                intent.setType("text/plain");
                intent.putExtra(Intent.EXTRA_TEXT, splashIntent.getStringExtra(Intent.EXTRA_TEXT));
            }
        }

        startActivity(intent);
        finish();
    }

    private static final String TAG = "SplashActivity";
}
