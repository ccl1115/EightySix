package com.utree.eightysix.rest;

import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

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

  public RequestData() {
    requestTime = System.currentTimeMillis();
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
}
