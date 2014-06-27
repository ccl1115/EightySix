package com.utree.eightysix.rest;

import android.widget.Toast;
import com.jakewharton.disklrucache.DiskLruCache;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.utree.eightysix.BuildConfig;
import com.utree.eightysix.C;
import com.utree.eightysix.R;
import com.utree.eightysix.U;
import de.akquinet.android.androlog.Log;
import java.io.IOException;
import java.net.ConnectException;
import org.apache.http.HttpStatus;

/**
 * Wrapper of handler use to parse response data using Gson and cache automatically
 * <p/>
 * And it will do error handling and cache automation.
 */
public class HandlerWrapper<T extends Response> extends BaseJsonHttpResponseHandler<T> {

  private String mKey;
  private OnResponse<T> mOnResponse;
  private Object mRequest;
  private Class<T> mClz;

  /**
   * No cache constructor
   *
   * @param request    the object represents the reqeust
   * @param onResponse the callback
   */
  public HandlerWrapper(Object request, OnResponse<T> onResponse, Class<T> clz) {
    mOnResponse = onResponse;
    mRequest = request;
    mClz = clz;
  }

  /**
   * Cache response using the key
   *
   * @param key        the cache key
   * @param request    the object represents the request
   * @param onResponse the callback
   */
  public HandlerWrapper(String key, Object request, OnResponse<T> onResponse, Class<T> clz) {
    mKey = key;
    mOnResponse = onResponse;
    mRequest = request;
    mClz = clz;
  }

  @Override
  public void onSuccess(int statusCode, org.apache.http.Header[] headers, String rawResponse, T response) {
    if (response != null) {
      handleObjectError(response);

      Cache need = mRequest.getClass().getAnnotation(Cache.class);
      if (mKey != null && need != null) {
        try {
          DiskLruCache.Editor edit = U.getApiCache().edit(mKey);
          edit.set(0, rawResponse);
          edit.commit();
        } catch (IOException e) {
          U.getAnalyser().reportException(U.getContext(), e);
        }
      }
    }

    if (statusCode > HttpStatus.SC_MULTIPLE_CHOICES) {
      if (BuildConfig.DEBUG) {
        Toast.makeText(U.getContext(), "HttpStatus: " + statusCode, Toast.LENGTH_SHORT).show();
      }
    }
    mOnResponse.onResponse(response);
  }

  @Override
  public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, String rawData, T errorResponse) {
    if (e != null) {
      if (e instanceof ConnectException) {
        Toast.makeText(U.getContext(), U.gs(R.string.error_connect_exception), Toast.LENGTH_SHORT).show();
      }
      if (BuildConfig.DEBUG) {
        e.printStackTrace();
      }
    }

    if (statusCode > HttpStatus.SC_MULTIPLE_CHOICES) {
      if (BuildConfig.DEBUG) {
        Toast.makeText(U.getContext(), "HttpStatus: " + statusCode, Toast.LENGTH_SHORT).show();
      } else {
        U.showToast(U.gs(R.string.server_500));
      }
    }
    mOnResponse.onResponse(null);
  }

  @Override
  public T parseResponse(String responseBody) throws Throwable {
    Log.d(C.TAG.RR, "response: " + responseBody);
    return U.getGson().fromJson(responseBody, mClz);
  }

  private void handleObjectError(T response) {
    if (response.code != 0) {
      if (BuildConfig.DEBUG) {
        Toast.makeText(U.getContext(), String.format("%s(%h)", response.message, response.code), Toast.LENGTH_SHORT).show();
      } else {
        Toast.makeText(U.getContext(), response.message, Toast.LENGTH_SHORT).show();
      }
    }
  }
}
