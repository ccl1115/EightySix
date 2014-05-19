package com.utree.eightysix.storage;

import com.utree.eightysix.U;
import java.io.File;

/**
 */
public class StorageConfig {

    private static final String DEFAULT_GET_LOCAL_PATH = U.getContext().getFilesDir().getAbsolutePath()
            + File.separator + "storage" + File.separator;

    private static String sGetLocalPath = DEFAULT_GET_LOCAL_PATH;

    public static void setGetLocalPath(String path) {
        File file = new File(path);
        if (file.exists() && file.isDirectory()) {
            sGetLocalPath = path;
        }
    }

    public static String getGetLocalPath() {
        return sGetLocalPath;
    }
}
