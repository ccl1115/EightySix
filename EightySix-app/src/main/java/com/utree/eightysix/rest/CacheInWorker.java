package com.utree.eightysix.rest;

import android.os.AsyncTask;
import com.jakewharton.disklrucache.DiskLruCache;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.IOUtils;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;

/**
 * @author simon
 */
public class CacheInWorker extends AsyncTask<Void, Void, Void> {

  private String mKey;
  private InputStream mInputStream;

  public CacheInWorker(String key, InputStream inputStream) {
    mKey = key;
    mInputStream = inputStream;
  }

  @Override
  protected Void doInBackground(Void... voids) {
    DiskLruCache.Editor edit = null;
    OutputStream os = null;
    try {
      edit = U.getApiCache().edit(mKey);
      os = edit.newOutputStream(0);
      IOUtils.copyFile(mInputStream, os);
    } catch (IOException e) {
      U.getAnalyser().reportException(U.getContext(), e);
    } finally {

      try {
        if (os != null) {
          os.close();
        }
      } catch (IOException ignored) {
      }

      try {
        if (edit != null)
          edit.commit();
      } catch (IOException ignored) {
      }

      try {
        mInputStream.close();
      } catch (IOException ignored) {
      }
    }
    return null;
  }
}
