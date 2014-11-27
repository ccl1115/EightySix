package com.utree.eightysix.rest;

import com.loopj.android.http.BuildConfig;
import com.utree.eightysix.Account;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 * @author simon
 */
public class RequestData {
  String api;
  boolean cache;
  boolean log;
  RequestParams params;
  int method;
  org.apache.http.Header[] headers;
  long requestTime;
  String host;

  boolean sign;

  public RequestData(Object request) {
    requestTime = System.currentTimeMillis();
    Class<?> clz = request.getClass();

    List<Header> headers = new ArrayList<Header>();

    try {
      api = clz.getAnnotation(Api.class).value();
      params = new RequestParams();

      Cache cache = clz.getAnnotation(Cache.class);
      this.cache = cache != null;

      com.utree.eightysix.rest.Log log = clz.getAnnotation(com.utree.eightysix.rest.Log.class);
      this.log = log != null;

      Token token = clz.getAnnotation(Token.class);
      if (token != null && Account.inst().isLogin()) {
        params.add("userId", Account.inst().getUserId());
        params.add("token", Account.inst().getToken());
      }

      Method method = clz.getAnnotation(Method.class);
      if (method != null) {
        this.method = method.value();
      } else {
        this.method = Method.POST;
      }

      Host host = clz.getAnnotation(Host.class);
      if (host != null) {
        this.host = host.value();
      }

      Sign sign = clz.getAnnotation(Sign.class);
      this.sign = sign != null;

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
            params.put(p.value(), value);
          } else if (value instanceof File) {
            params.put(p.value(), (File) value);
          } else if (value instanceof InputStream) {
            params.put(p.value(), (InputStream) value);
          } else {
            params.put(p.value(), String.valueOf(value));
          }
        }

        com.utree.eightysix.rest.Header h = f.getAnnotation(com.utree.eightysix.rest.Header.class);

        if (h != null) {
          headers.add(new BasicHeader(h.value(), (String) f.get(request)));
        }
      }

      if (headers.size() > 0) {
        setHeaders(new Header[headers.size()]);
        headers.toArray(getHeaders());
      }
    } catch (Throwable t) {
      if (BuildConfig.DEBUG) {
        throw new IllegalArgumentException("Request object parse failed", t);
      }
    }

  }

  public boolean isLog() {
    return log;
  }

  public void setLog(boolean log) {
    this.log = log;
  }

  public long getRequestTime() {
    return requestTime;
  }

  public void setRequestTime(long requestTime) {
    this.requestTime = requestTime;
  }

  public boolean isCache() {
    return cache;
  }

  public void setCache(boolean cache) {
    this.cache = cache;
  }

  public String getApi() {
    return api;
  }

  public void setApi(String api) {
    this.api = api;
  }

  public boolean needCache() {
    return cache;
  }

  public RequestParams getParams() {
    return params;
  }

  public void setParams(RequestParams params) {
    this.params = params;
  }

  public int getMethod() {
    return method;
  }

  public void setMethod(int method) {
    this.method = method;
  }

  public Header[] getHeaders() {
    return headers;
  }

  public void setHeaders(Header[] headers) {
    this.headers = headers;
  }

  public void setHost(String host) {
    this.host = host;
  }

  public String getHost() {
    return host;
  }

  public boolean needSign() {
    return sign;
  }

  public void setSign(boolean need) {
    this.sign = need;
  }
}
