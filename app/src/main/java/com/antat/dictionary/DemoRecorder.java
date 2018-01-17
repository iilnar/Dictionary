package com.antat.dictionary;

/**
 * Created by Ilnar on 14/02/2017.
 */

public abstract class DemoRecorder {
    public abstract void onDone(String result);

    public void stop() {
        onDone("машина");
    }
}
