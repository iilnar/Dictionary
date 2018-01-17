package com.antat.dictionary.Util;

import android.support.annotation.NonNull;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;

/**
 * Created by Ilnar on 06/09/2017.
 */

public class XoredFileInputStream extends FileInputStream {
    public static final byte XOR_VALUE = 49;

    public XoredFileInputStream(@NonNull String name) throws FileNotFoundException {
        super(name);
    }

    public XoredFileInputStream(@NonNull File file) throws FileNotFoundException {
        super(file);
    }

    public XoredFileInputStream(@NonNull FileDescriptor fdObj) {
        super(fdObj);
    }

    public int read(byte b[], int off, int len) throws IOException {
        int res = super.read(b, off, len);
        if (res > 0) {
            for (int i = 0; i < res; i++) {
                b[i] ^= XOR_VALUE;
            }
        }
        return res;
    }
}
