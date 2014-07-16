package com.utree.eightysix.rest;

import android.os.AsyncTask;
import com.jakewharton.disklrucache.DiskLruCache;
import com.utree.eightysix.U;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * @author simon
 */
public class CacheOutWorker<T extends Response> extends AsyncTask<Void, Void, T> {

  private String mKey;
  private Class<T> mClz;
  private OnResponse<T> mOnResponse;

  public CacheOutWorker(String key, OnResponse<T> onResponse, Class<T> clz) {
    mKey = key;
    mClz = clz;
    mOnResponse = onResponse;
  }

  @Override
  protected T doInBackground(Void... voids) {
    DiskLruCache.Snapshot snapshot = null;
    InputStream is = null;
    try {
      snapshot = U.getApiCache().get(mKey);
      if (snapshot != null)  {
        is = snapshot.getInputStream(0);
        return U.getGson().fromJson(new InputStreamReader(is), mClz);
      } else {
        return null;
      }
    } catch (IOException e) {
      U.getAnalyser().reportException(U.getContext(), e);
    } finally {
      if (is != null) {
        try {
          is.close();
        } catch (IOException e) {
          e.printStackTrace();
        }
      }

      if (snapshot != null) snapshot.close();
    }
    return null;
  }

  @Override
  protected void onPostExecute(T t) {
    try {
      mOnResponse.onResponse(t);
    } catch (Exception e) {
      if (mOnResponse instanceof OnResponse2) {
        ((OnResponse2) mOnResponse).onResponseError(e);
      }
    }
  }
}
