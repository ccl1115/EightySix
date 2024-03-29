package com.utree.eightysix.utils;

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

  private CacheUtils() {
  }

  public static CacheUtils inst() {
    if (sInst == null) {
      sInst = new CacheUtils();
    }
    return sInst;
  }

  public DiskLruCache getCache(String dir, int version, int count, long size) {
    DiskLruCache cache = mDiskLruCache.get(dir);
    if (cache == null) {
      try {
        cache = DiskLruCache.open(getOrCreateCacheDir(dir), version, count, size);
        mDiskLruCache.put(dir, cache);
      } catch (Exception e) {
        U.getAnalyser().reportError(U.getContext(), "open disk lru cache failed");
      }
    } else if (cache.isClosed()) {
      try {
        cache = DiskLruCache.open(getOrCreateCacheDir(dir), version, count, size);
        mDiskLruCache.put(dir, cache);
      } catch (IOException e) {
        U.getAnalyser().reportError(U.getContext(), "open disk lru cache failed");
      }
    }
    return cache;
  }

  private File getOrCreateCacheDir(String key) {
    return new File(IOUtils.getAvailableAppDir().getAbsolutePath() + File.separator + key);
  }

}
