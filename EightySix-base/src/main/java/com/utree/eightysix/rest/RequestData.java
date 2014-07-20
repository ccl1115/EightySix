package com.utree.eightysix.rest;

import com.loopj.android.http.RequestParams;
import org.apache.http.Header;

/**
* @author simon
*/
public class RequestData {
  String api;
  boolean cache;
  RequestParams params;
  int method;
  org.apache.http.Header[] headers;

  public String getApi() {
    return api;
  }

  public boolean needCache() {
    return cache;
  }

  public RequestParams getParams() {
    return params;
  }

  public int getMethod() {
    return method;
  }

  public Header[] getHeaders() {
    return headers;
  }
}
