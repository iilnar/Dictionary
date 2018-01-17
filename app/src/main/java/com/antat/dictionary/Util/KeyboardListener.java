package com.antat.dictionary.Util;

import android.content.Context;
import android.graphics.Rect;
import android.view.View;
import android.view.ViewTreeObserver;
import android.widget.Button;
import android.widget.EditText;

import com.antat.dictionary.R;

/**
 * Created by ilnar on 05.06.16.
 */
public class KeyboardListener implements ViewTreeObserver.OnGlobalLayoutListener {
    private final View footer;
    private final EditText[] searchViews;

    public KeyboardListener(View footer, final EditText ... searchViews) {
        this.searchViews = searchViews;
        this.footer = footer;
        int[] buttonsId = {R.id.button1, R.id.button2, R.id.button3, R.id.button4, R.id.button5, R.id.button6};
        for (int id : buttonsId) {
            final Button b1 = (Button) footer.findViewById(id);
            b1.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    for (EditText text : searchViews) {
                        if (text.hasFocus()) {
                            text.getText().insert(text.getSelectionStart(), b1.getText());
                        }
                    }
                }
            });
        }
    }

    @Override
    public void onGlobalLayout() {
        boolean showKeyboard = searchViews[0].getContext().getSharedPreferences("preferences", Context.MODE_PRIVATE).getBoolean("showKeyboard", true);
        Rect r = new Rect();
        searchViews[0].getWindowVisibleDisplayFrame(r);
        int screenHeight = searchViews[0].getRootView().getHeight();
        int keypadHeight = screenHeight - r.bottom;
        if (showKeyboard && keypadHeight > screenHeight * 0.15) {
            footer.setVisibility(View.VISIBLE);
        } else {
            footer.setVisibility(View.INVISIBLE);
        }
    }

    private static final String TAG = "KeyboardListener";
}