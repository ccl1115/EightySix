package com.utree.eightysix.utils;

import android.os.Environment;
import com.jakewharton.disklrucache.DiskLruCache;
import com.utree.eightysix.U;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * Get cache instance from U
 *
 * @see com.utree.eightysix.U#getApiCache()
 */
public class CacheUtils {

    private static CacheUtils sInst;

    private Map<String, DiskLruCache> mDiskLruCache = new HashMap<String, DiskLruCache>();

    public static CacheUtils inst() {
        if (sInst == null) {
            sInst = new CacheUtils();
        }
        return sInst;
    }

    private CacheUtils() {}

    public DiskLruCache getCache(String dir, int version, int count, long size) {
        DiskLruCache cache = mDiskLruCache.get(dir);
        if (cache == null) {
            try {
                cache = DiskLruCache.open(getOrCreateCacheDir(dir), version, count, size);
                mDiskLruCache.put(dir, cache);
            } catch (IOException e) {
                U.getAnalyser().reportException(U.getContext(), e);
            }
        }
        return cache;
    }

    private File getOrCreateCacheDir(String key) {
        final String path;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = U.getContext().getExternalFilesDir(null).getAbsolutePath() + File.separator + key;
        } else {
            path = U.getContext().getFilesDir().getAbsolutePath() + File.separator + key;
        }

        File file = new File(path);
        if (file.exists()) {
            if (file.isFile()) {
                if (file.delete()) {
                    if (file.mkdirs()) return file;
                }
            } else if (file.isDirectory()) return file;
        } else {
            if (file.mkdirs()) return file;
        }
        return null;
    }

}
