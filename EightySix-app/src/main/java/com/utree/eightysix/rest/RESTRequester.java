package com.utree.eightysix.rest;

import android.os.Build;
import com.aliyun.android.util.MD5Util;
import com.baidu.android.common.util.CommonParam;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.RequestHandle;
import com.loopj.android.http.RequestParams;
import com.loopj.android.http.ResponseHandlerInterface;
import com.utree.eightysix.Account;
import com.utree.eightysix.C;
import com.utree.eightysix.U;
import com.utree.eightysix.utils.Env;
import de.akquinet.android.androlog.Log;
import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

/**
 */
public class RESTRequester {

  private AsyncHttpClient mAsyncHttpClient;

  private String mHost;

  public RESTRequester(String host) {
    mHost = host;
    mAsyncHttpClient = new AsyncHttpClient();
    mAsyncHttpClient.setMaxConnections(U.getConfigInt("api.connections"));
    mAsyncHttpClient.setMaxRetriesAndTimeout(U.getConfigInt("api.retry"), U.getConfigInt("api.timeout"));
  }

  public RESTRequester(String host, int maxConnections) {
    mHost = host;
    mAsyncHttpClient = new AsyncHttpClient();
    mAsyncHttpClient.setMaxConnections(maxConnections);
    mAsyncHttpClient.setMaxRetriesAndTimeout(U.getConfigInt("api.retry"), U.getConfigInt("api.timeout"));
  }

  public RESTRequester(String host, int maxConnections, int retry, int timeout) {
    mHost = host;
    mAsyncHttpClient = new AsyncHttpClient();
    mAsyncHttpClient.setMaxConnections(maxConnections);
    mAsyncHttpClient.setMaxRetriesAndTimeout(retry, timeout);
  }

  public static String genCacheKey(String api, RequestParams params) {
    return MD5Util.getMD5String((api + params.toString()).getBytes()).toLowerCase();
  }

  public String getHost() {
    return mHost;
  }

  public AsyncHttpClient getClient() {
    return mAsyncHttpClient;
  }

  public RequestHandle request(Object request, ResponseHandlerInterface handler) {
    RequestData data = convert(request);
    return request(data, handler);
  }

  public RequestHandle request(RequestData data, ResponseHandlerInterface handler) {
    if (data.getMethod() == Method.GET) {
      return get(data.getApi(), data.getHeaders(), data.getParams(), handler);
    } else if (data.getMethod() == Method.POST) {
      return post(data.getApi(), data.getHeaders(), data.getParams(), null, handler);
    }
    return null;
  }

  public RequestData convert(Object request) {
    RequestData data = new RequestData();
    Class<?> clz = request.getClass();

    List<Header> headers = new ArrayList<Header>();

    try {
      data.api = clz.getAnnotation(Api.class).value();
      data.params = new RequestParams();

      Cache cache = clz.getAnnotation(Cache.class);
      data.cache = cache != null;

      Token token = clz.getAnnotation(Token.class);
      if (token != null && Account.inst().isLogin()) {
        addAuthParams(data.getParams());
      }

      Method method = clz.getAnnotation(Method.class);
      if (method != null) {
        data.method = method.value();
      } else {
        data.method = Method.POST;
      }

      for (Field f : clz.getFields()) {
        Param p = f.getAnnotation(Param.class);

        if (p != null) {
          Object value = f.get(request);
          if (value == null) {
            if (f.getAnnotation(Optional.class) == null) {
              throw new IllegalArgumentException("value is null, add @Optional");
            } else {
              continue;
            }
          }
          if (value instanceof List || value instanceof Set || value instanceof Map) {
            data.getParams().put(p.value(), value);
          } else if (value instanceof File) {
            data.getParams().put(p.value(), (File) value);
          } else if (value instanceof InputStream) {
            data.getParams().put(p.value(), (InputStream) value);
          } else {
            data.getParams().put(p.value(), String.valueOf(value));
          }
        }

        com.utree.eightysix.rest.Header h = f.getAnnotation(com.utree.eightysix.rest.Header.class);

        if (h != null) {
          headers.add(new BasicHeader(h.value(), (String) f.get(request)));
        }
      }

      if (headers.size() > 0) {
        data.headers = new Header[headers.size()];
        headers.toArray(data.getHeaders());
      }
    } catch (Throwable t) {
      U.getAnalyser().reportException(U.getContext(), t);
      throw new IllegalArgumentException("Request object parse failed", t);
    }

    return data;
  }

  public RequestHandle get(String api, Header[] headers, RequestParams params, ResponseHandlerInterface handler) {
    Log.d(C.TAG.RR, "   get: " + mHost + api);
    Log.d(C.TAG.RR, "params: " + params.toString());
    putBaseParams(params);
    return mAsyncHttpClient.get(U.getContext(), mHost + api, headers, params, handler);
  }

  public RequestHandle post(String api, Header[] headers, RequestParams params, String contentType, ResponseHandlerInterface handler) {
    Log.d(C.TAG.RR, "  post: " + mHost + api);
    Log.d(C.TAG.RR, "params: " + params.toString());
    putBaseParams(params);
    return mAsyncHttpClient.post(U.getContext(), mHost + api, headers, params, contentType, handler);
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

  public RequestParams addAuthParams(RequestParams params) {
    if (params == null) params = new RequestParams();
    params.add("userId", Account.inst().getUserId());
    params.add("token", Account.inst().getToken());
    return params;
  }
}
