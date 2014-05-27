package com.utree.eightysix;

import android.os.Environment;
import com.jakewharton.disklrucache.DiskLruCache;
import java.io.File;
import java.io.IOException;

/**
 * Get cache instance from U
 *
 * @see com.utree.eightysix.U#getApiCache()
 */
class CacheUtils {

    private static CacheUtils sInst;

    private DiskLruCache mDiskLruCache;

    private File mApiCacheDir;

    static CacheUtils inst() {
        if (sInst == null) {
            sInst = new CacheUtils();
        }
        return sInst;
    }

    private CacheUtils() {}

    DiskLruCache getApiCache() {
        if (mDiskLruCache == null) {
            try {
                mDiskLruCache = DiskLruCache.open(getOrCreateApiCacheDir(),
                        U.getConfigInt("cache.api.version"),
                        U.getConfigInt("cache.api.count"),
                        U.getConfigInt("cache.api.size"));
            } catch (IOException e) {
                U.getAnalyser().reportException(U.getContext(), e);
            }
        }
        return mDiskLruCache;
    }

    private File getOrCreateApiCacheDir() {
        final String path;
        if (Environment.MEDIA_MOUNTED.equals(Environment.getExternalStorageState())) {
            path = U.getContext().getExternalFilesDir(null).getAbsolutePath() + File.separator + U.getConfig("cache.api.dir");
        } else {
            path = U.getContext().getFilesDir().getAbsolutePath() + File.separator + U.getConfig("cache.api.dir");
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
