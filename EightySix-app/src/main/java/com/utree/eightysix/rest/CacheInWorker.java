package com.utree.eightysix.rest;

import android.os.AsyncTask;
import com.jakewharton.disklrucache.DiskLruCache;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * @author simon
 */
public class CacheInWorker extends AsyncTask<Void, Void, Void> {

  private String mKey;
  private InputStream mInputStream;
  private String mValue;

  public CacheInWorker(String key, InputStream inputStream) {
    mKey = key;
    mInputStream = inputStream;
  }

  public CacheInWorker(String key, String string) {
    mKey = key;
    mValue = string;
  }

  @Override
  protected Void doInBackground(Void... voids) {
    if (U.getApiCache() == null) return null;

    if (mInputStream != null) {
      cacheInputStream();
    } else if (mValue != null) {
      cacheInString();
    }
    return null;
  }

  private void cacheInString() {
    DiskLruCache.Editor edit = null;
    try {
      edit = U.getApiCache().edit(mKey);
      edit.set(0, mValue);
    } catch (IOException e) {
      U.getAnalyser().reportException(U.getContext(), e);
    } finally {
      if (edit != null) {
        try {
          edit.commit();
        } catch (IOException ignored) {
        }
      }
    }
  }

  private void cacheInputStream() {
    DiskLruCache.Editor edit = null;
    OutputStream os = null;
    try {
      edit = U.getApiCache().edit(mKey);
      os = edit.newOutputStream(0);
      IOUtils.copyFile(mInputStream, os);
    } catch (IOException e) {
      U.getAnalyser().reportException(U.getContext(), e);
    } finally {

      if (os != null) {
        try {
          os.close();
        } catch (IOException ignored) {
        }
      }

      if (edit != null) {
        try {
          edit.commit();
        } catch (IOException ignored) {
        }
      }

      try {
        mInputStream.close();
      } catch (IOException ignored) {
      }
    }
  }
}
