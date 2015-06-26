package com.utree.eightysix.rest;

import android.os.Build;
import com.baidu.android.common.util.CommonParam;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.ResponseHandlerInterface;
import com.utree.eightysix.*;
import com.utree.eightysix.utils.Env;
import com.utree.eightysix.utils.MD5Util;
import de.akquinet.android.androlog.Log;
import org.apache.http.Header;
import org.apache.http.params.HttpProtocolParams;

/**
 */
public class RESTRequester implements IRESTRequester {

  private AsyncHttpClient mAsyncHttpClient;

  private RequestSchema mRequestSchema;

  private String mHost;
  private String mSecondHost;

  public RESTRequester(String host, String secondHost) {
    mHost = host;
    mAsyncHttpClient = new AsyncHttpClient();
    mAsyncHttpClient.setTimeout(U.getConfigInt("api.timeout"));
    mAsyncHttpClient.setMaxRetriesAndTimeout(U.getConfigInt("api.retry"), U.getConfigInt("api.retry.timeout"));
    mAsyncHttpClient.setUserAgent("EightySix/" + C.VERSION + " Android/" + Build.VERSION.SDK_INT);

    mRequestSchema = new RequestSchema();
    mSecondHost = secondHost;
    mRequestSchema.load(U.getContext(), mSecondHost, R.raw.request_schema_second);
    mRequestSchema.load(U.getContext(), host, R.raw.request_schema_new);

    compact();
  }

  public RESTRequester(String host, String secondHost, AsyncHttpClient client) {
    mHost = host;
    mAsyncHttpClient = client;
    mAsyncHttpClient.setTimeout(U.getConfigInt("api.timeout"));
    mAsyncHttpClient.setMaxRetriesAndTimeout(U.getConfigInt("api.retry"), U.getConfigInt("api.retry.timeout"));
    mAsyncHttpClient.setUserAgent("EightySix/" + C.VERSION + " Android/" + Build.VERSION.SDK_INT);

    mRequestSchema = new RequestSchema();
    mSecondHost = secondHost;
    mRequestSchema.load(U.getContext(), secondHost, R.raw.request_schema_second);
    mRequestSchema.load(U.getContext(), host, R.raw.request_schema_new);
    compact();
  }

  public static String genCacheKey(String api, RequestParams params) {
    return MD5Util.getMD5String((api + params.toString() + Account.inst().getUserId()).getBytes()).toLowerCase();
  }

  public static boolean responseOk(Response response) {
    return response != null && response.code == 0;
  }

  @Override
  public String getHost() {
    return mHost;
  }

  @Override
  public AsyncHttpClient getClient() {
    return mAsyncHttpClient;
  }

  @Override
  public RequestHandle request(Object request, ResponseHandlerInterface handler) {
    RequestData data = new RequestData(request);
    return request(data, handler);
  }

  @Override
  public void setHost(String host) {
    mHost = host;
  }

  @Override
  public void setSecondHost(String host) {
    mSecondHost = host;
  }

  @Override
  public RequestHandle request(RequestData data, ResponseHandlerInterface handler) {
    putBaseParams(data.getParams());
    if (data.isToken()) {
      addAuthParams(data.getParams());
    }
    if (data.needSign()) {
      data.getParams().sign();
    }
    if (data.getMethod() == Method.GET) {
      if (data.getHost() != null) {
        return get(data.getHost(), data.getApi(), data.getHeaders(), data.getParams(), handler);
      } else {
        return get(data.getApi(), data.getHeaders(), data.getParams(), handler);
      }
    } else if (data.getMethod() == Method.POST) {
      if (data.getHost() != null) {
        return post(data.getHost(), data.getApi(), data.getHeaders(), data.getParams(), null, handler);
      } else {
        return post(data.getApi(), data.getHeaders(), data.getParams(), null, handler);
      }
    }
    return null;
  }

  @Override
  public <T extends Response> RequestHandle request(Object request, OnResponse<T> onResponse, Class<T> clz) {
    RequestData data = new RequestData(request);
    return request(data, new HandlerWrapper<T>(data, onResponse, clz));
  }

  @Override
  public <T extends Response> RequestHandle request(String requestSchemaId, OnResponse<T> onResponse, Class<T> clz, Object... params) {
    RequestData request = mRequestSchema.getRequest(requestSchemaId, params);
    return request(request, new HandlerWrapper<T>(request, onResponse, clz));
  }

  @Override
  public RequestData convert(Object request) {
    throw new RuntimeException("Do not call this method");
  }

  @Override
  public RequestHandle get(String api, Header[] headers, RequestParams params, ResponseHandlerInterface handler) {
    if (BuildConfig.DEBUG) Log.d(C.TAG.RR, "   get: " + mHost + api);
    if (BuildConfig.DEBUG) Log.d(C.TAG.RR, "params: " + params.toString());
    return mAsyncHttpClient.get(U.getContext(), mHost + api, headers, params, handler);
  }

  @Override
  public RequestHandle post(String api, Header[] headers, RequestParams params, String contentType, ResponseHandlerInterface handler) {
    if (BuildConfig.DEBUG) Log.d(C.TAG.RR, "  post: " + mHost + api);
    if (BuildConfig.DEBUG) Log.d(C.TAG.RR, "params: " + params.toString());
    return mAsyncHttpClient.post(U.getContext(), mHost + api, headers, params, contentType, handler);
  }

  private RequestHandle post(String host, String path, Header[] headers, RequestParams params, String contentType, ResponseHandlerInterface handler) {
    if (BuildConfig.DEBUG) Log.d(C.TAG.RR, "  post: " + host + path);
    if (BuildConfig.DEBUG) Log.d(C.TAG.RR, "params: " + params.toString());
    return mAsyncHttpClient.post(U.getContext(), host + path, headers, params, contentType, handler);
  }

  private RequestHandle get(String host, String path, Header[] headers, RequestParams params, ResponseHandlerInterface handler) {
    if (BuildConfig.DEBUG) Log.d(C.TAG.RR, "   get: " + host + path);
    if (BuildConfig.DEBUG) Log.d(C.TAG.RR, "params: " + params.toString());
    return mAsyncHttpClient.get(U.getContext(), host + path, headers, params, handler);
  }

  @Override
  public RequestParams addAuthParams(RequestParams params) {
    if (params == null) params = new RequestParams();
    params.add("userId", Account.inst().getUserId());
    params.add("token", Account.inst().getToken());
    return params;
  }

  private void putBaseParams(RequestParams params) {
    if (params == null) {
      params = new RequestParams();
    }

    params.add("os", "android");
    params.add("os_version", String.valueOf(Build.VERSION.SDK_INT));
    params.add("device", Build.DEVICE);
    params.add("model", Build.MODEL);
    params.add("manufacturer", Build.MANUFACTURER);
    params.add("imei", Env.getImei());
    params.add("version", String.valueOf(C.VERSION));
    params.add("channel", U.getConfig("app.channel"));
    params.add("lat", Env.getLastLatitude());
    params.add("lon", Env.getLastLongitude());
    params.add("cityName", Env.getLastCity());
    params.add("district", Env.getLastDistrict());

    params.add("cuid", CommonParam.getCUID(U.getContext()));

    String pushChannelId = Env.getPushChannelId();
    if (pushChannelId != null) {
      params.add("push_channelid", pushChannelId);
    }

    String pushUserId = Env.getPushUserId();
    if (pushUserId != null) {
      params.add("push_userid", pushUserId);
    }
  }

  private void addSign(RequestParams params) {
    params.add("sign", MD5Util.getMD5String(params.toString().getBytes()));
  }

  private void compact() {
    HttpProtocolParams.setUseExpectContinue(mAsyncHttpClient.getHttpClient().getParams(), false);
  }
}
