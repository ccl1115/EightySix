package com.utree.eightysix.rest;

import com.loopj.android.http.BuildConfig;
import com.utree.eightysix.Account;
import org.apache.http.Header;
import org.apache.http.message.BasicHeader;

import java.io.File;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.util.*;

/**
 * @author simon
 */
public class RequestData<RES extends Response> {
  String api;
  boolean cache;
  boolean log;
  RequestParams params;
  int method;
  org.apache.http.Header[] headers;
  long requestTime;
  String host;

  Class<RES> resClz;

  boolean sign;

  public RequestData() {
    requestTime = System.currentTimeMillis();
  }

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

  public Class<RES> getResClz() {
    return resClz;
  }

  public void setResClz(Class<RES> resClz) {
    this.resClz = resClz;
  }

  @Override
  public boolean equals(Object o) {
    if (this == o) return true;
    if (o == null || getClass() != o.getClass()) return false;

    RequestData that = (RequestData) o;

    if (cache != that.cache) return false;
    if (log != that.log) return false;
    if (method != that.method) return false;
    if (requestTime != that.requestTime) return false;
    if (sign != that.sign) return false;
    if (api != null ? !api.equals(that.api) : that.api != null) return false;
    if (!Arrays.equals(headers, that.headers)) return false;
    if (host != null ? !host.equals(that.host) : that.host != null) return false;
    if (params != null ? !params.equals(that.params) : that.params != null) return false;
    if (resClz != null ? !resClz.equals(that.resClz) : that.resClz != null) return false;

    return true;
  }

  @Override
  public int hashCode() {
    int result = api != null ? api.hashCode() : 0;
    result = 31 * result + (cache ? 1 : 0);
    result = 31 * result + (log ? 1 : 0);
    result = 31 * result + (params != null ? params.hashCode() : 0);
    result = 31 * result + method;
    result = 31 * result + (headers != null ? Arrays.hashCode(headers) : 0);
    result = 31 * result + (int) (requestTime ^ (requestTime >>> 32));
    result = 31 * result + (host != null ? host.hashCode() : 0);
    result = 31 * result + (resClz != null ? resClz.hashCode() : 0);
    result = 31 * result + (sign ? 1 : 0);
    return result;
  }
}
