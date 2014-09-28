package com.utree.eightysix.rest;

import android.widget.Toast;
import com.google.gson.Gson;
import com.loopj.android.http.BaseJsonHttpResponseHandler;
import com.tencent.stat.StatAppMonitor;
import com.utree.eightysix.*;
import com.utree.eightysix.utils.IOUtils;
import de.akquinet.android.androlog.Log;
import org.apache.http.HttpStatus;
import org.apache.http.NoHttpResponseException;

import java.io.File;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.util.Date;

/**
 * Wrapper of handler use to parse response data using Gson and cache automatically
 * <p/>
 * And it will do error handling and cache automation.
 */
public class HandlerWrapper<T extends Response> extends BaseJsonHttpResponseHandler<T> {

  private OnResponse<T> mOnResponse;
  private RequestData mRequestData;
  private Class<T> mClz;
  private Gson mGson;

  private StatAppMonitor mStatAppMonitor;

  /**
   * No cache constructor
   *
   * @param data    the object represents the request
   * @param onResponse the callback
   */
  public HandlerWrapper(RequestData data, OnResponse<T> onResponse, Class<T> clz) {
    mOnResponse = onResponse;
    mRequestData = data;
    mClz = clz;
  }

  public HandlerWrapper(RequestData data, OnResponse<T> onResponse, Class<T> clz, Gson customGson) {
    this(data, onResponse, clz);
    mGson = customGson;
  }

  @Override
  public void onStart() {
    super.onStart();
    Log.d(C.TAG.RR, "request starting:" + mRequestData.getApi());
  }

  @Override
  public void onSuccess(int statusCode, org.apache.http.Header[] headers, String rawResponse, T response) {

    mStatAppMonitor = new StatAppMonitor(mRequestData.getApi());

    mStatAppMonitor.setMillisecondsConsume(System.currentTimeMillis() - mRequestData.getRequestTime());

    mStatAppMonitor.setRespSize(rawResponse.length() * 2);
    mStatAppMonitor.setReqSize(mRequestData.getParams().toString().length() * 2);

    if (response != null) {
      handleObjectError(response);

      if (mRequestData.needCache()) {
        new CacheInWorker(RESTRequester.genCacheKey(mRequestData.getApi(), mRequestData.getParams()),
            rawResponse).execute();
      }
    }

    if (statusCode > HttpStatus.SC_MULTIPLE_CHOICES) {
      if (BuildConfig.DEBUG) {
        Toast.makeText(U.getContext(), "HttpStatus: " + statusCode, Toast.LENGTH_SHORT).show();
      }
      mStatAppMonitor.setReturnCode(statusCode);
      mStatAppMonitor.setResultType(StatAppMonitor.FAILURE_RESULT_TYPE);
    }
    try {
      mOnResponse.onResponse(response);
    } catch (Throwable t) {
      if (mOnResponse instanceof OnResponse2) {
        ((OnResponse2) mOnResponse).onResponseError(t);
      }
    }

    U.getAnalyser().reportHttpRequest(U.getContext(), mStatAppMonitor);
  }

  @Override
  public void onProgress(int bytesWritten, int totalSize) {
  }

  @Override
  public void onFailure(int statusCode, org.apache.http.Header[] headers, Throwable e, String rawData, T errorResponse) {

    mStatAppMonitor = new StatAppMonitor(mRequestData.getApi());

    mStatAppMonitor.setMillisecondsConsume(System.currentTimeMillis() - mRequestData.getRequestTime());

    if (rawData != null) {
      mStatAppMonitor.setRespSize(rawData.length() * 2);
    } else {
      mStatAppMonitor.setRespSize(0);
    }
    mStatAppMonitor.setReqSize(mRequestData.getParams().toString().length() * 2);
    mStatAppMonitor.setResultType(StatAppMonitor.FAILURE_RESULT_TYPE);

    if (e != null) {
      if (!(e instanceof NoHttpResponseException)) {
        U.showToast(U.getContext().getString(R.string.server_connection_exception));
      }
      if (BuildConfig.DEBUG) {
        File tmp = IOUtils.createTmpFile(
            String.format("server_error_%d_%d", statusCode, System.currentTimeMillis()));
        PrintWriter writer = null;
        try {

          writer = new PrintWriter(tmp);

          writer.write(DateFormat.getDateTimeInstance(DateFormat.FULL, DateFormat.FULL).format(new Date()));
          writer.write("\nAPI: " + mRequestData.getApi());
          writer.write("\nParams: " + mRequestData.getParams().toString());
          writer.write("\n\n");
          e.printStackTrace(writer);
          writer.write("\n\n");
          writer.write(rawData);
        } catch (Exception ignored) {
        } finally {
          if (writer != null) {
            writer.close();
          }
        }

        e.printStackTrace();
      } else {
        U.getReporter().reportRequestError(mRequestData, e);
      }
    }

    if (statusCode > HttpStatus.SC_MULTIPLE_CHOICES) {
      if (BuildConfig.DEBUG) {
        Toast.makeText(U.getContext(), "HttpStatus: " + statusCode, Toast.LENGTH_SHORT).show();
      } else {
        U.showToast(U.gs(R.string.server_500));
        U.getReporter().reportRequestStatusCode(mRequestData, statusCode);
      }
      mStatAppMonitor.setReturnCode(statusCode);
    }
    try {
      mOnResponse.onResponse(null);
    } catch (Throwable t) {
      if (mOnResponse instanceof OnResponse2) {
        ((OnResponse2) mOnResponse).onResponseError(t);
      }
    }

    U.getAnalyser().reportHttpRequest(U.getContext(), mStatAppMonitor);
  }

  @Override
  protected T parseResponse(String responseBody, boolean b) throws Throwable {
    if (BuildConfig.DEBUG) Log.d(C.TAG.RR, "response: " + responseBody);

    if (mGson == null) {
      return U.getGson().fromJson(responseBody, mClz);
    } else {
      return mGson.fromJson(responseBody, mClz);
    }
  }


  private void handleObjectError(T response) {
    if (response.code != 0) {
      if ((response.code & 0xffff) == 0x1014 || (response.code & 0xffff) == 0x1025
          || (response.code & 0xffff) == 0x1024) {
        // 用户token失效，退出客户端
        Account.inst().logout();
      }

      switch (response.code & 0xf0000) {
        case 0x10000:
          // mta
          U.getReporter().reportRequestStatusCode(mRequestData, response.code);
          break;
        case 0x20000:
          // show
          if (BuildConfig.DEBUG) {
            U.showToast(String.format("%s(%h)", response.message, response.code));
          } else {
            U.showToast(response.message);
          }
          break;
        case 0x30000:
          // show + mta
          if (BuildConfig.DEBUG) {
            U.showToast(String.format("%s(%h)", response.message, response.code));
          } else {
            U.showToast(response.message);
          }
          U.getReporter().reportRequestStatusCode(mRequestData, response.code);
          break;
      }
    }
  }
}
