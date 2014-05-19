package com.utree.eightysix;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 */
public class FileUtils {

    public static void copyFile(InputStream in, OutputStream os) {
        final byte[] buffer = new byte[1024 * 1024];
        int n;

        try {
            while((n = in.read(buffer)) != -1) {
                os.write(buffer, 0, n);
            }
        } catch (IOException e) {
            U.getAnalyser().reportException(U.getContext(), e);
        }
    }
}
