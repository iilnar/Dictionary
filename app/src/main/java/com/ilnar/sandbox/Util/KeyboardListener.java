package com.ilnar.sandbox.Util;

import android.content.Context;
import android.graphics.Rect;
import android.util.Log;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

import com.ilnar.sandbox.R;

/**
 * Created by ilnar on 05.06.16.
 */
public class KeyboardListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private final View footer;
    private final EditText[] searchViews;

    public KeyboardListener(View footer, EditText ... searchViews) {
        this.searchViews = searchViews;
        this.footer = footer;
    }

    @Override
    public void onGlobalLayout() {
        boolean showKeyboard = searchViews[0].getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE).getBoolean("showKeyboard", true);
        Rect r = new Rect();
        searchViews[0].getWindowVisibleDisplayFrame(r);
        int screenHeight = searchViews[0].getRootView().getHeight();
        int keypadHeight = screenHeight - r.bottom;
        Log.d(LOG_TAG, "keypad height " + keypadHeight);
        if (showKeyboard && keypadHeight > screenHeight * 0.15) {
            Log.d(LOG_TAG, "opened");
            footer.setVisibility(View.VISIBLE);
            int[] buttonsId = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6};
            for (int id : buttonsId) {
                final Button b1 = (Button) footer.findViewById(id);
                b1.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        for (EditText text : searchViews) {
                            if (text.hasFocus()) {
                                Log.d(LOG_TAG, text.getId() + " has focus");
                                text.getText().insert(text.getSelectionStart(), b1.getText());
                            }
                        }
                    }
                });
            }
        } else {
            Log.d(LOG_TAG, "closed");
            footer.setVisibility(View.INVISIBLE);
        }
    }

    private static final String LOG_TAG = KeyboardListener.class.getName();
}